/**
 * Copyright (c) 2011 Forschungszentrum Juelich GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Claudia Knobloch, FZ Juelich
 */
package org.eclipse.ptp.rm.lml.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ptp.rm.lml.core.events.IJobListSortedEvent;
import org.eclipse.ptp.rm.lml.core.events.ILguiAddedEvent;
import org.eclipse.ptp.rm.lml.core.events.ILguiSelectedEvent;
import org.eclipse.ptp.rm.lml.core.events.IMarkObjectEvent;
import org.eclipse.ptp.rm.lml.core.events.ISelectedObjectChangeEvent;
import org.eclipse.ptp.rm.lml.core.events.ITableColumnChangeEvent;
import org.eclipse.ptp.rm.lml.core.events.IUnmarkObjectEvent;
import org.eclipse.ptp.rm.lml.core.events.IUnselectedObjectEvent;
import org.eclipse.ptp.rm.lml.core.events.IViewAddedEvent;
import org.eclipse.ptp.rm.lml.core.events.IViewDisposedEvent;
import org.eclipse.ptp.rm.lml.core.listeners.ILMLListener;
import org.eclipse.ptp.rm.lml.core.listeners.IListener;
import org.eclipse.ptp.rm.lml.core.listeners.IViewListener;
import org.eclipse.ptp.rm.lml.core.model.ILguiItem;
import org.eclipse.ptp.rm.lml.internal.core.events.JobListSortedEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.LguiAddedEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.LguiSelectedEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.MarkObjectEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.SelectedObjectChangeEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.TableColumnChangeEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.UnmarkObjectEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.UnselectObjectEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.ViewAddedEvent;
import org.eclipse.ptp.rm.lml.internal.core.events.ViewDisposedEvent;
import org.eclipse.ptp.rm.lml.internal.core.model.LguiItem;

/**
 * Class of the interface ILMLManager
 */
public class LMLManager {

	/*
	 * Map of all ILguiItems
	 */
	protected final Map<String, ILguiItem> LGUIS = new HashMap<String, ILguiItem>();

	/*
	 * The current considered ILguiItem
	 */
	private ILguiItem fSelectedLguiItem = null;

	/*
	 * A list of all listeners on the ILguiItem
	 */
	private final ListenerList lmlListeners = new ListenerList();

	/*
	 * A list of all listeners on the views
	 */
	private final ListenerList viewListeners = new ListenerList();

	/*
	 * A list of all listeners.
	 */
	private final Map<String, IListener> listeners = new HashMap<String, IListener>();
	
	/*
	 * An instance of this class.
	 */
	private static LMLManager manager;
	
	/**************************************************************************************************************
	 * Constructors
	 **************************************************************************************************************/

	private LMLManager() {
		manager = this;
	}
	
	/**************************************************************************************************************
	 * Getting methods
	 **************************************************************************************************************/

	public static LMLManager getInstance() {
		if (manager == null) {
			manager = new LMLManager();
		}
		return manager;
	}
	
	/**************************************************************************************************************
	 * Job related methods
	 **************************************************************************************************************/
	
	
	
	/**************************************************************************************************************
	 * Communication methods
	 **************************************************************************************************************/

	public void register(String name, InputStream stream) {
		//that is 
		if (!LGUIS.containsKey(name)) {
			fSelectedLguiItem = new LguiItem(stream);
			synchronized (LGUIS) {
				LGUIS.put(name, fSelectedLguiItem);
			}
			fireNewLgui();
		}
		open(name);
		update(stream);
	}
	
	public void register(String name, InputStream inStream, OutputStream outStream) {
		
	}
	
	public void open(String name) {
		// TODO load all data
		if (LGUIS.containsKey(name)) {
			fSelectedLguiItem = LGUIS.get(name);
			fireSelectedLgui();
		}
	}
	
	public void close(String name) {
		// TODO save data
		fSelectedLguiItem = null;
	}
	
	/**************************************************************************************************************
	 * Lgui handling methods
	 **************************************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.ILMLManager#addLgui(URL xmlFile)
	 */
	public boolean addLgui(URI xmlFile) {
		if (!LGUIS.containsKey(xmlFile.getPath())) {
			fSelectedLguiItem = new LguiItem(xmlFile);
			synchronized (LGUIS) {
				LGUIS.put(xmlFile.getPath(), fSelectedLguiItem);
			}
			fireNewLgui();
			return false;
		} else {
			return true;
		}
	}

