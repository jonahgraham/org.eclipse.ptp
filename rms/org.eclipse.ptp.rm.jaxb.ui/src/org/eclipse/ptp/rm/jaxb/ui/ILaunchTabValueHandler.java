/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.ui;

import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.rm.jaxb.core.variables.LTVariableMap;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;

public interface ILaunchTabValueHandler {

	void getValuesFromMap(LTVariableMap ltMap);

	void setDefaultValuesOnControl(RMVariableMap rmMap);

	void setValuesOnMap(LTVariableMap ltMap);

	void validateControlValues(RMVariableMap rmMap, IRemoteFileManager manager) throws Throwable;

}