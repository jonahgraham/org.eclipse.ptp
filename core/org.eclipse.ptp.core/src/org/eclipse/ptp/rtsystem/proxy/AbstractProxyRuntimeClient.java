/*******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California. 
 * This material was produced under U.S. Government contract W-7405-ENG-36 
 * for Los Alamos National Laboratory, which is operated by the University 
 * of California for the U.S. Department of Energy. The U.S. Government has 
 * rights to use, reproduce, and distribute this software. NEITHER THE 
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR 
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified 
 * to produce derivative works, such modified software should be clearly marked, 
 * so as not to confuse it with the version available from LANL.
 * 
 * Additionally, this program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * LA-CC 04-115
 *******************************************************************************/

package org.eclipse.ptp.rtsystem.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.proxy.AbstractProxyClient;
import org.eclipse.ptp.core.proxy.command.IProxyCommand;
import org.eclipse.ptp.core.proxy.event.IProxyConnectedEvent;
import org.eclipse.ptp.core.proxy.event.IProxyDisconnectedEvent;
import org.eclipse.ptp.core.proxy.event.IProxyErrorEvent;
import org.eclipse.ptp.core.proxy.event.IProxyEvent;
import org.eclipse.ptp.core.proxy.event.IProxyEventListener;
import org.eclipse.ptp.core.proxy.event.IProxyExtendedEvent;
import org.eclipse.ptp.core.proxy.event.IProxyOKEvent;
import org.eclipse.ptp.core.proxy.event.IProxyTimeoutEvent;
import org.eclipse.ptp.rtsystem.proxy.command.IProxyRuntimeCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeInitCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeModelDefCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeStartEventsCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeStopEventsCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeSubmitJobCommand;
import org.eclipse.ptp.rtsystem.proxy.command.ProxyRuntimeTerminateJobCommand;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeAttributeDefEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeConnectedStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeErrorEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeEventListener;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeJobChangeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeMachineChangeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNewJobEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNewMachineEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNewNodeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNewProcessEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNewQueueEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeNodeChangeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeProcessChangeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeQueueChangeEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeRunningStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeShutdownStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.ProxyRuntimeConnectedStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.ProxyRuntimeErrorEvent;
import org.eclipse.ptp.rtsystem.proxy.event.ProxyRuntimeEventFactory;
import org.eclipse.ptp.rtsystem.proxy.event.ProxyRuntimeRunningStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.ProxyRuntimeShutdownStateEvent;
import org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeErrorEvent.ErrorType;

public abstract class AbstractProxyRuntimeClient extends AbstractProxyClient implements IProxyRuntimeClient,IProxyEventListener {

	private class ProxyServerThread implements Runnable {
		private static final String name = "Proxy Server Thread";