	public String[] getLguis() {
		Set<String> lguis = LGUIS.keySet();
		return lguis.toArray(new String[lguis.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.ILMLManager#sortLgui
	 */
	public void sortLgui() {
		fireSortedLgui();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.lml.core.ILMLManager#getSelectedLguiItem()
	 */
	public ILguiItem getSelectedLguiItem() {
		return fSelectedLguiItem;
	}
	
	private String getSelectedLguiTitle(int index) {
		String[] lguis = LGUIS.keySet().toArray(new String[LGUIS.size()]);
		return lguis[index];
	}
	
	public void selectLgui(int index) {
		fSelectedLguiItem = LGUIS.get(getSelectedLguiTitle(index));
		fireSelectedLgui();
	}

	public void selectLgui(URI xmlFile) {
		fSelectedLguiItem = LGUIS.get(xmlFile.getPath());
		fireSelectedLgui();
	}

	public int getSelectedLguiIndex(String title) {
		String[] lguis = LGUIS.keySet().toArray(new String[LGUIS.size()]);
		int index = 0;
		for (int i = 0; i < lguis.length; i++) {
			if (title.equals(lguis[i])) {
				index = i;
			}
		}
		return index;
	}

	public void removeLgui(String title) {
		LGUIS.remove(title);
		if (LGUIS.isEmpty()) {
			fSelectedLguiItem = null;
		} else {
			fSelectedLguiItem = LGUIS.get(getLguis()[0]);
		}
		fireSelectedLgui();
	}
	
	public void update(InputStream stream) {
		fSelectedLguiItem.update(stream);
		fireNewLgui();
	}

	public void update() {
		fSelectedLguiItem.updateXML();
		fireNewLgui();
	}

	/**************************************************************************************************************
	 * Listener methods
	 **************************************************************************************************************/

	public void addListener(IViewListener listener) {
		viewListeners.add(listener);
		listeners.put("ViewManager", listener);
	}

	public void addListener(IViewListener listener, String view) {
		viewListeners.add(listener);
		listeners.put(view, listener);
	}

	public void addListener(ILMLListener listener, String view) {
		lmlListeners.add(listener);
		// listeners.put(view, (IListener) listener);
	}
	
	public IListener getListener(String view) {
		return listeners.get(view);
	}
	
	public void removeListener(ILMLListener listener) {
		lmlListeners.remove(listener);
	}

	public void removeListener(IViewListener listener) {
		viewListeners.remove(listener);
		listeners.remove("ViewManager");
	}
	

	/**************************************************************************************************************
	 * View methods
	 **************************************************************************************************************/

	
	public void setTableColumnActive(String gid, String title) {
		fSelectedLguiItem.getTableHandler().setTableColumnActive(gid, title, true);
		fireChangeTableColumn();
	}
	
	public void setTableColumnNonActive(String gid, String title) {
		fSelectedLguiItem.getTableHandler().setTableColumnActive(gid, title, false);
		fireChangeTableColumn();
	}

	public void selectObject(String oid) {
		fireChangeSelectedObject(oid);
	}
	
	public void markObject(String oid) {
		fireMarkObject(oid);
	}

	public void unmarkObject(String oid) {
		fireUnmarkObject(oid);
	}
	
	public void addComponent(String gid) {
		String type = fSelectedLguiItem.getLayoutAccess().setComponentActive(gid, true);
		fireAddView(gid, type);
	}

	public void removeComponent(String gid) {
		fSelectedLguiItem.getLayoutAccess().setComponentActive(gid, false);
		fireremoveView(gid);
	}	
	
	public void unselectObject(String oid) {
		fireUnselectObject(oid);
	}

	
	/**************************************************************************************************************
	 * Fire events method
	 **************************************************************************************************************/
	
	/**
	 * Method is called when a new ILguiItem was generated.
	 */
	private void fireNewLgui() {
		ILguiAddedEvent event = new LguiAddedEvent(this, fSelectedLguiItem);
		for (Object listener : viewListeners.getListeners()) {
			((IViewListener) listener).handleEvent(event);
		}
	}

	private void fireremoveView(String gid) {
		IViewDisposedEvent event = new ViewDisposedEvent();
		for (Object listener : viewListeners.getListeners()) {
			((IViewListener) listener).handleEvent(event);
		}
	}

	private void fireSelectedLgui() {
		ILguiSelectedEvent event = new LguiSelectedEvent(this, fSelectedLguiItem);
		for (Object listener : viewListeners.getListeners()) {
			((IViewListener) listener).handleEvent(event);
		}
	}


	private void fireAddView(String gid, String type) {
		IViewAddedEvent event = new ViewAddedEvent(gid, type);
		for (Object listener : viewListeners.getListeners()) {
			((IViewListener) listener).handleEvent(event);
		}
	}
	
	
	private void fireMarkObject(String oid) {
		IMarkObjectEvent event = new MarkObjectEvent(oid);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}
	}

	private void fireUnmarkObject(String oid) {
		IUnmarkObjectEvent event = new UnmarkObjectEvent(oid);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}
	}
	
	private void fireChangeTableColumn() {
		ITableColumnChangeEvent event = new TableColumnChangeEvent(this, fSelectedLguiItem);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}

	}

	private void fireUnselectObject(String oid) {
		IUnselectedObjectEvent event = new UnselectObjectEvent(oid);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}
	}

	private void fireChangeSelectedObject(String oid) {
		ISelectedObjectChangeEvent event = new SelectedObjectChangeEvent(oid);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}
	}

	/**
	 * Method is called when an ILguiItem was sorted.
	 */
	private void fireSortedLgui() {
		IJobListSortedEvent event = new JobListSortedEvent(this, fSelectedLguiItem);
		for (Object listener : lmlListeners.getListeners()) {
			((ILMLListener) listener).handleEvent(event);
		}
	}
}