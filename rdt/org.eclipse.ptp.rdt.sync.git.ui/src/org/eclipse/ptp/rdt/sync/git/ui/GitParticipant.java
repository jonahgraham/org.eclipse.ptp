/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    John Eblen - initial implementation
 *******************************************************************************/

package org.eclipse.ptp.rdt.sync.git.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ptp.rdt.sync.core.CommandRunner;
import org.eclipse.ptp.rdt.sync.core.CommandRunner.CommandResults;
import org.eclipse.ptp.rdt.sync.core.RecursiveSubMonitor;
import org.eclipse.ptp.rdt.sync.core.RemoteExecutionException;
import org.eclipse.ptp.rdt.sync.core.RemoteSyncException;
import org.eclipse.ptp.rdt.sync.core.serviceproviders.ISyncServiceProvider;
import org.eclipse.ptp.rdt.sync.core.services.IRemoteSyncServiceConstants;
import org.eclipse.ptp.rdt.sync.git.ui.messages.Messages;
import org.eclipse.ptp.rdt.sync.git.core.GitServiceProvider;
import org.eclipse.ptp.rdt.sync.ui.ISynchronizeParticipant;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remote.ui.IRemoteUIConnectionManager;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Launches a dialog that configures a remote sync target with OK and Cancel
 * buttons. Also has a text field to allow the name of the configuration to be
 * changed.
 */
public class GitParticipant implements ISynchronizeParticipant {
	private static final String FILE_SCHEME = "file"; //$NON-NLS-1$
	private static final String TOUCH_TEST_FILE = ".touch_test_file_ptp_sync";
	private static final Display display = Display.getCurrent();

	// private IServiceConfiguration fConfig;
	private IRemoteConnection fSelectedConnection;
	private IRemoteServices fSelectedProvider;
	private String fProjectName = ""; //$NON-NLS-1$

	private final Map<Integer, IRemoteServices> fComboIndexToRemoteServicesProviderMap = new HashMap<Integer, IRemoteServices>();
	private final Map<Integer, IRemoteConnection> fComboIndexToRemoteConnectionMap = new HashMap<Integer, IRemoteConnection>();

//	private Control fDialogControl;
//	private Point fDialogSize;
//	private Text fNameText;
	private IRunnableContext fContext;
	private Button fRemoteLocationBrowseButton;
	private Button fRemoteLocationValidationButton;
	private Button fGitLocationBrowseButton;
	private Button fUseGitDefaultLocationButton;
	private Button fGitLocationValidationButton;
	private Button fNewConnectionButton;
	private Combo fProviderCombo;
	private Combo fConnectionCombo;
	private Text fLocationText;
	private Text fGitLocationText;

	private IWizardContainer container;
	
	private boolean fGitValidated = false;
	private boolean fGitHasBeenTested = false;
	private String specificGitError = null;
	private boolean fRemoteValidated = false;
	private boolean fRemoteHasBeenTested = false;
	private String specificRemoteError = "";
	private String specificGitWarning = null;
	
	// If false, automatically select "Remote Tools" provider instead of letting the user select the provider.
	private boolean showProviderCombo = false;

	/**
	 * Attempt to open a connection.
	 */
	private void checkConnection() {
		IRemoteUIConnectionManager mgr = getUIConnectionManager();
		if (mgr != null) {
			mgr.openConnectionWithProgress(fConnectionCombo.getShell(), null, fSelectedConnection);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rdt.sync.ui.ISynchronizeParticipant#createConfigurationArea
	 * (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.jface.operation.IRunnableContext)
	 */
	public void createConfigurationArea(Composite parent, IRunnableContext c) {
		fContext = c;
		this.container = (IWizardContainer)fContext;
		final Composite configArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		configArea.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		configArea.setLayoutData(gd);

		if (showProviderCombo) {
			// Label for "Provider:"
			Label providerLabel = new Label(configArea, SWT.LEFT);
			providerLabel.setText(Messages.GitParticipant_remoteProvider);

			// combo for providers
			fProviderCombo = new Combo(configArea, SWT.DROP_DOWN | SWT.READ_ONLY);
			gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
			gd.horizontalSpan = 2;
			fProviderCombo.setLayoutData(gd);
			fProviderCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					handleServicesSelected();
				}
			});
		}

