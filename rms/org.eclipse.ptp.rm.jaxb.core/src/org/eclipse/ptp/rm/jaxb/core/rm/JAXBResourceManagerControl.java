/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.rm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.JAXBElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.rm.jaxb.core.ICommandJobStatus;
import org.eclipse.ptp.rm.jaxb.core.ICommandJobStreamsProxy;
import org.eclipse.ptp.rm.jaxb.core.IJAXBNonNLSConstants;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerConfiguration;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl;
import org.eclipse.ptp.rm.jaxb.core.JAXBCorePlugin;
import org.eclipse.ptp.rm.jaxb.core.data.Attribute;
import org.eclipse.ptp.rm.jaxb.core.data.Command;
import org.eclipse.ptp.rm.jaxb.core.data.Control;
import org.eclipse.ptp.rm.jaxb.core.data.ManagedFiles;
import org.eclipse.ptp.rm.jaxb.core.data.Property;
import org.eclipse.ptp.rm.jaxb.core.data.ResourceManagerData;
import org.eclipse.ptp.rm.jaxb.core.data.Script;
import org.eclipse.ptp.rm.jaxb.core.messages.Messages;
import org.eclipse.ptp.rm.jaxb.core.runnable.ManagedFilesJob;
import org.eclipse.ptp.rm.jaxb.core.runnable.ScriptHandler;
import org.eclipse.ptp.rm.jaxb.core.runnable.command.CommandJob;
import org.eclipse.ptp.rm.jaxb.core.runnable.command.CommandJobStatus;
import org.eclipse.ptp.rm.jaxb.core.utils.CoreExceptionUtils;
import org.eclipse.ptp.rm.jaxb.core.utils.RemoteServicesDelegate;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;
import org.eclipse.ptp.rmsystem.AbstractResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.AbstractResourceManagerControl;
import org.eclipse.ptp.rmsystem.IJobStatus;
import org.eclipse.ptp.rmsystem.IResourceManager;

