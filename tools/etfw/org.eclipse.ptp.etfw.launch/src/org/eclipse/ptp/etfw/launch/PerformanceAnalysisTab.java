/*******************************************************************************
 * Copyright (c) 2012 University of Illinois and others.  All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *   
 * Contributors: 
 * 		Chris Navarro (Illinois/NCSA) - Design and implementation
 *******************************************************************************/
package org.eclipse.ptp.etfw.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.ptp.core.util.LaunchUtils;
import org.eclipse.ptp.etfw.Activator;
import org.eclipse.ptp.etfw.IToolLaunchConfigurationConstants;
import org.eclipse.ptp.etfw.PreferenceConstants;
import org.eclipse.ptp.etfw.Preferences;
import org.eclipse.ptp.etfw.jaxb.JAXBInitializationUtil;
import org.eclipse.ptp.etfw.jaxb.data.BuildToolType;
import org.eclipse.ptp.etfw.jaxb.data.EtfwToolProcessType;
import org.eclipse.ptp.etfw.jaxb.data.ExecToolType;
import org.eclipse.ptp.etfw.jaxb.data.ToolAppType;
import org.eclipse.ptp.etfw.jaxb.data.ToolPaneType;
import org.eclipse.ptp.etfw.jaxb.util.JAXBExtensionUtils;
import org.eclipse.ptp.etfw.launch.messages.Messages;
import org.eclipse.ptp.etfw.launch.ui.util.ETFWToolTabBuilder;
import org.eclipse.ptp.etfw.launch.variables.ETFWVariableMap;
import org.eclipse.ptp.etfw.ui.ExternalToolSelectionTab;
import org.eclipse.ptp.internal.rm.jaxb.control.ui.launch.IJAXBLaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationContentsChangedListener;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.launch.ui.extensions.JAXBDynamicLaunchConfigurationTab;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteConnectionManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.RemoteServices;
import org.eclipse.ptp.rm.jaxb.control.core.ILaunchController;
import org.eclipse.ptp.rm.jaxb.control.core.LaunchControllerManager;
import org.eclipse.ptp.rm.jaxb.control.ui.IUpdateModel;
import org.eclipse.ptp.rm.jaxb.core.data.AttributeType;
import org.eclipse.ptp.rm.jaxb.core.data.CommandType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Notes: This class is a re-implementation of ParallelToolSelectionTab using JAXB to dynamically build the UI.
 * TODO Review this class to see if some of what is done could be replaced with the JAXB provided classes or done as an extension of
 * the JAXB parent classes instead of what has been done with Some of the functionality in this class belongs in
 * ETFWParentLaunchConfiguration
 * 
 * @author Chris Navarro
 * 
 */
public class PerformanceAnalysisTab extends AbstractLaunchConfigurationTab implements IToolLaunchConfigurationConstants {

	private static final String TAB_ID = "org.eclipse.ptp.etfw.launch.PerformanceAnalysisTab"; //$NON-NLS-1$
	/**
	 * Determines if the launch configuration associated with this tab has
	 * access to the PTP
	 */
	protected boolean noPTP = false;
	private EtfwToolProcessType etfwTool;
	private ETFWParentLaunchConfigurationTab launchTabParent;
	private ETFWVariableMap vmap;
	private ILaunchConfiguration launchConfiguration = null;
	private ILaunchController controller;
	private final WidgetListener listener = new WidgetListener();
	private List<ToolPaneType> toolTabs;

	private Composite topComposite;
	private Composite toolComposite;
	private Composite bottomComposite;
	private Combo toolCombo;
	private Label selectToolLbl;
	private Button buildOnlyCheck;
	private Button analyzeonlyCheck;
	private String controlId;
	// I believe this should be part of the launchTabParent, but there is RM specifics that must be removed
	private final LinkedList<IJAXBLaunchConfigurationTab> tabControllers = new LinkedList<IJAXBLaunchConfigurationTab>();
	private final ContentsChangedListener launchContentsChangedListener = new ContentsChangedListener();

	// Sax Parser ETFW
	private ExternalToolSelectionTab saxETFWTab;

	public PerformanceAnalysisTab() {
		this(true);
	}

	public PerformanceAnalysisTab(boolean noPar) {
		noPTP = noPar;
	}

