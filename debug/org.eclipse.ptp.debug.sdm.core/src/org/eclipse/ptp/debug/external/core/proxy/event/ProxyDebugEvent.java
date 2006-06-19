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

package org.eclipse.ptp.debug.external.core.proxy.event;

import java.math.BigInteger;

import org.eclipse.ptp.core.proxy.event.IProxyEvent;
import org.eclipse.ptp.core.proxy.event.ProxyEvent;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.ExtFormat;
import org.eclipse.ptp.debug.core.aif.AIF;
import org.eclipse.ptp.debug.core.aif.IAIF;
import org.eclipse.ptp.debug.core.cdi.IPCDICondition;
import org.eclipse.ptp.debug.core.cdi.IPCDILineLocation;
import org.eclipse.ptp.debug.core.cdi.model.IPCDIBreakpoint;
import org.eclipse.ptp.debug.core.cdi.model.IPCDILocator;
import org.eclipse.ptp.debug.external.core.cdi.Condition;
import org.eclipse.ptp.debug.external.core.cdi.Locator;
import org.eclipse.ptp.debug.external.core.cdi.breakpoints.LineBreakpoint;
import org.eclipse.ptp.debug.external.core.cdi.model.DataReadMemoryInfo;
import org.eclipse.ptp.debug.external.core.cdi.model.LineLocation;
import org.eclipse.ptp.debug.external.core.cdi.model.Memory;
import org.eclipse.ptp.debug.external.core.proxy.ProxyDebugSignal;
import org.eclipse.ptp.debug.external.core.proxy.ProxyDebugStackframe;

public class ProxyDebugEvent extends ProxyEvent {
	
	public static IProxyEvent toEvent(String str) {
		IProxyDebugEvent evt = null;
		String[] args = str.split(" ");
		
		int type = Integer.parseInt(args[0]);
		
		BitList set = ProxyEvent.decodeBitSet(args[1]);
		
		switch (type) {
		case IProxyDebugEvent.EVENT_DBG_OK:
			evt = new ProxyDebugOKEvent(set);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_ERROR:
			int errCode = Integer.parseInt(args[2]);
			evt = new ProxyDebugErrorEvent(set, errCode, decodeString(args[3]));
			break;

		case IProxyDebugEvent.EVENT_DBG_BPHIT:
			int hitId = Integer.parseInt(args[2]);
			int bpTid = Integer.parseInt(args[3]);
			evt = new ProxyDebugBreakpointHitEvent(set, hitId, bpTid);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_BPSET:
			int setId = Integer.parseInt(args[2]);
			IPCDILineLocation loc = toLineLocation(args[8], args[11]);
			IPCDIBreakpoint bpt = toBreakpoint(args[4], args[5], args[6], args[7], loc);
			evt = new ProxyDebugBreakpointSetEvent(set, setId, bpt);
			break;

		case IProxyDebugEvent.EVENT_DBG_SIGNAL:
			int sigTid = Integer.parseInt(args[4]);
			IPCDILocator sigLoc = null;
			
			if (!(args[5].compareTo("*") == 0)) {
				sigLoc = toLocator(args[6], args[7], args[8], args[9]);
			}

			evt = new ProxyDebugSignalEvent(set, decodeString(args[2]), decodeString(args[3]), sigTid, sigLoc);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_SIGNALS:
			int numSignals = Integer.parseInt(args[2]);
			ProxyDebugSignal[] signals = new ProxyDebugSignal[numSignals];
			for (int i = 0; i<numSignals; i++) {
				signals[i] = new ProxyDebugSignal(decodeString(args[5*i+3]), toboolean(Integer.parseInt(args[5*i+4])), toboolean(Integer.parseInt(args[5*i+5])), toboolean(Integer.parseInt(args[5*i+6])), decodeString(args[5*i+7]));
			}
			evt = new ProxyDebugSignalsEvent(set, signals);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_EXIT_SIGNAL:
			evt = new ProxyDebugSignalExitEvent(set, decodeString(args[2]), decodeString(args[3]));
			break;
			
		case IProxyDebugEvent.EVENT_DBG_EXIT:
			int status = Integer.parseInt(args[2]);
			evt = new ProxyDebugExitEvent(set, status);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_SUSPEND:
			IPCDILocator suspendLoc = toLocator(args[3], args[4], args[5], args[6]);
			int susTid = Integer.parseInt(args[7]);
			evt = new ProxyDebugSuspendEvent(set, suspendLoc, susTid);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_STEP:
			ProxyDebugStackframe frame = toFrame(args[2], args[3], args[4], args[6], args[5]);
			int stTid = Integer.parseInt(args[7]);
			evt = new ProxyDebugStepEvent(set, frame, stTid);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_FRAMES:
			int numFrames = Integer.parseInt(args[2]);
			ProxyDebugStackframe[] frames = new ProxyDebugStackframe[numFrames];
			for (int i = 0; i < numFrames; i++) {
				frames[i] = toFrame(args[5*i+3], args[5*i+4], args[5*i+5], args[5*i+7], args[5*i+6]);
			}
			evt = new ProxyDebugStackframeEvent(set, frames);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_DATA:
			IAIF data = new AIF(decodeString(args[2]), decodeBytes(args[3]), decodeString(args[4]));
			evt = new ProxyDebugDataEvent(set, data);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_TYPE:
			evt = new ProxyDebugTypeEvent(set, decodeString(args[2]));
			break;
			
		case IProxyDebugEvent.EVENT_DBG_VARS:
			int numVars = Integer.parseInt(args[2]);
			String[] vars = new String[numVars];
			for (int i = 0; i < numVars; i++) {
				vars[i] = decodeString(args[i+3]);
			}
			evt = new ProxyDebugVarsEvent(set, vars);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_ARGS:
			int numArgs = Integer.parseInt(args[2]);
			String[] arg_strs = new String[numArgs];
			for (int i = 0; i < numArgs; i++) {
				arg_strs[i] = decodeString(args[i+3]);
			}
			evt = new ProxyDebugArgsEvent(set, arg_strs);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_INIT:
			int num_servers = Integer.parseInt(args[2]);
			evt = new ProxyDebugInitEvent(set, num_servers);
			break;
			
		case IProxyDebugEvent.EVENT_DBG_THREADS:
			int numThreads = Integer.parseInt(args[3]);
			String[] thread_ids = new String[numThreads + 1];
			thread_ids[0] = args[2];
			for (int i=1; i<thread_ids.length; i++) {
				thread_ids[i] = decodeString(args[i+3]);
			}
			evt = new ProxyDebugInfoThreadsEvent(set, thread_ids);
			break;

		case IProxyDebugEvent.EVENT_DBG_THREAD_SELECT:
			int current_thread_id = Integer.parseInt(args[2]);
			ProxyDebugStackframe th_frame = toFrame(args[3], args[4], args[5], args[7], args[6]);
			evt = new ProxyDebugSetThreadSelectEvent(set, current_thread_id, th_frame);
			break;

		case IProxyDebugEvent.EVENT_DBG_STACK_INFO_DEPTH:
			int depth = Integer.parseInt(args[2]);
			evt = new ProxyDebugStackInfoDepthEvent(set, depth);
			break;

		case IProxyDebugEvent.EVENT_DBG_DATA_READ_MEMORY:
			int numMemories = Integer.parseInt(args[9]);
			Memory[] memories = new Memory[numMemories];
			int data_len = 0;
			for (int i=0; i<numMemories; i++) {
				int new_data_len = Integer.parseInt(args[data_len*i+12]);
				String addr = decodeString(args[data_len*i+10]);
				String ascii = decodeString(args[data_len*i+11]);
				String[] data_str = new String[new_data_len];
				for (int j=0; j<new_data_len; j++) {
					data_str[j] = decodeString(args[data_len*i+13+j]);
				}
				data_len = new_data_len;
				memories[i] = new Memory(addr, ascii, data_str);
			}
			evt = new ProxyDebugMemoryInfoEvent(set, toMemoryInfo(args[2], args[3], args[4], args[5], args[6], args[7], args[8], memories));
			break;
		}
		return evt;
	}
	
