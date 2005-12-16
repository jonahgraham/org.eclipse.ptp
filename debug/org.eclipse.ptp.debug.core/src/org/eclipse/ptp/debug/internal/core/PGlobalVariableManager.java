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
package org.eclipse.ptp.debug.internal.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.debug.core.PTPDebugCorePlugin;
import org.eclipse.ptp.debug.core.model.IGlobalVariableDescriptor;
import org.eclipse.ptp.debug.core.model.IPGlobalVariable;
import org.eclipse.ptp.debug.core.model.IPGlobalVariableManager;
import org.eclipse.ptp.debug.internal.core.model.PDebugTarget;
import org.eclipse.ptp.debug.internal.core.model.PVariable;
import org.eclipse.ptp.debug.internal.core.model.PVariableFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Clement chu
 * 
 */
public class PGlobalVariableManager implements IPGlobalVariableManager {
	private static final String GLOBAL_VARIABLE_LIST = "globalVariableList";
	private static final String GLOBAL_VARIABLE = "globalVariable";
	private static final String ATTR_GLOBAL_VARIABLE_PATH = "path";
	private static final String ATTR_GLOBAL_VARIABLE_NAME = "name";
	private PDebugTarget fDebugTarget;
	private IGlobalVariableDescriptor[] fInitialDescriptors = new IGlobalVariableDescriptor[0];
	private ArrayList fGlobals;

