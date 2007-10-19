/**
 * Copyright (c) 2007 ORNL and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the term of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * @author - Feiyi Wang
 * initial API and implementation
 * 
 */
 
package org.eclipse.ptp.ui.consoles;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.elements.IPQueue;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.events.IChangedJobEvent;
import org.eclipse.ptp.core.elements.events.IChangedMachineEvent;
import org.eclipse.ptp.core.elements.events.IChangedQueueEvent;
import org.eclipse.ptp.core.elements.events.INewJobEvent;
import org.eclipse.ptp.core.elements.events.INewMachineEvent;
import org.eclipse.ptp.core.elements.events.INewQueueEvent;
import org.eclipse.ptp.core.elements.events.IRemoveJobEvent;
import org.eclipse.ptp.core.elements.events.IRemoveMachineEvent;
import org.eclipse.ptp.core.elements.events.IRemoveQueueEvent;
import org.eclipse.ptp.core.elements.listeners.IQueueChildListener;
import org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener;
import org.eclipse.ptp.core.events.IChangedResourceManagerEvent;
import org.eclipse.ptp.core.events.INewResourceManagerEvent;
import org.eclipse.ptp.core.events.IRemoveResourceManagerEvent;
import org.eclipse.ptp.core.listeners.IModelManagerChildListener;

public class ConsoleManager implements IModelManagerChildListener,
		IResourceManagerChildListener, IQueueChildListener {
	
	// TODO set this flag from preferences
	private boolean createJobConsole = false; // Flag indicating if a console should be opened on job creation
	private IModelManager imm = null;
	private Map<IPJob, JobConsole> consoles = new HashMap<IPJob, JobConsole>();
	
	public ConsoleManager() {
		imm = PTPCorePlugin.getDefault().getModelManager();
		imm.addListener(this);
		for (IResourceManager rm: imm.getUniverse().getResourceManagers()) {
			rm.addChildListener(this);
			for (IPQueue queue: rm.getQueues()) {
				queue.addChildListener(this);
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IQueueChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IChangedJobEvent)
	 */
	public void handleEvent(IChangedJobEvent e) {
		// OK to ignore
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IChangedMachineEvent)
	 */
	public void handleEvent(IChangedMachineEvent e) {
		// Nothing to do
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IChangedQueueEvent)
	 */
	public void handleEvent(IChangedQueueEvent e) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.listeners.IModelManagerChildListener#handleEvent(org.eclipse.ptp.core.events.IChangedResourceManagerEvent)
	 */
	public void handleEvent(IChangedResourceManagerEvent e) {
		// Nothing to do
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IQueueChildListener#handleEvent(org.eclipse.ptp.core.elements.events.INewJobEvent)
	 */
	public void handleEvent(INewJobEvent e) {
		if (createJobConsole) {
			synchronized (consoles) {
				for (IPJob job: e.getJobs()) {
					JobConsole jc = new JobConsole(job);
					job.addChildListener(jc);
					consoles.put(job, jc);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.INewMachineEvent)
	 */
	public void handleEvent(INewMachineEvent e) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.INewQueueEvent)
	 */
	public void handleEvent(INewQueueEvent e) {
		for (IPQueue queue: e.getQueues()) {
			queue.addChildListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.listeners.IModelManagerChildListener#handleEvent(org.eclipse.ptp.core.events.INewResourceManagerEvent)
	 */
	public void handleEvent(INewResourceManagerEvent e) {
		e.getResourceManager().addChildListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IQueueChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IRemoveJobEvent)
	 */
	public void handleEvent(IRemoveJobEvent e) {
		for (IPJob job: e.getJobs()) {
			removeConsole(job);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IRemoveMachineEvent)
	 */
	public void handleEvent(IRemoveMachineEvent e) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.elements.listeners.IResourceManagerChildListener#handleEvent(org.eclipse.ptp.core.elements.events.IRemoveQueueEvent)
	 */
	public void handleEvent(IRemoveQueueEvent e) {
		for (IPQueue queue: e.getQueues()) {
			queue.removeChildListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ptp.core.listeners.IModelManagerChildListener#handleEvent(org.eclipse.ptp.core.events.IRemoveResourceManagerEvent)
	 */
	public void handleEvent(IRemoveResourceManagerEvent e) {
		e.getResourceManager().removeChildListener(this);
	}

	/**
	 * Shut down the console manager. This removes any job consoles that
	 * have been created and all listeners.
	 */
	public void shutdown() {
		imm.removeListener(this);
		for (IResourceManager rm: imm.getUniverse().getResourceManagers()) {
			rm.removeChildListener(this);
			for (IPQueue queue: rm.getQueues()) {
				queue.removeChildListener(this);
				for (IPJob job : queue.getJobs()) {
					removeConsole(job);
				}
			}
		}		
	}

	/**
	 * Convenience method to remove the console associated with a job.
	 * 
	 * @param job job that has an associated console
	 */
	private void removeConsole(IPJob job) {
		synchronized (consoles) {
			JobConsole jc = consoles.get(job);
			if (jc != null) {
				jc.removeConsole();
				job.removeChildListener(jc);
				consoles.remove(jc);
			}
		}
	}
}
