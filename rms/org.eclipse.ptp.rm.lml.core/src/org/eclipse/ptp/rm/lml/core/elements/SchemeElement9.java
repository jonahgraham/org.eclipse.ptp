//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.13 at 09:16:36 AM CET 
//

package org.eclipse.ptp.rm.lml.core.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for scheme_element9 complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="scheme_element9">
 *   &lt;complexContent>
 *     &lt;extension base="{http://eclipse.org/ptp/lml}scheme_element">
 *       &lt;sequence>
 *         &lt;element name="el10" type="{http://eclipse.org/ptp/lml}scheme_element10" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheme_element9", propOrder = {
		"el10"
})
public class SchemeElement9
		extends SchemeElement
{

	protected List<SchemeElement10> el10;

	/**
	 * Gets the value of the el10 property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the el10
	 * property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getEl10().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link SchemeElement10 }
	 * 
	 * 
	 */
	public List<SchemeElement10> getEl10() {
		if (el10 == null) {
			el10 = new ArrayList<SchemeElement10>();
		}
		return this.el10;
	}

}
