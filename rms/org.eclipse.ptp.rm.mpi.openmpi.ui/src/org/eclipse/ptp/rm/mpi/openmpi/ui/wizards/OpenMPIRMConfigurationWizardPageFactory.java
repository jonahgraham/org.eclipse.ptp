/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.mpi.openmpi.ui.wizards;

import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.OpenMPIResourceManagerFactory;
import org.eclipse.ptp.ui.wizards.RMConfigurationWizard;
import org.eclipse.ptp.ui.wizards.RMConfigurationWizardPage;
import org.eclipse.ptp.ui.wizards.RMConfigurationWizardPageFactory;


public class OpenMPIRMConfigurationWizardPageFactory extends
	RMConfigurationWizardPageFactory {

	public OpenMPIRMConfigurationWizardPageFactory() {
		// no-op
	}

	public RMConfigurationWizardPage[] getPages(RMConfigurationWizard wizard) {
		return new RMConfigurationWizardPage[] {
				new OpenMPIRMConfigurationWizardPage(wizard),
				new OpenMPIToolConfigurationWizardPage(wizard) };
	}

	public Class<OpenMPIResourceManagerFactory> getRMFactoryClass() {
		return OpenMPIResourceManagerFactory.class;
	}
}