	/**
	 * Generates the UI for the analysis tab, consisting of sub-tabs which may be
	 * dynamically generated
	 * 
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		setControl(content);

		GridLayout layout = new GridLayout();
		content.setLayout(layout);

		topComposite = new Composite(content, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		toolComposite = new Composite(content, SWT.NONE);
		toolComposite.setLayout(new FillLayout());
		toolComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		bottomComposite = new Composite(content, SWT.NONE);
		bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bottomComposite.setLayout(new GridLayout(1, false));

		buildOnlyCheck = new Button(bottomComposite, SWT.CHECK);
		buildOnlyCheck.setText(Messages.PerformanceAnalysisTab_BuildInstrumentedExecutable);

		analyzeonlyCheck = new Button(bottomComposite, SWT.CHECK);
		analyzeonlyCheck.setText(Messages.PerformanceAnalysisTab_SelectExistingPerfData);

		String parser = Preferences.getString(Activator.PLUGIN_ID, PreferenceConstants.ETFW_VERSION);
		if (parser.equals(IToolLaunchConfigurationConstants.USE_SAX_PARSER)) {
			buildSAXParserUI();
		} else {
			buildJAXBParserUI();
		}
	}

	private void buildSAXParserUI() {
		bottomComposite.setVisible(false);

		saxETFWTab = new ExternalToolSelectionTab(true);
		saxETFWTab.createControl(toolComposite);
		saxETFWTab.setLaunchConfigurationDialog(this.getLaunchConfigurationDialog());

		toolComposite.getParent().layout();
		toolComposite.layout();
	}

	private void buildJAXBParserUI() {
		selectToolLbl = new Label(topComposite, SWT.NONE);
		selectToolLbl.setText(Messages.PerformanceAnalysisTab_SelectTool);

		toolCombo = new Combo(topComposite, SWT.READ_ONLY);
		toolCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		String[] toolNames = JAXBExtensionUtils.getToolNames();
		toolCombo.add(Messages.PerformanceAnalysisTab_PleaseSelectWorkflow);

		for (String name : toolNames) {
			toolCombo.add(name);
		}

		toolCombo.addSelectionListener(listener);

		toolComposite.getParent().layout();
		topComposite.layout();
		toolComposite.layout();
	}

	/**
	 * Helper to add a read-only attribute
	 * 
	 * @param name
	 * @param value
	 */
	private void addAttribute(String name, String value) {
		AttributeType attr = vmap.get(name);
		if (attr == null) {
			attr = new AttributeType();
			attr.setName(name);
			attr.setVisible(true);
			attr.setReadOnly(true);
			vmap.put(name, attr);
		}
		attr.setValue(value);
	}

	/**
	 * Add connection properties to the attribute map.
	 * 
	 * @param conn
	 */
	private void setConnectionPropertyAttributes(IRemoteConnection conn) {
		String property = conn.getProperty(IRemoteConnection.OS_ARCH_PROPERTY);
		if (property != null) {
			addAttribute(IRemoteConnection.OS_ARCH_PROPERTY, property);
		}
		property = conn.getProperty(IRemoteConnection.OS_NAME_PROPERTY);
		if (property != null) {
			addAttribute(IRemoteConnection.OS_NAME_PROPERTY, property);
		}
		property = conn.getProperty(IRemoteConnection.OS_VERSION_PROPERTY);
		if (property != null) {
			addAttribute(IRemoteConnection.OS_VERSION_PROPERTY, property);
		}
	}