		// attempt to restore settings from saved state
		// IRemoteServices providerSelected = fProvider.getRemoteServices();

		// populate the combo with a list of providers
		IRemoteServices[] providers = PTPRemoteUIPlugin.getDefault().getRemoteServices(fContext);
		int toSelect = 0;

		for (int k = 0; k < providers.length; k++) {
			if (showProviderCombo) {
				fProviderCombo.add(providers[k].getName(), k);
			}
			if  (providers[k].getName().equals("Remote Tools")) { //$NON-NLS-1$
				toSelect = k;
			}
			fComboIndexToRemoteServicesProviderMap.put(k, providers[k]);
		}

		if (showProviderCombo) {
			fProviderCombo.select(toSelect);
		}
		fSelectedProvider = fComboIndexToRemoteServicesProviderMap.get(toSelect);

		// connection combo
		// Label for "Connection:"
		Label connectionLabel = new Label(configArea, SWT.LEFT);
		connectionLabel.setText(Messages.GitParticipant_connection);

		// combo for providers
		fConnectionCombo = new Combo(configArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		// set layout to grab horizontal space
		fConnectionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fConnectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleConnectionSelected();
			}
		});

		// populate the combo with a list of providers
		populateConnectionCombo(fConnectionCombo);

		// new connection button
		fNewConnectionButton = new Button(configArea, SWT.PUSH);
		fNewConnectionButton.setText(Messages.GitParticipant_new);
		gd = new GridData(GridData.END, GridData.CENTER, false, false);
		fNewConnectionButton.setLayoutData(gd);
		updateNewConnectionButtonEnabled(fNewConnectionButton);
		fNewConnectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRemoteUIConnectionManager connectionManager = getUIConnectionManager();
				if (connectionManager != null) {
					connectionManager.newConnection(fNewConnectionButton.getShell());
				}
				// refresh list of connections
				populateConnectionCombo(fConnectionCombo);
				update();
			}
		});

		// Remote directory location
		// Label for "Remote directory:"
		Label locationLabel = new Label(configArea, SWT.LEFT);
		locationLabel.setText(Messages.GitParticipant_location);

		// Remote directory textbox
		fLocationText = new Text(configArea, SWT.SINGLE | SWT.BORDER);
		fLocationText.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 250;
		fLocationText.setLayoutData(gd);
		fLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// MBSCustomPageManager.addPageProperty(REMOTE_SYNC_WIZARD_PAGE_ID,setGitLocation
				// PATH_PROPERTY, fLocationText.getText());
				setRemoteIsValid(false);
				fRemoteHasBeenTested = false;
				update();
			}
		});
		
		// Remote location browse button
		fRemoteLocationBrowseButton = new Button(configArea, SWT.PUSH);
		fRemoteLocationBrowseButton.setText(Messages.GitParticipant_browse);
		gd = new GridData(GridData.END, GridData.CENTER, false, false);
		fRemoteLocationBrowseButton.setLayoutData(gd);
		fRemoteLocationBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fSelectedConnection != null) {
					checkConnection();
					if (fSelectedConnection.isOpen()) {
						IRemoteUIServices remoteUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(fSelectedProvider);
						if (remoteUIServices != null) {
							IRemoteUIFileManager fileMgr = remoteUIServices.getUIFileManager();
							if (fileMgr != null) {
								fileMgr.setConnection(fSelectedConnection);
								String correctPath = fLocationText.getText();
								String selectedPath = fileMgr.browseDirectory(
										fLocationText.getShell(),
										"Project Location (" + fSelectedConnection.getName() + ")", correctPath, IRemoteUIConstants.NONE); //$NON-NLS-1$ //$NON-NLS-2$
								if (selectedPath != null) {
									fLocationText.setText(selectedPath);
								}
							}
						}
					}
				}
			}
		});
		
		// Remote location validation button
		fRemoteLocationValidationButton = new Button(configArea, SWT.PUSH);
		fRemoteLocationValidationButton.setText("Validate");
		gd = new GridData(GridData.END, GridData.CENTER, true, true);
		gd.horizontalSpan = 3;
		fRemoteLocationValidationButton.setLayoutData(gd);
		fRemoteLocationValidationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setRemoteIsValid(isRemoteValid());
				fRemoteHasBeenTested = true;
				update();
			}
		});
		
		// Git location
		// "Use default location" button
		fUseGitDefaultLocationButton = new Button(configArea, SWT.CHECK);
		fUseGitDefaultLocationButton.setText("Use default Git location");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		fUseGitDefaultLocationButton.setLayoutData(gd);
		fUseGitDefaultLocationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setGitLocation();
				update();
			}
		});
		fUseGitDefaultLocationButton.setSelection(false);

		// Git location label
		Label gitLocationLabel = new Label(configArea, SWT.NONE);
		gitLocationLabel.setText("Git location: ");

		// Git location entry field
		fGitLocationText = new Text(configArea, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 250;
		fGitLocationText.setLayoutData(gd);
		fGitLocationText.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		fGitLocationText.setEnabled(true);
		fGitLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setGitIsValid(false);
				fGitHasBeenTested = false;
				update();
			}
		});
		
		// Git location browse button
		fGitLocationBrowseButton = new Button(configArea, SWT.PUSH);
		fGitLocationBrowseButton.setText(Messages.GitParticipant_browse);
		fGitLocationBrowseButton.setEnabled(true);
		fGitLocationBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fSelectedConnection != null) {
					checkConnection();
					if (fSelectedConnection.isOpen()) {
						IRemoteUIServices remoteUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(fSelectedProvider);
						if (remoteUIServices != null) {
							IRemoteUIFileManager fileMgr = remoteUIServices.getUIFileManager();
							if (fileMgr != null) {
								fileMgr.setConnection(fSelectedConnection);
								String selectedPath = fileMgr.browseFile(
										fGitLocationText.getShell(),
										"Project Location (" + fSelectedConnection.getName() + ")", "/", IRemoteUIConstants.NONE); //$NON-NLS-1$ //$NON-NLS-2$
								if (selectedPath != null) {
									fGitLocationText.setText(selectedPath);
								}
							}
						}
					}
				}
			}
		});
		
		// Git location validation button
		fGitLocationValidationButton = new Button(configArea, SWT.PUSH);
		fGitLocationValidationButton.setText("Validate");
		gd = new GridData(GridData.END, GridData.CENTER, true, true);
		gd.horizontalSpan = 3;
		fGitLocationValidationButton.setLayoutData(gd);
		fGitLocationValidationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setGitIsValid(validateGit());
				fGitHasBeenTested = true;
				update();
			}
		});
		
		handleConnectionSelected();
	}

	/**
	 * Return the path we are going to display. If it is a file URI then remove
	 * the file prefix.
	 *
	 * Only do this if the connection is open. Otherwise we will attempt to
	 * connect to the first machine in the list, which is annoying.
	 * 
	 * @return String
	 */
	private String getDefaultPathDisplayString() {
//		String projectName = ""; //$NON-NLS-1$
		// IWizardPage page = getWizard().getStartingPage();
		// if (page instanceof CDTMainWizardPage) {
		// projectName = ((CDTMainWizardPage) page).getProjectName();
		// }
		if (fSelectedConnection != null && fSelectedConnection.isOpen()) {
			IRemoteFileManager fileMgr = fSelectedProvider.getFileManager(fSelectedConnection);
			URI defaultURI = fileMgr.toURI(fSelectedConnection.getWorkingDirectory());

			// Handle files specially. Assume a file if there is no project to
			// query
			if (defaultURI != null && defaultURI.getScheme().equals(FILE_SCHEME)) {
				return Platform.getLocation().append(fProjectName).toString();
			}
			if (defaultURI == null) {
				return ""; //$NON-NLS-1$
			}
			return new Path(defaultURI.getPath()).append(fProjectName).toString();
		}
		return ""; //$NON-NLS-1$
	}
	
	/** 
	 * @see ISynchronizeParticipant#getErrorMessage()
	 */
	public String getErrorMessage() {
		if (fSelectedProvider == null) 
			return Messages.GitParticipant_0;
		if (fSelectedConnection == null) 
			return Messages.GitParticipant_1;
		if (fLocationText.getText().length() == 0 ) 
			return Messages.GitParticipant_2;
		IRemoteFileManager fileManager = fSelectedProvider.getFileManager(fSelectedConnection);
		if (fileManager.toURI(fLocationText.getText()) == null)
			return Messages.GitParticipant_3;
		if (fileManager.toURI(fGitLocationText.getText()) == null)
			return "invalid Git path";
		// should we check permissions of: fileManager.getResource(fLocationText.getText()).getParent() ?
		if (!fRemoteValidated) {
			if (!fRemoteHasBeenTested) {
				return "Remote location must be validated";
			} else {
				return specificRemoteError;
			}
		}
		if (!fGitValidated) {
			if (!fGitHasBeenTested) {
				return "Git location must be validated";
			} else {
				return specificGitError;
			}
		}
		return null;
	}
	
	/**
	 * @see ISynchronizeParticipant#getMessage()
	 */
	public String getMessage() {
		if (fGitHasBeenTested) {
			return specificGitWarning;
		}

		return null;
	}

	/**
	 * @see ISynchronizeParticipant#getMessageType()
	 */
	public int getMessageType() {
		// Currently all messages are warning messages.
		return IMessageProvider.WARNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rdt.sync.ui.ISynchronizeParticipant#getProvider(org.eclipse.core.resources.IProject)
	 */
	public ISyncServiceProvider getProvider(IProject project) {
		ServiceModelManager smm = ServiceModelManager.getInstance();
		IService syncService = smm.getService(IRemoteSyncServiceConstants.SERVICE_SYNC);
		GitServiceProvider provider = (GitServiceProvider) smm.getServiceProvider(syncService
				.getProviderDescriptor(GitServiceProvider.ID));
		provider.setLocation(fLocationText.getText());
		provider.setToolLocation(fGitLocationText.getText());
		provider.setRemoteConnection(fSelectedConnection);
		provider.setRemoteServices(fSelectedProvider);
		return provider;
	}

	/**
	 * @return
	 */
	private IRemoteUIConnectionManager getUIConnectionManager() {
		IRemoteUIConnectionManager connectionManager = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(fSelectedProvider)
				.getUIConnectionManager();
		return connectionManager;
	}

	/**
	 * Handle new connection selected
	 */
	private void handleConnectionSelected() {
		int selectionIndex = fConnectionCombo.getSelectionIndex();
		fSelectedConnection = fComboIndexToRemoteConnectionMap.get(selectionIndex);
		updateNewConnectionButtonEnabled(fNewConnectionButton);
		fLocationText.setText(getDefaultPathDisplayString());
		this.setRemoteIsValid(false);
		fRemoteHasBeenTested = false;
		this.setGitIsValid(false);
		fGitHasBeenTested = false;
		this.changeGitLocationUIForConnection();
		update();
	}

	/**
	 * Handle new remote services selected
	 */
	private void handleServicesSelected() {
		int selectionIndex = fProviderCombo.getSelectionIndex();
		fSelectedProvider = fComboIndexToRemoteServicesProviderMap.get(selectionIndex);
		populateConnectionCombo(fConnectionCombo);
		updateNewConnectionButtonEnabled(fNewConnectionButton);
		handleConnectionSelected();
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rdt.sync.ui.ISynchronizeParticipant#isConfigComplete()
	 */
	public boolean isConfigComplete() {
		return getErrorMessage() == null;
	}

	/**
	 * @param connectionCombo
	 */
	private void populateConnectionCombo(final Combo connectionCombo) {
		connectionCombo.removeAll();

		IRemoteConnection[] connections = fSelectedProvider.getConnectionManager().getConnections();

		for (int k = 0; k < connections.length; k++) {
			connectionCombo.add(connections[k].getName(), k);
			fComboIndexToRemoteConnectionMap.put(k, connections[k]);
		}

		connectionCombo.select(0);
		fSelectedConnection = fComboIndexToRemoteConnectionMap.get(0);
	}

	private void update() {
		container.updateMessage();
		// updateButtons() may fail if current page is null. This can happen if update() is called during wizard/page creation. 
		if (container.getCurrentPage() == null) {
			return;
		}
		container.updateButtons();
	}

	/**
	 * @param button
	 */
	private void updateNewConnectionButtonEnabled(Button button) {
		IRemoteUIConnectionManager connectionManager = getUIConnectionManager();
		button.setEnabled(connectionManager != null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rdt.sync.ui.ISynchronizeParticipant#setProjectName(String projectName)
	 */
	public void setProjectName(String projectName) {
		fProjectName = projectName;
		fLocationText.setText(getDefaultPathDisplayString());
		this.setRemoteIsValid(false);
		fRemoteHasBeenTested = false;
	}

	// Decide whether to check or not check "use default location" on a connection change before calling setGitLocation()
	private void changeGitLocationUIForConnection() {
		if (fSelectedConnection != null && fSelectedConnection.isOpen()) {
			fUseGitDefaultLocationButton.setSelection(true);
		} else {
			fUseGitDefaultLocationButton.setSelection(false);
		}
		this.setGitLocation();
	}

	// Fill in Git location text and set the related UI elements.
	private void setGitLocation() {
		// If "use default" not selected - enable and clear textbox only if not yet enabled.
		if (!fUseGitDefaultLocationButton.getSelection()) {
			if (!fGitLocationText.isEnabled()) {
				fGitLocationText.setText(""); //$NON-NLS-1$
				this.setGitIsValid(false);
				fGitHasBeenTested = false;
				fGitLocationText.setEnabled(true);
			}
		// Otherwise, ask remote machine for location.
		// Textbox must be either enabled or disabled depending on if the request is successful.
		} else {
			List<String> args = Arrays.asList("which", "git");
			String errorMessage;
			CommandResults cr = null;
			try {
				cr = this.runRemoteCommand(args, "Finding remote Git location");
				errorMessage = this.buildErrorMessage(cr, "Unable to find Git on remote", null);
			} catch (RemoteExecutionException e) {
				errorMessage = this.buildErrorMessage(null, "Unable to find Git on remote", e);
			}

			// Unable to find Git location
			if (errorMessage != null) {
				fGitLocationText.setText("");
				this.setGitIsValid(false);
				fGitHasBeenTested = false;
				fUseGitDefaultLocationButton.setSelection(false);
				fGitLocationText.setEnabled(true);
				MessageDialog.openError(null, "Remote Execution", errorMessage);
			// Git location found
			} else {
				fGitLocationText.setText(cr.getStdout().trim());
				this.setGitIsValid(validateGit());
				fGitHasBeenTested = true;
				fGitLocationText.setEnabled(false);
			}
		}
		
		// Browse button should be enabled if and only if textbox is enabled.
		fGitLocationBrowseButton.setEnabled(fGitLocationText.isEnabled());
	}
	
	// Check if the remote location is valid (does not actually set it as valid)
	private boolean isRemoteValid() {
		IPath parentPath = new Path(fLocationText.getText());
		if (!parentPath.isAbsolute()) {
			specificRemoteError = "Remote path must be absolute";
			return false;
		}

		// Find the lowest-level file in the path that exist.
		while(!parentPath.isRoot()) {
			List<String> args = Arrays.asList("test", "-e", parentPath.toString());
			String errorMessage = null;
			CommandResults cr = null;
			try {
				cr = this.runRemoteCommand(args, "Verifying remote location");
			} catch (RemoteExecutionException e) {
				errorMessage = this.buildErrorMessage(null, "Unable to verify remote", e);
			}

			if (errorMessage != null) {
				MessageDialog.openError(null, "Remote Execution", errorMessage);
				specificRemoteError = "Unable to verify remote path";
				return false;
			} else if (cr.getExitCode() == 0) {
				break;
			}

			parentPath = parentPath.removeLastSegments(1);
		}

		// Assume parent path is a directory and see if we can write a test file to it.
		// Note that this test fails if parent path is not a directory, so no need to test that case.
		String touchFile = parentPath.append(new Path(TOUCH_TEST_FILE)).toString();
		List<String> args = Arrays.asList("touch", touchFile);
		String errorMessage = null;
		CommandResults cr = null;
		try {
			cr = this.runRemoteCommand(args, "Creating test file");
		} catch (RemoteExecutionException e) {
			errorMessage = this.buildErrorMessage(null, "Unable to verify remote", e);
		}

		if (errorMessage != null) {
			MessageDialog.openError(null, "Remote Execution", errorMessage);
			specificRemoteError = "Unable to verify remote path";
			return false;
		} else if (cr.getExitCode() != 0) {
			specificRemoteError = "Remote path invalid";
			return false;
		}
		
		// Remove the test file
		args = Arrays.asList("rm", "-f", touchFile);
		errorMessage = null;
		cr = null;
		try {
			cr = this.runRemoteCommand(args, "Removing test file");
			errorMessage = this.buildErrorMessage(cr, "Unable to remove test file: " + touchFile, null);
		} catch (RemoteExecutionException e) {
			errorMessage = this.buildErrorMessage(null, "Unable to remove test file: " + touchFile, e);
		}

		if (errorMessage != null) {
			MessageDialog.openError(null, "Remote Execution", errorMessage);
		}
		
		return true;
	}
	
	// Set the remote location as valid
	private void setRemoteIsValid(boolean isValid) {
		fRemoteValidated = isValid;
		if (isValid) {
			fLocationText.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		} else {
			fLocationText.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		}
	}

	// Check if the Git location is valid (does not actually set it as valid).
	// Also sets Git error and warning messages.
	private boolean validateGit() {
		specificGitError = null;
		specificGitWarning = null;
		IPath gitPath = new Path(fGitLocationText.getText());
		if (gitPath.isEmpty()) {
			specificGitError = "Git path is empty";
			return false;
		}
    	if (!gitPath.isAbsolute()) { //$NON-NLS-1$
    		specificGitError = "Git path must be absolute";
    		return false;
    	}
    	List<String> args = Arrays.asList(gitPath.toString(), "--version");
		String errorMessage = null;
		CommandResults cr = null;
		try {
			cr = this.runRemoteCommand(args, "Verifying Git location");
		} catch (RemoteExecutionException e) {
			errorMessage = this.buildErrorMessage(null, "Unable to verify Git", e);
		}

		if (errorMessage != null) {
			MessageDialog.openError(null, "Remote Execution", errorMessage);
			specificGitError = "Unable to verify Git";
			return false;
		} else if (cr.getExitCode() == 126) {
			specificGitError = "Git file invalid - improper permissions";
			return false;
		} else if (cr.getExitCode() == 127) {
			specificGitError = "Git file invalid - not found";
			return false;
		}

		int version = parseGitVersionAsInt(cr.getStdout());
		String versionString = parseGitVersionAsString(cr.getStdout());
		if (cr.getExitCode() != 0 || version == 0) {
			specificGitWarning = "Git file found but version check failed - may not be a Git executable.";
			return true;
		}

		if (version < 10600) {
			specificGitWarning = "Git version " + versionString + " is not supported. Version 1.6.0 is supported and 1.7.0 is recommended.";
		} else if (version < 10700) {
			specificGitWarning = "Git version " + versionString + " is supported but 1.7.0 is recommended for best performance.";
		}

		// Prefer false positives to false negatives. Return true by default.
		return true;
	}
	
	// Set the Git location as valid
	private void setGitIsValid(boolean isValid) {
		fGitValidated = isValid;
		if (isValid) {
			fGitLocationText.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		} else {
			fGitLocationText.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		}
	}

	// Wrapper for running commands - wraps exceptions and invoking of command runner inside container run command.
	private CommandResults remoteCommandResults;
	private CommandResults runRemoteCommand(final List<String> command, final String commandDesc) throws RemoteExecutionException {
		try {
			fContext.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					RecursiveSubMonitor progress = RecursiveSubMonitor.convert(monitor, 100);
					progress.subTask(commandDesc);
					try {
						remoteCommandResults = CommandRunner.executeRemoteCommand(fSelectedConnection, command, null, progress.newChild(100));
					} catch (RemoteSyncException e) {
						throw new InvocationTargetException(e);
					} catch (IOException e) {
						throw new InvocationTargetException(e);
					} catch (InterruptedException e) {
						throw new InvocationTargetException(e);
					} catch (RemoteConnectionException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			throw new RemoteExecutionException(e.getCause());
		} catch (InterruptedException e) {
			throw new RemoteExecutionException(e);
		}
		return remoteCommandResults;
	}

	// Builds error message for command.
	// Either the command result or the exception should be null, but not both.
	// baseMessage cannot be null.
	// Returns error message or null if no error occurred (can only occur if cr is not null).
	private String buildErrorMessage(CommandResults cr, String baseMessage, RemoteExecutionException e) {
		// Command successful
		if (cr != null && cr.getExitCode() == 0) {
			return null;
		}

		// Command runs but unsuccessfully
		if (cr != null) {
			return baseMessage + ": " + cr.getStderr();
		}

		// Command did not run - exception thrown
		String errorMessage = baseMessage;
		if (e.getMessage() != null) {
			errorMessage += ": " + e.getMessage();
		}

		return errorMessage;
	}

	/**
	 * Parse raw output of "git --version" and return an integer representation of the version, suitable for comparisons.
	 * @param versionCommandOutput
	 * @return version integer or 0 on failure to parse
	 */
	public static int parseGitVersionAsInt(String versionCommandOutput) {
		Matcher m = Pattern.compile("git version ([0-9]+)\\.([0-9]+)\\.([0-9]+).*").matcher(versionCommandOutput.trim()); //$NON-NLS-1$
		if (m.matches()) {
			return Integer.parseInt(m.group(1)) * 10000 +
					Integer.parseInt(m.group(2)) * 100 + Integer.parseInt(m.group(3));
		} else {
			return 0;
		}
	}

	/**
	 * Parse raw output of "git --version" and return the version string, suitable for displaying to users.
	 * @param versionCommandOutput
	 * @return version string or null on failure to parse
	 */
	public static String parseGitVersionAsString(String versionCommandOutput) {
		Matcher m = Pattern.compile("git version ([0-9]+\\.[0-9]+\\.[0-9]+).*").matcher(versionCommandOutput.trim()); //$NON-NLS-1$
		if (m.matches()) {
			return m.group(1);
		} else {
			return null;
		}
	}
}
