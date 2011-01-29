/*******************************************************************************
 * Copyright (c) 2009 School of Computer Science, 
 * National University of Defense Technology, P.R.China
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Peichang Shi <pcmutates@163.com>/<pcshi@nudt.edu.cn>
 * 		Jie Jiang, 	National University of Defense Technology
 *******************************************************************************/
package org.eclipse.ptp.rm.slurm.core.rmsystem;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ptp.core.attributes.AttributeDefinitionManager;
import org.eclipse.ptp.core.elements.IPResourceManager;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.rm.slurm.core.SLURMCorePlugin;
import org.eclipse.ptp.rm.slurm.core.SLURMJobAttributes;
import org.eclipse.ptp.rm.slurm.core.SLURMNodeAttributes;
import org.eclipse.ptp.rm.slurm.core.messages.Messages;
import org.eclipse.ptp.rm.slurm.core.rtsystem.SLURMProxyRuntimeClient;
import org.eclipse.ptp.rm.slurm.core.rtsystem.SLURMRuntimeSystem;
import org.eclipse.ptp.rmsystem.AbstractRuntimeResourceManager;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.rtsystem.IRuntimeSystem;
import org.eclipse.ptp.rtsystem.events.IRuntimeSubmitJobErrorEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SLURMResourceManager extends AbstractRuntimeResourceManager {

	/**
	 * @since 5.0
	 */
	public SLURMResourceManager(IPUniverse universe, IResourceManagerConfiguration config) {
		super(universe, config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractProxyResourceManager#doAfterCloseConnection
	 * ()
	 */
	@Override
	protected void doAfterCloseConnection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractProxyResourceManager#doAfterOpenConnection
	 * ()
	 */
	@Override
	protected void doAfterOpenConnection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractProxyResourceManager#doBeforeCloseConnection
	 * ()
	 */
	@Override
	protected void doBeforeCloseConnection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractProxyResourceManager#doBeforeOpenConnection
	 * ()
	 */
	@Override
	protected void doBeforeOpenConnection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractRuntimeResourceManager#doCreateRuntimeSystem
	 * ()
	 */
	@Override
	protected IRuntimeSystem doCreateRuntimeSystem() throws CoreException {
		IRuntimeSystem slurmRMS;
		ISLURMResourceManagerConfiguration config = (ISLURMResourceManagerConfiguration) getConfiguration();
		IPResourceManager rm = (IPResourceManager) getAdapter(IPResourceManager.class);
		int baseId;
		try {
			baseId = Integer.parseInt(rm.getID());
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, SLURMCorePlugin.getUniqueIdentifier(), e.getLocalizedMessage()));
		}
		SLURMProxyRuntimeClient runtimeProxy = new SLURMProxyRuntimeClient(config, baseId);
		AttributeDefinitionManager attrDefMgr = getAttributeDefinitionManager();
		attrDefMgr.setAttributeDefinitions(SLURMJobAttributes.getDefaultAttributeDefinitions());
		attrDefMgr.setAttributeDefinitions(SLURMNodeAttributes.getDefaultAttributeDefinitions());
		slurmRMS = new SLURMRuntimeSystem(runtimeProxy, attrDefMgr);
		return slurmRMS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rtsystem.IRuntimeEventListener#handleEvent(org.eclipse
	 * .ptp.rtsystem.events.IRuntimeSubmitJobErrorEvent;)
	 */
	@Override
	public void handleEvent(IRuntimeSubmitJobErrorEvent e) {
		final String title = Messages.SLURMResourceManager_0;
		final String msg = e.getErrorMessage();

		// System.out.println("Job submit error!");
		// System.out.println(msg);
		/*
		 * see showErrorDialog(title, msg, status) in UIUtils.java;
		 */
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final Shell shell = Display.getDefault().getActiveShell();
				MessageDialog.openError(shell, title, msg);
			}
		});
	}
}