public final class JAXBResourceManagerControl extends AbstractResourceManagerControl implements IJAXBResourceManagerControl,
		IJAXBNonNLSConstants {

	private class JobStatusMap extends Thread {
		private final Map<String, ICommandJobStatus> map = new HashMap<String, ICommandJobStatus>();
		private boolean running = false;

		@Override
		public void run() {
			Map<String, String> toPrune = new HashMap<String, String>();

			synchronized (map) {
				running = true;
			}

			while (isRunning()) {
				try {
					Thread.sleep(5 * MINUTE_IN_MS);
				} catch (InterruptedException ignored) {
				}
				synchronized (map) {
					for (String jobId : map.keySet()) {
						IJobStatus status = getJobStatus(jobId);
						String state = status.getState();
						if (IJobStatus.COMPLETED.equals(state) || IJobStatus.FAILED.equals(state)) {
							toPrune.put(jobId, jobId);
						} else if (IJobStatus.RUNNING.equals(state)) {
							ICommandJobStatus commandJobStatus = map.get(jobId);
							if (commandJobStatus != null) {
								commandJobStatus.startProxy();
							}
						}
					}
					for (Iterator<Map.Entry<String, ICommandJobStatus>> i = map.entrySet().iterator(); i.hasNext();) {
						Map.Entry<String, ICommandJobStatus> e = i.next();
						if (null != toPrune.remove(e.getKey())) {
							ICommandJobStatus status = e.getValue();
							if (status != null) {
								status.cancel();
							}
							i.remove();
						}
					}
				}
			}

			synchronized (map) {
				for (String jobId : map.keySet()) {
					ICommandJobStatus status = map.get(jobId);
					if (status != null) {
						status.cancel();
					}
				}
				map.clear();
			}
		}

		private void addJobStatus(String jobId, ICommandJobStatus status) {
			synchronized (map) {
				map.put(jobId, status);
			}
		}

		private ICommandJobStatus getStatus(String jobId) {
			ICommandJobStatus status = null;
			synchronized (map) {
				status = map.get(jobId);
			}
			return status;
		}

		private void halt() {
			synchronized (map) {
				running = false;
			}
			this.interrupt();
		}

		private boolean isRunning() {
			boolean b = false;
			synchronized (map) {
				b = running;
			}
			return b;
		}

		private void removeJobStatus(String jobId) {
			synchronized (map) {
				map.remove(jobId);
			}
		}
	}

	private final IJAXBResourceManagerConfiguration config;
	private final Control controlData;
	private final Map<String, String> dynSystemEnv;
	private final JobStatusMap jobStatusMap;
	private final RemoteServicesDelegate delegate;
	private boolean appendSysEnv;

	public JAXBResourceManagerControl(AbstractResourceManagerConfiguration jaxbServiceProvider) {
		super(jaxbServiceProvider);
		config = (IJAXBResourceManagerConfiguration) jaxbServiceProvider;
		delegate = new RemoteServicesDelegate(config.getRemoteServicesId(), config.getConnectionName());
		ResourceManagerData d = null;

		try {
			config.realizeRMDataFromXML();
			d = config.getResourceManagerData();
		} catch (Throwable t) {
			JAXBCorePlugin.log(t);
		}

		if (d != null) {
			controlData = d.getControlData();
		} else {
			controlData = null;
		}
		dynSystemEnv = new TreeMap<String, String>();
		jobStatusMap = new JobStatusMap();
		jobStatusMap.start();
	}

	public boolean getAppendSysEnv() {
		return appendSysEnv;
	}

	public IJAXBResourceManagerConfiguration getConfig() {
		return config;
	}

	public Map<String, String> getDynSystemEnv() {
		return dynSystemEnv;
	}

	public IJAXBResourceManagerConfiguration getJAXBRMConfiguration() {
		return config;
	}

	public RemoteServicesDelegate getRemoteServicesDelegate() {
		return delegate;
	}

	@Override
	protected void doControlJob(String jobId, String operation, IProgressMonitor monitor) throws CoreException {
		try {
			resetEnv();
			Property p = new Property();
			p.setVisible(false);
			p.setName(jobId);
			p.setValue(jobId);
			RMVariableMap.getActiveInstance().getVariables().put(jobId, p);
			doControlCommand(jobId, operation);
			RMVariableMap.getActiveInstance().getVariables().remove(jobId);
			if (TERMINATE_OPERATION.equals(operation)) {
				jobStatusMap.removeJobStatus(jobId);
			}
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		}
	}

	@Override
	protected void doDispose() {
		// NOP for the moment
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doGetJobStatus
	 * (java.lang.String)
	 */
	@Override
	protected IJobStatus doGetJobStatus(String jobId) throws CoreException {
		try {
			Property p = new Property();
			p.setVisible(false);
			p.setName(jobId);
			p.setValue(jobId);
			RMVariableMap.getActiveInstance().getVariables().put(jobId, p);

			Command job = controlData.getGetJobStatus();
			if (job == null) {
				throw CoreExceptionUtils.newException(Messages.RMNoSuchCommandError + JOBSTATUS, null);
			}

			runCommand(jobId, job, false, true);

			p = (Property) RMVariableMap.getActiveInstance().getVariables().remove(jobId);
			String state = IJobStatus.UNDETERMINED;
			if (p != null) {
				state = (String) p.getValue();
			}

			ICommandJobStatus status = jobStatusMap.getStatus(jobId);
			if (status == null) {
				status = new CommandJobStatus(jobId, state);
				jobStatusMap.addJobStatus(jobId, status);
			} else {
				status.setState(state);
			}

			if (IJobStatus.RUNNING.equals(state)) {
				status.startProxy();
			} else if (IJobStatus.FAILED.equals(state) || IJobStatus.COMPLETED.equals(state)) {
				status.cancel();
				jobStatusMap.removeJobStatus(jobId);
			}

			return status;
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		}
	}

	@Override
	protected void doShutdown() throws CoreException {
		getResourceManager().setState(IResourceManager.TERMINATE_OPERATION);
		try {
			resetEnv();
			doOnShutdown();
			doDisconnect();
			config.clearReferences();
			jobStatusMap.halt();
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		}
		getResourceManager().setState(IResourceManager.STOPPED_STATE);
	}

	@Override
	protected void doStartup(IProgressMonitor monitor) throws CoreException {
		getResourceManager().setState(IResourceManager.STARTING_STATE);
		try {
			try {
				doConnect(monitor);
			} catch (RemoteConnectionException t) {
				throw CoreExceptionUtils.newException(t.getMessage(), t);
			}
			resetEnv();
			doOnStartUp(monitor);
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		}
		getResourceManager().setState(IResourceManager.STARTED_STATE);
	}

	@Override
	protected IJobStatus doSubmitJob(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		resetEnv();
		/*
		 * give submission a unique id which will in most cases be replaced by
		 * the resource-generated id for the job/process
		 */
		String uuid = UUID.randomUUID().toString();
		Property p = new Property();
		p.setVisible(false);
		RMVariableMap.getActiveInstance().getVariables().put(uuid, p);

		/*
		 * overwrite property/attribute values based on user choices
		 */
		updatePropertyValuesFromTab(configuration);

		/*
		 * create the script if necessary; adds the contents to env as
		 * "${rm:script}" (property, cleared during #updatePropertyValuesFromTab
		 * and possibly reset from custom script contents passed in through the
		 * launch configuration; if so, the following returns immediately)
		 */
		maybeHandleScript(uuid, controlData.getScript());

		/*
		 * if the script is to be staged, a managed file pointing to
		 * ${rm:script#value} as its content must exist.
		 */
		if (!maybeHandleManagedFiles(uuid, controlData.getManagedFiles())) {
			throw CoreExceptionUtils.newException(Messages.CannotCompleteSubmitFailedStaging, null);
		}

		CommandJob job = doJobSubmitCommand(uuid, mode);

		/*
		 * If the submit job lacks a jobId on the standard streams, then we
		 * assign it the UUID (it is most probably interactive); else we wait
		 * for the id to be set by the tokenizer.
		 */
		CommandJobStatus status = null;
		if (job.waitForId()) {
			status = new CommandJobStatus();
			status.waitForJobId(uuid);
		} else {
			String state = job.isActive() ? IJobStatus.RUNNING : IJobStatus.FAILED;
			status = new CommandJobStatus(uuid, state);
		}

		/*
		 * property containing actual jobId as name was accessed in the wait
		 * call
		 */
		RMVariableMap.getActiveInstance().getVariables().remove(uuid);
		ICommandJobStreamsProxy proxy = job.getProxy();
		status.setProxy(proxy);
		jobStatusMap.addJobStatus(status.getJobId(), status);
		status.setLaunchConfig(configuration);
		if (!job.isBatch()) {
			status.setProcess(job.getProcess());
		}
		return status;
	}

	/*
	 * If there are special server connections to open, those need to be taken
	 * care of by a command to be run on start-up; here we just check for open
	 * connections.
	 */
	private void doConnect(IProgressMonitor monitor) throws RemoteConnectionException {
		IRemoteConnection conn = delegate.getLocalConnection();
		if (!conn.isOpen()) {
			throw new RemoteConnectionException(Messages.LocalConnectionError);
		}
		conn = delegate.getRemoteConnection();
		if (!conn.isOpen()) {
			throw new RemoteConnectionException(Messages.RemoteConnectionError + conn.getAddress());
		}
	}

	/*
	 * If the command is not supported, throw exception
	 */
	private void doControlCommand(String jobId, String operation) throws CoreException {
		CoreException ce = CoreExceptionUtils.newException(Messages.RMNoSuchCommandError + operation, null);
		Command job = null;
		if (TERMINATE_OPERATION.equals(operation)) {
			if (maybeKillInteractive(jobId)) {
				return;
			}

			job = controlData.getTerminateJob();
			if (job == null) {
				throw ce;
			}
		} else if (SUSPEND_OPERATION.equals(operation)) {
			job = controlData.getSuspendJob();
			if (job == null) {
				throw ce;
			}
		} else if (RESUME_OPERATION.equals(operation)) {
			job = controlData.getResumeJob();
			if (job == null) {
				throw ce;
			}
		} else if (RELEASE_OPERATION.equals(operation)) {
			job = controlData.getReleaseJob();
			if (job == null) {
				throw ce;
			}
		} else if (HOLD_OPERATION.equals(operation)) {
			job = controlData.getHoldJob();
			if (job == null) {
				throw ce;
			}
		}
		runCommand(jobId, job, false, true);
	}

	/*
	 * Close the connections.
	 */
	private void doDisconnect() {
		IRemoteConnection conn = delegate.getLocalConnection();
		if (conn.isOpen()) {
			conn.close();
		}
		conn = delegate.getRemoteConnection();
		if (conn.isOpen()) {
			conn.close();
		}
	}

	/*
	 * Run either in interactive, batch or debug mode right now,
	 * ILaunchManager.RUN_MODE and ILaunchManager.DEBUG_MODE are the two
	 * choices, meaning a single configuration cannot support both batch and
	 * interactive.
	 */
	private CommandJob doJobSubmitCommand(String uuid, String mode) throws CoreException {

		List<JAXBElement<Command>> commands = controlData.getSubmitInteractiveOrSubmitBatchOrSubmitDebugInteractive();
		if (commands.isEmpty()) {
			throw CoreExceptionUtils.newException(Messages.MissingRunCommandsError, null);
		}

		Command command = null;
		boolean batch = false;
		for (JAXBElement<Command> job : commands) {
			command = job.getValue();
			if (job.getName().equals(SUBMIT_INTERACTIVE)) {
				if (ILaunchManager.RUN_MODE.equals(mode)) {
					break;
				}
			} else if (job.getName().equals(SUBMIT_BATCH)) {
				if (ILaunchManager.RUN_MODE.equals(mode)) {
					batch = true;
					break;
				}
			} else if (job.getName().equals(SUBMIT_DEBUG_INTERACTIVE)) {
				if (ILaunchManager.DEBUG_MODE.equals(mode)) {
					break;
				}
			} else if (job.getName().equals(SUBMIT_DEBUG_BATCH)) {
				if (ILaunchManager.DEBUG_MODE.equals(mode)) {
					batch = true;
					break;
				}
			}
		}

		if (command == null) {
			throw CoreExceptionUtils.newException(Messages.MissingRunCommandsError + mode, null);
		}

		return runCommand(uuid, command, batch, false);
	}

	/*
	 * Run the shut down commands, if any
	 */
	private void doOnShutdown() throws CoreException {
		List<Command> onShutDown = controlData.getShutDownCommand();
		runCommands(null, onShutDown, SHUTDOWN);
	}

	/*
	 * Run the start up commands, if any
	 */
	private void doOnStartUp(IProgressMonitor monitor) throws CoreException {
		List<Command> onStartUp = controlData.getStartUpCommand();
		runCommands(null, onStartUp, STARTUP);
	}

	/*
	 * Write content to file if indicated, and stage to host.
	 */
	private boolean maybeHandleManagedFiles(String uuid, ManagedFiles files) throws CoreException {
		ManagedFilesJob job = new ManagedFilesJob(uuid, files, delegate);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException ignored) {
		}
		return job.getSuccess();
	}

	/*
	 * Serialize script content if necessary. We first check to see if there is
	 * a custom script loaded through the launch configuration tab.
	 */
	private void maybeHandleScript(String uuid, Script script) {
		Property p = (Property) RMVariableMap.getActiveInstance().getVariables().get(SCRIPT);
		if (p != null && p.getValue() != null) {
			return;
		}
		if (script == null) {
			return;
		}
		ScriptHandler job = new ScriptHandler(uuid, script, RMVariableMap.getActiveInstance(), dynSystemEnv, appendSysEnv);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException ignored) {
		}
	}

	private boolean maybeKillInteractive(String jobId) {
		ICommandJobStatus status = jobStatusMap.getStatus(jobId);
		if (status != null && status.isInteractive()) {
			status.cancel();
			jobStatusMap.removeJobStatus(jobId);
			return true;
		}
		return false;
	}

	/*
	 * Ensure that the RM has its own environement. Add the fixed properties
	 * again, clear dynamic env from the tab.
	 */
	private void resetEnv() {
		try {
			config.setActive();
		} catch (Throwable t) {
			JAXBCorePlugin.log(t);
			return;
		}
		setFixedConfigurationProperties();
		dynSystemEnv.clear();
		appendSysEnv = true;
	}

	/*
	 * Create command job, and schedule.
	 */
	private CommandJob runCommand(String uuid, Command command, boolean batch, boolean join) throws CoreException {
		if (command == null) {
			throw CoreExceptionUtils.newException(Messages.RMNoSuchCommandError, null);
		}

		CommandJob job = new CommandJob(uuid, command, batch, this);
		if (batch) {
			Property p = (Property) RMVariableMap.getActiveInstance().getVariables().get(STDOUT);
			if (p != null) {
				job.setRemoteOutPath((String) p.getValue());
			}
			p = (Property) RMVariableMap.getActiveInstance().getVariables().get(STDERR);
			if (p != null) {
				job.setRemoteErrPath((String) p.getValue());
			}
		}

		job.schedule();

		if (join) {
			try {
				job.join();
			} catch (InterruptedException ignored) {
			}
		}

		return job;
	}

	/*
	 * Run command sequence. Here we join to insure seriality. If a job in the
	 * sequence fails, the subsequent commands will not run.
	 */
	private void runCommands(String uuid, List<Command> cmds, String operation) throws CoreException {
		for (Command cmd : cmds) {
			CommandJob job = runCommand(uuid, cmd, false, false);

			if (!job.isActive()) {
				return;
			}

			try {
				job.join();
			} catch (InterruptedException ignored) {
			}
		}
	}

	/*
	 * From the user runtime choices.
	 */
	private void setFixedConfigurationProperties() {
		IRemoteConnection rc = delegate.getRemoteConnection();
		RMVariableMap.getActiveInstance().maybeAddProperty(CONTROL_USER_VAR, rc.getUsername(), false);
		RMVariableMap.getActiveInstance().maybeAddProperty(CONTROL_ADDRESS_VAR, rc.getAddress(), false);
	}

	/*
	 * Updates selection: if not selected, value is nulled out. Transfers the
	 * values from the configuration to the live map.
	 */
	@SuppressWarnings("unchecked")
	private void updatePropertyValuesFromTab(ILaunchConfiguration configuration) throws CoreException {
		RMVariableMap map = RMVariableMap.getActiveInstance();
		Map<String, Object> env = map.getVariables();
		env.remove(SCRIPT); // to ensure the most recent script is used

		Map<String, String> selected = config.getSelectedAttributeSet();

		@SuppressWarnings("rawtypes")
		Map lcattr = configuration.getAttributes();
		for (Object key : lcattr.keySet()) {
			Object value = lcattr.get(key);
			Object target = env.get(key.toString());
			if (target instanceof Property) {
				Property p = (Property) target;
				if (selected != null && !selected.containsKey(p.getName())) {
					p.setValue(null);
					p.setSelected(false);
				} else {
					p.setValue(value.toString());
				}
			} else if (target instanceof Attribute) {
				Attribute ja = (Attribute) target;
				if (selected != null && !selected.containsKey(ja.getName())) {
					ja.setValue(null);
					ja.setSelected(false);
				} else {
					ja.setValue(value);
				}
			}
		}

		dynSystemEnv.putAll(configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, dynSystemEnv));
		appendSysEnv = configuration.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, appendSysEnv);

		map.maybeOverwrite(DIRECTORY, IPTPLaunchConfigurationConstants.ATTR_WORKING_DIR, configuration);
		map.maybeOverwrite(EXEC_PATH, IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH, configuration);
		map.maybeOverwrite(PROG_ARGS, IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS, configuration);
	}
}
