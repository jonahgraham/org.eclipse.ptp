package org.eclipse.ptp.launch.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.core.elements.attributes.JobAttributes.State;
import org.eclipse.ptp.core.events.IJobChangedEvent;
import org.eclipse.ptp.core.listeners.IJobListener;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.launch.PTPLaunchPlugin;
import org.eclipse.ptp.launch.messages.Messages;
import org.eclipse.ptp.rmsystem.IJobStatus;
import org.eclipse.ptp.rmsystem.IResourceManagerControl;
import org.eclipse.ptp.rmsystem.IResourceManagerControl.JobControlOperation;

public class RuntimeProcess implements IProcess, IJobListener {
	private IPLaunch fLaunch = null;
	private final IResourceManagerControl fResourceManager;
	private String fJobId = null;
	private Map<String, String> fAttributes;
	private int fExitValue = -1;
	private boolean fTerminated = false;

	public RuntimeProcess(IPLaunch launch, IResourceManagerControl rm, String jobId, Map<String, String> attributes) {
		fLaunch = launch;
		fResourceManager = rm;
		fJobId = jobId;
		rm.addJobListener(this);
		initializeAttributes(attributes);
		fTerminated = rm.getJobStatus(jobId).getState() == JobAttributes.State.COMPLETED;
		launch.addProcess(this);
		fireCreationEvent();
	}

	private void initializeAttributes(Map<String, String> attributes) {
		if (attributes != null) {
			Iterator<String> keys = attributes.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				setAttribute(key, attributes.get(key));
			}
		}
	}

	/***************************************************************************************************************************************************************************************************
	 * IProcess interface
	 **************************************************************************************************************************************************************************************************/
	public String getLabel() {
		return fJobId;
	}

	public ILaunch getLaunch() {
		return fLaunch;
	}

	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public void setAttribute(String key, String value) {
		if (fAttributes == null) {
			fAttributes = new HashMap<String, String>(5);
		}
		Object origVal = fAttributes.get(key);
		if (origVal != null && origVal.equals(value)) {
			return;
		}
		fAttributes.put(key, value);
	}

	public String getAttribute(String key) {
		if (fAttributes == null) {
			return null;
		}
		return fAttributes.get(key);
	}

	public int getExitValue() throws DebugException {
		if (isTerminated()) {
			return fExitValue;
		}
		throw new DebugException(new Status(IStatus.ERROR, PTPLaunchPlugin.getUniqueIdentifier(), IStatus.ERROR,
				Messages.RuntimeProcess_0, null));
	}

	protected void terminated() {
		synchronized (this) {
			fTerminated = true;
		}
		fExitValue = 0;
		fireTerminateEvent();
	}

	/***************************************************************************************************************************************************************************************************
	 * ITerminate interface
	 **************************************************************************************************************************************************************************************************/
	public synchronized boolean canTerminate() {
		return !fTerminated;
	}

	public synchronized boolean isTerminated() {
		return fTerminated;
	}

	public void terminate() throws DebugException {
		if (!isTerminated()) {
			try {
				fResourceManager.control(fJobId, JobControlOperation.TERMINATE, null);
			} catch (CoreException e) {
				throw new DebugException(e.getStatus());
			}
		}
	}

	/***************************************************************************************************************************************************************************************************
	 * Adapter interface
	 **************************************************************************************************************************************************************************************************/
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(ILaunch.class)) {
			return getLaunch();
		}
		return null;
	}

	/***************************************************************************************************************************************************************************************************
	 * IResourceManagerListener interface
	 **************************************************************************************************************************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.core.listeners.IJobListener#handleEvent(org.eclipse.ptp
	 * .core.events.IJobChangeEvent)
	 */
	public void handleEvent(IJobChangedEvent e) {
		IResourceManagerControl rm = e.getSource();
		IJobStatus status = rm.getJobStatus(e.getJobId());
		if (status.getState() == State.COMPLETED) {
			terminated();
		}
	}

	/***************************************************************************************************************************************************************************************************
	 * Debug Event
	 **************************************************************************************************************************************************************************************************/
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[] { event });
		}
	}

	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
}
