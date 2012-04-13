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

package org.eclipse.ptp.core.elements;

import org.eclipse.ptp.core.jobs.IJobControl;
import org.eclipse.ptp.core.jobs.IJobStatus;

/**
 * A Universe represents a user's view of the world. It is comprised of a set of Resource Managers.
 * 
 * @author Nathan DeBardeleben
 * @see IPResourceManager
 */
public interface IPUniverse extends IPElement {
	/**
	 * @param id
	 *            of controller
	 * @since 6.0
	 */
	public IPResourceManager addResourceManager(String controlId);

	/**
	 * @since 6.0
	 */
	public IPJob getJob(IJobControl control, String jobId);

	/**
	 * @since 6.0
	 */
	public IPJob getJob(IJobStatus status);

	/**
	 * @since 5.0
	 */
	public String getNextResourceManagerId();

	/**
	 * @since 6.0
	 */
	public IPNode getNode(IJobControl control, String nodeId);

	/**
	 * @param controlId
	 * @return
	 * @since 6.0
	 */
	public IPResourceManager getResourceManager(String controlId);

	/**
	 * @return IPResourceManager[]
	 * @since 5.0
	 */
	public IPResourceManager[] getResourceManagers();

	/**
	 * @since 6.0
	 */
	public void removeResourceManager(String controlId);

}