	public static BigInteger decodeAddr(String str) {
		String[] parts = str.split(":");
		int len = Integer.parseInt(parts[0], 16) - 1; // Skip trailing NULL
		byte[] strBytes = new byte[len];
		
		for (int i = 0, p = 0; i < len; i++, p += 2) {
			byte c = (byte) ((Character.digit(parts[1].charAt(p), 16) & 0xf) << 4);
			c |= (byte) ((Character.digit(parts[1].charAt(p+1), 16) & 0xf));
			strBytes[i] = c;
		}
		
		BigInteger a = new BigInteger(strBytes);
		return a;
	}
	
	public static IPCDIBreakpoint toBreakpoint(String ignoreStr, String spec, String del, String typeStr, IPCDILineLocation loc) {
		IPCDIBreakpoint bpt = null;
		int typeVal;
		
		int ignore = Integer.parseInt(ignoreStr);
		IPCDICondition cond = new Condition(ignore, null, null);
		
		String type = decodeString(typeStr);

		if (type.compareTo("breakpoint") == 0)
			typeVal = IPCDIBreakpoint.REGULAR;
		else if (type.compareTo("hw") == 0)
			typeVal = IPCDIBreakpoint.HARDWARE;
		else
			typeVal = IPCDIBreakpoint.TEMPORARY;
		
		bpt = new LineBreakpoint(typeVal, loc, cond);
	
		return bpt;
	}
	
	public static IPCDILineLocation toLineLocation(String fileStr, String lineStr) {
		String file = decodeString(fileStr);
		int line = Integer.parseInt(lineStr);
		return new LineLocation(file, line);
	}
	
	public static IPCDILocator toLocator(String fileStr, String funcStr, String addrStr, String lineStr) {
		String file = decodeString(fileStr);
		String func = decodeString(funcStr);
		int line = Integer.parseInt(lineStr);
		String addr = decodeString(addrStr);
		
		return new Locator(file, func, line, ExtFormat.getBigInteger(addr));
	}

	public static ProxyDebugStackframe toFrame(String level, String file, String func, String line, String addr)  {
		int stepLevel = Integer.parseInt(level);
		int stepLine = Integer.parseInt(line);
		return new ProxyDebugStackframe(stepLevel, decodeString(file), decodeString(func), stepLine, decodeString(addr));
	}
	public static DataReadMemoryInfo toMemoryInfo(String addr, String nextRow, String prevRow, String nextPage, String prevPage, String numBytes, String totalBytes, Memory[] memories) {
		return new DataReadMemoryInfo(decodeString(addr), Long.parseLong(nextRow), Long.parseLong(prevRow), Long.parseLong(nextPage), Long.parseLong(prevPage), Long.parseLong(numBytes), Long.parseLong(totalBytes), memories);
	}
	public static boolean toboolean(int value) {
		return (value!=0);
	}
}