	private void rebuildTab(String toolName) {
		if (toolName.equals(Messages.PerformanceAnalysisTab_PleaseSelectWorkflow)) {
			clearOldWidgets();
			return;
		}

		etfwTool = JAXBExtensionUtils.getTool(toolName);
		vmap = new ETFWVariableMap();

		JAXBInitializationUtil.initializeMap(etfwTool, vmap);

		// Add in connection property attributes to ETFW variable map
		final IRemoteServices services = RemoteServices.getRemoteServices(controller.getRemoteServicesId());
		if (services != null) {
			final IRemoteConnectionManager connMgr = services.getConnectionManager();
			IRemoteConnection remoteConnection = connMgr.getConnection(controller.getConnectionName());
			if (remoteConnection != null) {
				setConnectionPropertyAttributes(remoteConnection);
			}
		}

		try {
			launchTabParent = new ETFWParentLaunchConfigurationTab(controller, new NullProgressMonitor(), vmap);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		launchTabParent.addContentsChangedListener(launchContentsChangedListener);

		for (IJAXBLaunchConfigurationTab tabControl : tabControllers) {
			tabControl.getLocalWidgets().clear();
		}

		tabControllers.clear();
		for (Control control : toolComposite.getChildren()) {
			control.dispose();
		}

		if (etfwTool.getControlData() != null) {
			for (CommandType command : etfwTool.getControlData().getInitializeCommand()) {
				if (command != null) {
					try {
						controller.runCommand(command, vmap);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}

			etfwTool.getControlData().getInitializeCommand();
		}

		final TabFolder tabParent = new TabFolder(toolComposite, SWT.NONE);

		toolTabs = findTabControllers();

		for (ToolPaneType toolTab : toolTabs) {
			tabControllers.add(new JAXBDynamicLaunchConfigurationTab(controller, toolTab.getOptionPane(), launchTabParent));
		}

		ETFWToolTabBuilder.initialize();

		for (IJAXBLaunchConfigurationTab tabControl : tabControllers) {
			ETFWToolTabBuilder builder = new ETFWToolTabBuilder(tabControl, vmap);

			TabItem tabItem = new TabItem(tabParent, SWT.NONE);
			Control control = null;
			final ScrolledComposite scroller = new ScrolledComposite(tabParent, SWT.V_SCROLL | SWT.H_SCROLL);
			try {
				control = builder.build(scroller);
				((Composite) control).layout();

				tabItem.setText(tabControl.getController().getTitle());
				String tooltip = tabControl.getController().getTooltip();
				if (tooltip != null) {
					tabItem.setToolTipText(tooltip);
					scroller.setToolTipText(tooltip);
				}

				scroller.setContent(control);
				scroller.setExpandHorizontal(true);
				scroller.setExpandVertical(true);
				scroller.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));

				tabItem.setControl(scroller);
			} catch (Throwable t) {
				t.printStackTrace();
			}

		}

		handleUpdate();
	}

	/**
	 * Determines if the UI widget is enabled and should be included in the launch configuration. It prevents attributes that are
	 * not enabled from getting included in the launch configuration
	 * 
	 * @param attributeName
	 *            Name of the attribute associated with the widget
	 * @return enabled state of widget
	 */
	public boolean isWidgetEnabled(String attributeName) {
		for (IJAXBLaunchConfigurationTab tabControl : tabControllers) {
			for (IUpdateModel m : tabControl.getLocalWidgets().values()) {
				if (m.getName() != null) {
					if (m.getName().equals(attributeName)) {
						return ((Control) m.getControl()).isEnabled();
					}
				} else {
					// Do nothing, the model has no attribute associated with it
				}
			}
		}
		// Handles the case where attributes are not associated with UI models
		return true;
	}

	private void handleUpdate() {
		launchTabParent.initializeFrom(this.launchConfiguration);

		for (IJAXBLaunchConfigurationTab tabControl : tabControllers) {
			for (IUpdateModel m : tabControl.getLocalWidgets().values()) {
				m.initialize(this.launchConfiguration, vmap, launchTabParent.getVariableMap());
			}

			((JAXBDynamicLaunchConfigurationTab) tabControl).initializeFrom(this.launchConfiguration);
		}
	}

	private List<ToolPaneType> findTabControllers() {
		List<ToolPaneType> subTabs = new ArrayList<ToolPaneType>();
		for (Object tool : etfwTool.getExecToolOrAnalysisToolOrBuildTool()) {
			if (tool instanceof BuildToolType) {
				BuildToolType buildTool = (BuildToolType) tool;
				if (buildTool.getGlobal() != null) {
					for (ToolPaneType toolPane : buildTool.getGlobal().getToolPanes()) {
						if (!toolPane.isVirtual() && toolPane.getOptionPane() != null) {
							subTabs.add(toolPane);
						}
					}
				}

				if (buildTool.getAllCompilers() != null) {
					for (ToolPaneType toolPane : buildTool.getAllCompilers().getToolPanes()) {
						if (!toolPane.isVirtual() && toolPane.getOptionPane() != null) {
							subTabs.add(toolPane);
						}
					}
				}
			} else if (tool instanceof ExecToolType) {
				ExecToolType execTool = (ExecToolType) tool;
				if (execTool.getGlobal() != null) {
					for (ToolPaneType toolPane : execTool.getGlobal().getToolPanes()) {
						if (!toolPane.isVirtual() && toolPane.getOptionPane() != null) {
							subTabs.add(toolPane);
						}
					}
				}

				for (ToolAppType toolApp : execTool.getExecUtils()) {
					for (ToolPaneType toolPane : toolApp.getToolPanes()) {
						if (!toolPane.isVirtual() && toolPane.getOptionPane() != null) {
							subTabs.add(toolPane);
						}
					}
				}
			}
		}
		return subTabs;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// Do nothing
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy configuration) {
		super.activated(configuration);

		if (toolCombo != null) {
			try {
				String toolName = configuration.getAttribute(IToolLaunchConfigurationConstants.SELECTED_TOOL,
						IToolLaunchConfigurationConstants.EMPTY_STRING);
				for (int index = 0; index < toolCombo.getItemCount(); index++) {
					if (toolCombo.getItem(index).equals(toolName)) {
						toolCombo.select(index);
						toolCombo.notifyListeners(SWT.Selection, null);
						break;
					}
				}

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		launchConfiguration = configuration;
		final String rmType = LaunchUtils.getTemplateName(configuration);
		final String remId = LaunchUtils.getRemoteServicesId(configuration);
		final String remName = LaunchUtils.getConnectionName(configuration);
		try {
			controller = LaunchControllerManager.getInstance().getLaunchController(remId, remName, rmType);
			if (controller != null) {
				controlId = controller.getControlId();

				String parser = Preferences.getString(Activator.PLUGIN_ID, PreferenceConstants.ETFW_VERSION);
				if (parser.equals(IToolLaunchConfigurationConstants.USE_SAX_PARSER)) {
					saxETFWTab.initializeFrom(configuration);
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String parser = Preferences.getString(Activator.PLUGIN_ID, PreferenceConstants.ETFW_VERSION);
		configuration.setAttribute(IToolLaunchConfigurationConstants.ETFW_VERSION, parser);
		if (parser.equals(IToolLaunchConfigurationConstants.USE_SAX_PARSER)) {
			saxETFWTab.performApply(configuration);
		} else {
			if (toolCombo.getSelectionIndex() > 0) {
				String selectedtool = toolCombo.getItem(toolCombo.getSelectionIndex());
				configuration.setAttribute(SELECTED_TOOL, selectedtool);

				configuration.setAttribute(BUILDONLY, buildOnlyCheck.getSelection());
				configuration.setAttribute(ANALYZEONLY, analyzeonlyCheck.getSelection());

				Iterator<String> iterator = launchTabParent.getVariableMap().getAttributes().keySet().iterator();

				launchTabParent.performApply(configuration);

				// TODO the above performApply should update the attribute map, but it doesn't right now so it is handled below
				while (iterator.hasNext()) {
					String attribute = iterator.next();
					String name = attribute;
					if (name.startsWith(controlId)) {
						name = name.substring(controlId.length() + 1, attribute.length());

						// Check to see if the variable is part of ETFw
						AttributeType temp = vmap.getAttributes().get(name);
						if (temp != null) {
							if (isWidgetEnabled(temp.getName()) && temp.isVisible()) {
								String attType = temp.getType();

								// If boolean is translated to a string, insert the string into the launch configuration
								String translateBoolean = vmap.getAttributes().get(name).getTranslateBooleanAs();
								Object value = launchTabParent.getVariableMap().getValue(name);
								if (attType.equals("boolean")) { //$NON-NLS-1$
									if (translateBoolean != null) {
										configuration.setAttribute(attribute, value.toString());
									} else {
										boolean val = new Boolean(value.toString());
										configuration.setAttribute(attribute, val);
									}
								} else if (attType.equals("string")) { //$NON-NLS-1$
									configuration.setAttribute(attribute, value.toString());
								} else if (attType.equals("integer")) { //$NON-NLS-1$
									int val = new Integer(value.toString());
									configuration.setAttribute(attribute, val);
								} else {
									configuration.setAttribute(attribute, value.toString());
								}
							}
						}
					}
				}

			}
		}
	}

	protected class WidgetListener extends SelectionAdapter {
		private String prevToolName;

		@Override
		public void widgetSelected(SelectionEvent e) {
			int selection = toolCombo.getSelectionIndex();
			String toolName = toolCombo.getItem(selection);
			if (!toolName.equals(prevToolName)) {
				prevToolName = toolName;
				rebuildTab(toolName);
			}

			updateLaunchConfigurationDialog();
		}

	}

	private void clearOldWidgets() {
		for (Control child : toolComposite.getChildren()) {
			child.dispose();
		}
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return Messages.PerformanceAnalysisTab_Tab_Name;
	}

	@Override
	public String getId() {
		return TAB_ID;
	}

	/**
	 * @see ILaunchConfigurationTab#setLaunchConfigurationDialog (ILaunchConfigurationDialog)
	 */
	@Override
	public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
	}

	@Override
	public boolean isValid(ILaunchConfiguration configuration) {
		setErrorMessage(null);
		if (toolCombo != null) {
			/* If nothing has ever been selected then its valid so don't report an error */
			if (toolCombo.getSelectionIndex() == -1) {
				return true;
			} else if (toolCombo.getSelectionIndex() == 0) {
				setErrorMessage(Messages.PerformanceAnalysisTab_NoWorkflowSelected);
				return false;
			}
		} else if (launchTabParent != null) {
			String error = launchTabParent.getUpdateHandler().getFirstError();
			if (error != null) {
				setErrorMessage(error);
				return false;
			}
		}

		return true;
	}

	private final class ContentsChangedListener implements IRMLaunchConfigurationContentsChangedListener {

		@Override
		public void handleContentsChanged(IRMLaunchConfigurationDynamicTab rmDynamicTab) {
			// The buttons and messages have to be updated based on anything
			// that has changed in the dynamic portion of the launch tab.
			updateLaunchConfigurationDialog();
		}
	}

}
