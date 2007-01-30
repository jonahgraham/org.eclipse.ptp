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
/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.debug.internal.core.model;

import java.text.MessageFormat;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.ptp.debug.core.IPDebugConstants;
import org.eclipse.ptp.debug.core.PCDIDebugModel;
import org.eclipse.ptp.debug.core.PDebugUtils;
import org.eclipse.ptp.debug.core.cdi.IPCDISession;
import org.eclipse.ptp.debug.core.cdi.PCDIException;
import org.eclipse.ptp.debug.core.cdi.model.IPCDITarget;
import org.eclipse.ptp.debug.core.model.IPDebugElement;
import org.eclipse.ptp.debug.core.model.IPDebugElementStatus;
import org.eclipse.ptp.debug.core.model.IPDebugTarget;
import org.eclipse.ptp.debug.core.model.PDebugElementState;

public abstract class PDebugElement extends PlatformObject implements IPDebugElement, IPDebugElementStatus {
	private PDebugTarget fDebugTarget;
	private int fSeverity = IPDebugElementStatus.OK;
	private String fMessage = null;
	private PDebugElementState fState = PDebugElementState.UNDEFINED;
	private PDebugElementState fOldState = PDebugElementState.UNDEFINED;
	private Object fCurrentStateInfo = null;

	public PDebugElement(PDebugTarget target) {
		setDebugTarget(target);
	}
	public String getModelIdentifier() {
		return PCDIDebugModel.getPluginIdentifier();
	}
	public IDebugTarget getDebugTarget() {
		return fDebugTarget;
	}
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}
	protected void setDebugTarget(PDebugTarget target) {
		fDebugTarget = target;
	}
	protected void logError(Exception e) {
		DebugPlugin.log(e);
	}
	protected void logError(String message) {
		DebugPlugin.logMessage(message, null);
	}
	protected void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}
	protected void fireEventSet(DebugEvent[] events) {
		DebugPlugin.getDefault().fireDebugEventSet(events);
	}
	public void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}
	public DebugEvent createCreateEvent() {
		return new DebugEvent(this, DebugEvent.CREATE);
	}
	public void fireResumeEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}
	public DebugEvent createResumeEvent(int detail) {
		return new DebugEvent(this, DebugEvent.RESUME, detail);
	}
	public void fireSuspendEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}
	public DebugEvent createSuspendEvent(int detail) {
		return new DebugEvent(this, DebugEvent.SUSPEND, detail);
	}
	public void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
	public DebugEvent createTerminateEvent() {
		return new DebugEvent(this, DebugEvent.TERMINATE);
	}
	public void fireChangeEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE, detail));
	}
	public DebugEvent createChangeEvent(int detail) {
		return new DebugEvent(this, DebugEvent.CHANGE, detail);
	}
	public IPCDISession getCDISession() {
		return (IPCDISession)getCDITarget().getSession();
	}
	public IPCDITarget getCDITarget() {
		return (IPCDITarget) getDebugTarget().getAdapter(IPCDITarget.class);
	}
	public static void requestFailed(String message, Exception e) throws DebugException {
		requestFailed(message, e, DebugException.REQUEST_FAILED);
	}
	public static void targetRequestFailed(String message, PCDIException e) throws DebugException {
		requestFailed(MessageFormat.format("Target request failed: {0}.", new String[] { message }), e, DebugException.TARGET_REQUEST_FAILED); //$NON-NLS-1$
	}
	public static void requestFailed(String message, Throwable e, int code) throws DebugException {
		throwDebugException(message, code, e);
	}
	public static void targetRequestFailed(String message, Throwable e) throws DebugException {
		throwDebugException(MessageFormat.format("Target request failed: {0}.", new String[] { message }), DebugException.TARGET_REQUEST_FAILED, e); //$NON-NLS-1$
	}
	public static void notSupported(String message) throws DebugException {
		throwDebugException(message, DebugException.NOT_SUPPORTED, null);
	}
	protected static void throwDebugException(String message, int code, Throwable exception) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, PCDIDebugModel.getPluginIdentifier(), code, message, exception));
	}
	protected void infoMessage(Throwable e) {
		IStatus newStatus = new Status(IStatus.INFO, PCDIDebugModel.getPluginIdentifier(), IPDebugConstants.STATUS_CODE_INFO, e.getMessage(), null);
		PDebugUtils.info(newStatus, getDebugTarget());
	}
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IDebugElement.class))
			return this;
		if (adapter.equals(IPDebugElement.class))
			return this;
		if (adapter.equals(PDebugElement.class))
			return this;
		if (adapter.equals(IPDebugElementStatus.class))
			return this;
		if (adapter.equals(IPCDISession.class))
			return getCDISession();
		if (adapter.equals(IPDebugTarget.class))
			return getDebugTarget();
		if (adapter.equals(IDebugTarget.class))
			return getDebugTarget();
		if (adapter.equals(IMemoryBlockRetrieval.class))
			return getDebugTarget().getAdapter(adapter);
		if (adapter.equals(ILaunch.class))
			return getDebugTarget().getLaunch();

		return super.getAdapter(adapter);
	}
	protected void setStatus(int severity, String message) {
		fSeverity = severity;
		fMessage = message;
		if (fMessage != null)
			fMessage.trim();
	}
	protected void resetStatus() {
		fSeverity = IPDebugElementStatus.OK;
		fMessage = null;
	}
	public boolean isOK() {
		return (fSeverity == IPDebugElementStatus.OK);
	}
	public int getSeverity() {
		return fSeverity;
	}
	public String getMessage() {
		return fMessage;
	}
	public PDebugElementState getState() {
		return fState;
	}
	protected synchronized void setState(PDebugElementState state) throws IllegalArgumentException {
		fOldState = fState;
		fState = state;
	}
	protected synchronized void restoreState() {
		fState = fOldState;
	}
	public Object getCurrentStateInfo() {
		return fCurrentStateInfo;
	}
	protected void setCurrentStateInfo(Object currentStateInfo) {
		fCurrentStateInfo = currentStateInfo;
	}
}
