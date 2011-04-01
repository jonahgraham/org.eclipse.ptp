/**
 * Copyright (c) 2011 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 */
package org.eclipse.ptp.rm.jaxb.ui.wizards;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.ui.wizards.RMConfigurationSelectionFactory;
import org.osgi.framework.Bundle;

public class JAXBRMConfigurationSelectionFactory extends RMConfigurationSelectionFactory {
	private static String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
	private static String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static String CONFIGURATION_FILE_ATTRIBUTE = "configurationFile"; //$NON-NLS-1$
	private static String EXTENSION_POINT = "org.eclipse.ptp.rm.jaxb.core.JAXBResourceManagerConfigurations"; //$NON-NLS-1$

	private static Map<String, Map<String, URL>> fRMJAXBResourceManagers = null;

	private static void loadJAXBResourceManagers() {
		if (fRMJAXBResourceManagers == null) {
			fRMJAXBResourceManagers = new HashMap<String, Map<String, URL>>();

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_POINT);

			if (extensionPoint != null) {
				for (IExtension ext : extensionPoint.getExtensions()) {
					for (IConfigurationElement ce : ext.getConfigurationElements()) {
						String id = ce.getAttribute(ID_ATTRIBUTE);
						Map<String, URL> info = fRMJAXBResourceManagers.get(id);
						if (info == null) {
							info = new HashMap<String, URL>();
							fRMJAXBResourceManagers.put(id, info);
						}
						String name = ce.getAttribute(NAME_ATTRIBUTE);
						String configurationFile = ce.getAttribute(CONFIGURATION_FILE_ATTRIBUTE);
						String bundleId = ce.getDeclaringExtension().getContributor().getName();
						Bundle bundle = Platform.getBundle(bundleId);
						if (bundle != null) {
							URL url = bundle.getEntry(configurationFile);
							if (url != null) {
								info.put(name, url);
							}
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.ui.wizards.RMConfigurationSelectionFactory#
	 * getConfigurationTypes()
	 */
	@Override
	public String[] getConfigurationNames() {
		return getJAXBResourceManagerNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.ui.wizards.RMConfigurationSelectionFactory#
	 * setConfigurationType(java.lang.String,
	 * org.eclipse.ptp.rmsystem.IResourceManagerConfiguration)
	 */
	@Override
	public void setConfigurationName(String name, IResourceManagerConfiguration configuration) {
		if (configuration instanceof IJAXBResourceManagerConfiguration) {
			((IJAXBResourceManagerConfiguration) configuration).setRMConfigurationURL(getJAXBResourceManagerConfiguration(name));
			configuration.setName(name);
		}
	}

	private URL getJAXBResourceManagerConfiguration(String name) {
		loadJAXBResourceManagers();
		Map<String, URL> info = fRMJAXBResourceManagers.get(getId());
		if (info != null) {
			return info.get(name);
		}
		return null;
	}

	private String[] getJAXBResourceManagerNames() {
		loadJAXBResourceManagers();
		Map<String, URL> info = fRMJAXBResourceManagers.get(getId());
		if (info != null) {
			return info.keySet().toArray(new String[0]);
		}
		return new String[0];
	}
}