	public PGlobalVariableManager(PDebugTarget target) {
		super();
		setDebugTarget(target);
		initialize();
	}
	protected PDebugTarget getDebugTarget() {
		return fDebugTarget;
	}
	private void setDebugTarget(PDebugTarget debugTarget) {
		fDebugTarget = debugTarget;
	}
	public IPGlobalVariable[] getGlobals() {
		if (fGlobals == null) {
			try {
				addGlobals(getInitialDescriptors());
			} catch (DebugException e) {
				DebugPlugin.log(e);
			}
		}
		return (IPGlobalVariable[]) fGlobals.toArray(new IPGlobalVariable[fGlobals.size()]);
	}
	public void addGlobals(IGlobalVariableDescriptor[] descriptors) throws DebugException {
		fGlobals = new ArrayList(10);
		MultiStatus ms = new MultiStatus(PTPDebugCorePlugin.getUniqueIdentifier(), 0, "", null);
		ArrayList globals = new ArrayList(descriptors.length);
		for (int i = 0; i < descriptors.length; ++i) {
			try {
				globals.add(getDebugTarget().createGlobalVariable(descriptors[i]));
			} catch (DebugException e) {
				ms.add(e.getStatus());
			}
		}
		if (globals.size() > 0) {
			synchronized (fGlobals) {
				fGlobals.addAll(globals);
			}
			getDebugTarget().fireChangeEvent(DebugEvent.CONTENT);
		}
		if (!ms.isOK()) {
			throw new DebugException(ms);
		}
	}
	public void removeGlobals(IPGlobalVariable[] globals) {
		synchronized (fGlobals) {
			fGlobals.removeAll(Arrays.asList(globals));
		}
		for (int i = 0; i < globals.length; ++i) {
			if (globals[i] instanceof PVariable)
				((PVariable) globals[i]).dispose();
		}
		getDebugTarget().fireChangeEvent(DebugEvent.CONTENT);
	}
	public void removeAllGlobals() {
		IPGlobalVariable[] globals = new IPGlobalVariable[0];
		synchronized (fGlobals) {
			globals = (IPGlobalVariable[]) fGlobals.toArray(new IPGlobalVariable[fGlobals.size()]);
			fGlobals.clear();
		}
		for (int i = 0; i < globals.length; ++i) {
			if (globals[i] instanceof PVariable)
				((PVariable) globals[i]).dispose();
		}
		getDebugTarget().fireChangeEvent(DebugEvent.CONTENT);
	}
	public void dispose() {
		if (fGlobals != null) {
			Iterator it = fGlobals.iterator();
			while (it.hasNext()) {
				((PVariable) it.next()).dispose();
			}
			fGlobals.clear();
			fGlobals = null;
		}
	}
	public String getMemento() {
		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element node = document.createElement(GLOBAL_VARIABLE_LIST);
			document.appendChild(node);
			IPGlobalVariable[] globals = getGlobals();
			for (int i = 0; i < globals.length; ++i) {
				IGlobalVariableDescriptor descriptor = globals[i].getDescriptor();
				Element child = document.createElement(GLOBAL_VARIABLE);
				child.setAttribute(ATTR_GLOBAL_VARIABLE_NAME, descriptor.getName());
				child.setAttribute(ATTR_GLOBAL_VARIABLE_PATH, descriptor.getPath().toOSString());
				node.appendChild(child);
			}
			return CDebugUtils.serializeDocument(document);
		} catch (ParserConfigurationException e) {
			DebugPlugin.log(e);
		} catch (IOException e) {
			DebugPlugin.log(e);
		} catch (TransformerException e) {
			DebugPlugin.log(e);
		}
		return null;
	}
	private void initializeFromMemento(String memento) throws CoreException {
		Exception ex = null;
		try {
			Element root = null;
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			StringReader reader = new StringReader(memento);
			InputSource source = new InputSource(reader);
			root = parser.parse(source).getDocumentElement();
			if (root.getNodeName().equalsIgnoreCase(GLOBAL_VARIABLE_LIST)) {
				List descriptors = new ArrayList();
				NodeList list = root.getChildNodes();
				int length = list.getLength();
				for (int i = 0; i < length; ++i) {
					Node node = list.item(i);
					short type = node.getNodeType();
					if (type == Node.ELEMENT_NODE) {
						Element entry = (Element) node;
						if (entry.getNodeName().equalsIgnoreCase(GLOBAL_VARIABLE)) {
							String name = entry.getAttribute(ATTR_GLOBAL_VARIABLE_NAME);
							String pathString = entry.getAttribute(ATTR_GLOBAL_VARIABLE_PATH);
							IPath path = new Path(pathString);
							if (path.isValidPath(pathString)) {
								descriptors.add(PVariableFactory.createGlobalVariableDescriptor(name, path));
							}
						}
					}
				}
				fInitialDescriptors = (IGlobalVariableDescriptor[]) descriptors.toArray(new IGlobalVariableDescriptor[descriptors.size()]);
				return;
			}
		} catch (ParserConfigurationException e) {
			ex = e;
		} catch (SAXException e) {
			ex = e;
		} catch (IOException e) {
			ex = e;
		}
		abort(InternalDebugCoreMessages.getString("CGlobalVariableManager.0"), ex);
	}
	private void initialize() {
		ILaunchConfiguration config = getDebugTarget().getLaunch().getLaunchConfiguration();
		try {
			String memento = config.getAttribute(IPTPLaunchConfigurationConstants.ATTR_DEBUGGER_GLOBAL_VARIABLES, "");
			if (memento != null && memento.trim().length() != 0)
				initializeFromMemento(memento);
		} catch (CoreException e) {
			DebugPlugin.log(e);
		}
	}
	private void abort(String message, Throwable e) throws CoreException {
		IStatus s = new Status(IStatus.ERROR, PTPDebugCorePlugin.getUniqueIdentifier(), PTPDebugCorePlugin.INTERNAL_ERROR, message, e);
		throw new CoreException(s);
	}
	private IGlobalVariableDescriptor[] getInitialDescriptors() {
		return fInitialDescriptors;
	}
	public void save() {
		ILaunchConfiguration config = getDebugTarget().getLaunch().getLaunchConfiguration();
		try {
			ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
			wc.setAttribute(IPTPLaunchConfigurationConstants.ATTR_DEBUGGER_GLOBAL_VARIABLES, getMemento());
			wc.doSave();
		} catch (CoreException e) {
			DebugPlugin.log(e);
		}
	}
	public IGlobalVariableDescriptor[] getDescriptors() {
		if (fGlobals == null)
			return getInitialDescriptors();
		IGlobalVariableDescriptor[] result = new IGlobalVariableDescriptor[fGlobals.size()];
		Iterator it = fGlobals.iterator();
		for (int i = 0; it.hasNext(); ++i) {
			result[i] = ((IPGlobalVariable) it.next()).getDescriptor();
		}
		return result;
	}
}
