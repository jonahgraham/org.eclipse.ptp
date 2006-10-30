/*******************************************************************************
 * Copyright (c) 2006 The Regents of the University of California. 
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
package org.eclipse.ptp.core.attributes;

public final class StringAttribute extends AbstractAttribute {

	private final String value;

	public StringAttribute(IAttributeDescription description, String value) {
		super(description);
		this.value = value;
	}

	public IAttribute create(String string) {
		return new StringAttribute(getDescription(), string);
	}

	public boolean equals(Object obj) {
		if (obj instanceof StringAttribute) {
			StringAttribute attr = (StringAttribute) obj;
			return value.equals(attr.value);
		}
		return false;
	}

	public String getStringRep() {
		return value;
	}
	
	public String getValue() {
		return value;
	}

	public int hashCode() {
		return value.hashCode();
	}

	protected int doCompareTo(AbstractAttribute arg0) {
		StringAttribute attr = (StringAttribute) arg0;
		return this.value.compareTo(attr.value);
	}

}