		public void run() {
			String[] cmd = new String[2];
			cmd[0] = proxyPath;
			cmd[1] = "--port="+getSessionPort();
			if (logEvents)
				System.out.println("RUNNING PROXY SERVER COMMAND: '"+cmd[0]+" "+cmd[1]+"'");
			
			try {
				Process process = Runtime.getRuntime().exec(cmd);
				final BufferedReader err_reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				final BufferedReader out_reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				new Thread(new Runnable() {
					public void run() {
						try {
							String output;
							while ((output = out_reader.readLine()) != null) {
								if (proxyDebugOutput) System.out.println(proxyName + ": " + output);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, "Program output Thread").start();
				
				new Thread(new Runnable() {
					public void run() {
						try {
							String line;
							while ((line = err_reader.readLine()) != null) {
								if (proxyDebugOutput) System.err.println(proxyName + ": " + line);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, "Error output Thread").start();
			} catch(IOException e) {
				//PTPCorePlugin.log(e);
				e.printStackTrace();
			}
		}
	}

	private class StateMachineThread implements Runnable {
		private static final String name = "State Machine Thread";
		public void run() {
			try {
				runStateMachine();
			} catch (IllegalStateException e) {
				System.out.println("Illegal state detected: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (logEvents) System.out.println("state machine thread exited");
		}
	}
	/*
	 * Proxy state
	 */
	enum ProxyState {
		IDLE,
		STARTUP,
		INIT,
		MODEL_DEF,
		RUNNING,
		SHUTDOWN,
		END,
		ERROR
	}
	
	private boolean			logEvents = true;
	private boolean			proxyDebugOutput = true;
	private String			proxyName = "";
	private boolean			serverStarted = false;
	
	protected final String	proxyPath;
	protected final int		baseModelId;
	protected final boolean launchManually;

	/* state is volatile so no explicit synchronization needed */
	// TODO - if can limit to state machine thread, remove volatile
	private volatile ProxyState state;

	private List<IProxyCommand> 				commands = new ArrayList<IProxyCommand>();
	private LinkedBlockingQueue<IProxyEvent>	events = new LinkedBlockingQueue<IProxyEvent>();
	private ListenerList						listeners = new ListenerList();

	public AbstractProxyRuntimeClient(String proxyName, String proxyPath, int baseModelId, boolean launchManually) {
		super(new ProxyRuntimeEventFactory());
		super.addProxyEventListener(this);
		this.proxyName = proxyName;
		this.proxyPath = proxyPath;
		this.baseModelId = baseModelId;
		this.launchManually = launchManually;
		this.state = ProxyState.IDLE;
	}
	
	/**
	 * Add a command to the list of commands that have been sent to the proxy
	 * 
	 * @param command
	 */
	public void addCommand(IProxyCommand command) {
		synchronized (commands) {
			commands.add(command);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#addProxyRuntimeEventListener(org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeEventListener)
	 */
	public void addProxyRuntimeEventListener(IProxyRuntimeEventListener listener) {
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyConnectedEvent(org.eclipse.ptp.core.proxy.event.IProxyConnectedEvent)
	 */
	public void handleProxyConnectedEvent(IProxyConnectedEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyDisconnectedEvent(org.eclipse.ptp.core.proxy.event.IProxyDisconnectedEvent)
	 */
	public void handleProxyDisconnectedEvent(IProxyDisconnectedEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyErrorEvent(org.eclipse.ptp.core.proxy.event.IProxyErrorEvent)
	 */
	public void handleProxyErrorEvent(IProxyErrorEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyExtendedEvent(org.eclipse.ptp.core.proxy.event.IProxyExtendedEvent)
	 */
	public void handleProxyExtendedEvent(IProxyExtendedEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyOKEvent(org.eclipse.ptp.core.proxy.event.IProxyOKEvent)
	 */
	public void handleProxyOKEvent(IProxyOKEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.proxy.event.IProxyEventListener#handleProxyTimeoutEvent(org.eclipse.ptp.core.proxy.event.IProxyTimeoutEvent)
	 */
	public void handleProxyTimeoutEvent(IProxyTimeoutEvent event) {
		try {
			// this will wake up the state machine to process the event
			events.add(event);
		} catch (IllegalStateException except) {
			// events list should never be full
			except.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#removeProxyRuntimeEventListener(org.eclipse.ptp.rtsystem.proxy.event.IProxyRuntimeEventListener)
	 */
	public void removeProxyRuntimeEventListener(IProxyRuntimeEventListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Set flag to control the logging of events
	 * 
	 * @param logEvents - event logging is turned on if true, turned off otherwise 
	 */ 
	public void setEventLogging(boolean logEvents) {
		this.logEvents = logEvents;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#shutdown()
	 */
	public void shutdown() {
		if (state != ProxyState.SHUTDOWN) {
			try {
				if (logEvents) System.out.println(toString() + ": shutting down server...");
				state = ProxyState.SHUTDOWN;
				sessionFinish();
			} catch (IOException e) {
				e.printStackTrace();
				PTPCorePlugin.log(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#startEvents()
	 */
	public void startEvents() throws IOException {
		if (state != ProxyState.RUNNING) {
			throw new IOException("Not accepting commands");
		}
		IProxyCommand command = new ProxyRuntimeStartEventsCommand(this);
		addCommand(command);
		command.send();
	}
	
	/**
	 * Start the proxy state machine thread. This will launch the proxy server and
	 * forward events to listeners.
	 * 
	 * @return
	 */
	public boolean startup() {
		if (state == ProxyState.IDLE) {
			Thread smt = new Thread(new StateMachineThread(), proxyName + StateMachineThread.name);
			smt.start();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#stopEvents()
	 */
	public void stopEvents() throws IOException {
		if (state != ProxyState.RUNNING) {
			throw new IOException("Not accepting commands");
		}
		IProxyCommand command = new ProxyRuntimeStopEventsCommand(this);
		addCommand(command);
		command.send();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#submitJob(java.lang.String[])
	 */
	public void submitJob(String[] args) throws IOException {
		if (state != ProxyState.RUNNING) {
			throw new IOException("Not accepting commands");
		}
		IProxyCommand command = new ProxyRuntimeSubmitJobCommand(this, args);
		addCommand(command);
		command.send();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.rtsystem.proxy.IProxyRuntimeClient#terminateJob(java.lang.String)
	 */
	public void terminateJob(String jobId) throws IOException {
		if (state != ProxyState.RUNNING) {
			throw new IOException("Not accepting commands");
		}
		IProxyCommand command = new ProxyRuntimeTerminateJobCommand(this, jobId);
		addCommand(command);
		command.send();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
    	return proxyName + "ProxyRuntimeClient";
    }
	
	/**
	 * Locate the command associated with a particular event. Uses the
	 * transaction ID o match commands/events.
	 * 
	 * @param event
	 * @return
	 */
	private IProxyCommand getCommandForEvent(IProxyEvent event) {
		IProxyCommand[] ca;
		IProxyCommand command = null;
		synchronized (commands) {
			ca = commands.toArray(new IProxyCommand[0]);
		}
		for (IProxyCommand cmd : ca) {
			if (cmd.getTransactionID() == event.getTransactionID()) {
				command = cmd;
				break;
			}
		}
		return command;
	}
	
	/**
	 * Process events while in the RUNNING state. 
	 */
	private void processRunningEvent(IProxyCommand command, IProxyEvent event) {
		if (logEvents) {
			System.out.println(toString() + " recieved event " + event);
		}
    	switch (command.getCommandID()) {
    	case IProxyRuntimeCommand.START_EVENTS:
    		switch (event.getEventID()) {
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NEW_JOB_EVENT:
    			fireProxyRuntimeNewJobEvent((IProxyRuntimeNewJobEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NEW_MACHINE_EVENT:
    			fireProxyRuntimeNewMachineEvent((IProxyRuntimeNewMachineEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NEW_NODE_EVENT:
    			fireProxyRuntimeNewNodeEvent((IProxyRuntimeNewNodeEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NEW_PROCESS_EVENT:
    			fireProxyRuntimeNewProcessEvent((IProxyRuntimeNewProcessEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NEW_QUEUE_EVENT:
    			fireProxyRuntimeNewQueueEvent((IProxyRuntimeNewQueueEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_JOB_CHANGE_EVENT:
    			fireProxyRuntimeJobChangeEvent((IProxyRuntimeJobChangeEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_MACHINE_CHANGE_EVENT:
    			fireProxyRuntimeMachineChangeEvent((IProxyRuntimeMachineChangeEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_NODE_CHANGE_EVENT:
    			fireProxyRuntimeNodeChangeEvent((IProxyRuntimeNodeChangeEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_PROCESS_CHANGE_EVENT:
    			fireProxyRuntimeProcessChangeEvent((IProxyRuntimeProcessChangeEvent)event);
    			break;
    		case IProxyRuntimeEvent.PROXY_RUNTIME_QUEUE_CHANGE_EVENT:
    			fireProxyRuntimeQueueChangeEvent((IProxyRuntimeQueueChangeEvent)event);
    			break;
    		case IProxyEvent.EVENT_OK:
    			removeCommand(command);
    			break;
     		}
 			break;
    	case IProxyRuntimeCommand.STOP_EVENTS:
			if (event instanceof IProxyOKEvent) {
				removeCommand(command);
			}
    		break;
    	case IProxyRuntimeCommand.SUBMIT_JOB:
			if (event instanceof IProxyOKEvent) {
				removeCommand(command);
			}
    		break;
    	case IProxyRuntimeCommand.TERMINATE_JOB:
			if (event instanceof IProxyOKEvent) {
				removeCommand(command);
			}
    		break;
    	default:
    		break;
    	}

	}
	
	/**
	 * Remove command from list of sent commands.
	 * 
	 * @param command
	 */
	private void removeCommand(IProxyCommand command) {
		synchronized (commands) {
			commands.remove(command);
		}
	}	
	
	/**
	 * Start the proxy server. The server will eventually connect to the session
	 * created using sessionCreate(). This will result in a connected event.
	 * 
	 * NOTE: This code will change when remote server support is added
	 * 
	 * @return true if the session was created. Server errors are handled separately
	 * as events.
	 */
	private boolean startupProxyServer() {

		if (logEvents) {
			System.out.println(toString() + " - firing up proxy, waiting for connection.  Please wait!  This can take a minute . . .");
			System.out.println("PROXY_SERVER path = '" + proxyPath + "'");
		}

		try {
			sessionCreate();

			if (launchManually) {
				System.out.println("Waiting for manual launch of proxy on port " + getSessionPort() + "...");
			} else {
				Thread runThread = new Thread(new ProxyServerThread(), proxyName + ProxyServerThread.name);
				runThread.setDaemon(true);
				runThread.start();
			}
			if (logEvents) System.out.println(toString() + ": Waiting on accept.");

		} catch (IOException e) {
			System.err.println("Exception starting up proxy. :(");
			try {
				sessionFinish();
			} catch (IOException e1) {
				PTPCorePlugin.log(e1);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeAttributeDefEvent(IProxyRuntimeAttributeDefEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeAttributeDefEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeConnectedStateEvent(IProxyRuntimeConnectedStateEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeConnectedStateEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeErrorEvent(IProxyRuntimeErrorEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeErrorEvent(event);
		}
	}
	
	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeJobChangeEvent(IProxyRuntimeJobChangeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeJobChangeEvent(event);
		}
	}
	
	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeMachineChangeEvent(IProxyRuntimeMachineChangeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeMachineChangeEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNewJobEvent(IProxyRuntimeNewJobEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNewJobEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNewMachineEvent(IProxyRuntimeNewMachineEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNewMachineEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNewNodeEvent(IProxyRuntimeNewNodeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNewNodeEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNewProcessEvent(IProxyRuntimeNewProcessEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNewProcessEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNewQueueEvent(IProxyRuntimeNewQueueEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNewQueueEvent(event);
		}
	}
	
	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeNodeChangeEvent(IProxyRuntimeNodeChangeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeNodeChangeEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeProcessChangeEvent(IProxyRuntimeProcessChangeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeProcessChangeEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeQueueChangeEvent(IProxyRuntimeQueueChangeEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeQueueChangeEvent(event);
		}
	}

    /**
 	 * Forward event to listeners
	 * 
    * @param event
     */
    protected void fireProxyRuntimeRunningStateEvent(IProxyRuntimeRunningStateEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeRunningStateEvent(event);
		}
	}

	/**
	 * Forward event to listeners
	 * 
	 * @param event
	 */
	protected void fireProxyRuntimeShutdownStateEvent(IProxyRuntimeShutdownStateEvent event) {
		for (Object listener : listeners.getListeners()) {
			((IProxyRuntimeEventListener)listener).handleProxyRuntimeShutdownStateEvent(event);
		}
	}

	/**
	 * Main proxy state machine. Use to manage communication with a proxy
	 * client.
	 * 
	 * This should only be called from the state machine thread 
	 * (so synchronized not needed)
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 */
	protected void runStateMachine() throws IOException, InterruptedException, IllegalStateException {

		serverStarted = startupProxyServer();
		if (serverStarted == false) {
			state = ProxyState.SHUTDOWN;
			fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(0, ErrorType.PROXY, "Could not start proxy"));
		} else {
			state = ProxyState.STARTUP;
		}
		
		while (state != ProxyState.IDLE && state != ProxyState.ERROR) {
			IProxyCommand command;
			IProxyEvent event;
			
			switch (state) {
			case STARTUP:
				/*
				 * This state is used to wait for a connected event from the proxy after 
				 * it was started. A connected event indicates that the 
				 * proxy has been launched successfully. Send an INIT command 
				 * to the proxy and enter the INIT state.
				 */
				event = events.take();

				if (event instanceof IProxyConnectedEvent) {
					try {
						sessionHandleEvents();
						command = new ProxyRuntimeInitCommand(this, baseModelId);
						addCommand(command);
						command.send();
		    			state = ProxyState.INIT;
						fireProxyRuntimeConnectedStateEvent(new ProxyRuntimeConnectedStateEvent());
					} catch (IOException e) {
						state = ProxyState.SHUTDOWN;
						fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(0, ErrorType.INTERNAL, e.getMessage()));
					}
				} else if (event instanceof IProxyTimeoutEvent) {
					state = ProxyState.SHUTDOWN;
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.CONNECT_TIMEOUT, "Proxy connection timeout out"));
				} else if (event instanceof IProxyDisconnectedEvent) {
					state = ProxyState.SHUTDOWN;
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.DISCONNECTED, "Proxy disconnected"));
				} else if (event instanceof IProxyErrorEvent) {
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.PROXY, ((IProxyErrorEvent)event).getErrorMessage()));
				} else {
					state = ProxyState.ERROR;
					throw new IllegalStateException("Received " + event.toString() + " in STARTUP");
				}
				break;
				
			case INIT:
				/*
				 * This state is sed to wait from a response from the INIT command. 
				 * If we receive an OK event, we next send a MODEL_DEF command and 
				 * enter the MODEL_DEF state.
				 */
				event = events.take();

				if (event instanceof IProxyDisconnectedEvent) {
					state = ProxyState.SHUTDOWN;
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.DISCONNECTED, "Proxy disconnected"));
				} else if (event instanceof IProxyErrorEvent) {
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.PROXY, ((IProxyErrorEvent)event).getErrorMessage()));
				} else if (event instanceof IProxyOKEvent){
					command = getCommandForEvent(event);
					if (command != null) {
						removeCommand(command);
						command = new ProxyRuntimeModelDefCommand(this);
						addCommand(command);
						command.send();
						state = ProxyState.MODEL_DEF;
					} else {
						state = ProxyState.ERROR;
						throw new IllegalStateException("Could not find command for event in INIT");					
					}
				} else {
					state = ProxyState.ERROR;
					throw new IllegalStateException("Received " + event.toString() + " in INIT");				
				}
				break;
				
			case MODEL_DEF:
				/*
				 * This state is used to process attribute definition events that are used by
				 * the proxy to define any attributes that it will use. This state
				 * is terminated when an OK event is received. At this point
				 * enter the RUNNING state.
				 * 
				 */
				event = events.take();

				if (event instanceof IProxyDisconnectedEvent) {
					state = ProxyState.SHUTDOWN;
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.DISCONNECTED, "Proxy disconnected"));
				} else if (event instanceof IProxyErrorEvent) {
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.PROXY, ((IProxyErrorEvent)event).getErrorMessage()));
				} else {
					command = getCommandForEvent(event);
					if (command != null) {
						if (event instanceof IProxyOKEvent) {
							removeCommand(command);
							state = ProxyState.RUNNING;
							fireProxyRuntimeRunningStateEvent(new ProxyRuntimeRunningStateEvent());
						} else if (event instanceof IProxyRuntimeAttributeDefEvent){
							fireProxyRuntimeAttributeDefEvent((IProxyRuntimeAttributeDefEvent)event);
						} else {
							state = ProxyState.ERROR;
							throw new IllegalStateException("Could not find command for event in MODEL_DEF");						
						}
					} else {
						state = ProxyState.ERROR;
						throw new IllegalStateException("Received " + event.toString() + " in MODEL_DEF");					
					}
				}
				break;
				
			case RUNNING:
				/*
				 * This is the main event processing state. Any events we receive in this
				 * state are forwarded to event listeners. This state continues until
				 * the shutdown() method is called, at which time we enter the shutdown
				 * state.
				 */
				event = events.take();

				if (event instanceof IProxyDisconnectedEvent) {
					state = ProxyState.SHUTDOWN;
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.DISCONNECTED, "Proxy disconnected"));
				} else if (event instanceof IProxyErrorEvent) {
					fireProxyRuntimeErrorEvent(new ProxyRuntimeErrorEvent(event.getTransactionID(), ErrorType.PROXY, ((IProxyErrorEvent)event).getErrorMessage()));
				} else {
					command = getCommandForEvent(event);
					if (command != null) {
						processRunningEvent(command, event);
					} else {
						state = ProxyState.ERROR;
						throw new IllegalStateException("Could not find command for event in RUNNING");					
					}
				}
				break;
				
			case SHUTDOWN:
				/*
				 * This state is entered when the proxy has been shut down. We
				 * stay in this state until the session is shut down. This happens 
				 * when an OK event is received in response to a QUIT command, or if
				 * the shutdown timeout has expired. 
				 */
				if (isShutdown()) {
					fireProxyRuntimeShutdownStateEvent(new ProxyRuntimeShutdownStateEvent());
					state = ProxyState.IDLE;
				} else {
					/* 
					 * Wait for any event. We will eventually get an IProxyDisconnectedEvent.
					 */
					events.take();
				}
				break;
				
			default:
				throw new IllegalStateException("Unknown state: " +state.toString());
			}
		}
	}	
}
