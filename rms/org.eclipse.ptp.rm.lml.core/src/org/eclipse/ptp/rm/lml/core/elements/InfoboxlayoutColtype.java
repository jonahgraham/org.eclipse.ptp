//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.13 at 09:16:36 AM CET 
//

package org.eclipse.ptp.rm.lml.core.elements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for infoboxlayout_coltype complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="infoboxlayout_coltype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="pos" type="{http://eclipse.org/ptp/lml}integer01type" />
 *       &lt;attribute name="width" default="0.5">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="sorted" type="{http://eclipse.org/ptp/lml}columnsortedtype" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoboxlayout_coltype")
public class InfoboxlayoutColtype {

	@XmlAttribute
	protected Integer pos;
	@XmlAttribute
	protected Double width;
	@XmlAttribute
	protected Columnsortedtype sorted;

	/**
	 * Gets the value of the pos property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getPos() {
		return pos;
	}

	/**
	 * Sets the value of the pos property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setPos(Integer value) {
		this.pos = value;
	}

	/**
	 * Gets the value of the width property.
	 * 
	 * @return
	 *         possible object is {@link Double }
	 * 
	 */
	public double getWidth() {
		if (width == null) {
			return 0.5D;
		} else {
			return width;
		}
	}

	/**
	 * Sets the value of the width property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setWidth(Double value) {
		this.width = value;
	}

	/**
	 * Gets the value of the sorted property.
	 * 
	 * @return
	 *         possible object is {@link Columnsortedtype }
	 * 
	 */
	public Columnsortedtype getSorted() {
		if (sorted == null) {
			return Columnsortedtype.FALSE;
		} else {
			return sorted;
		}
	}

	/**
	 * Sets the value of the sorted property.
	 * 
	 * @param value
	 *            allowed object is {@link Columnsortedtype }
	 * 
	 */
	public void setSorted(Columnsortedtype value) {
		this.sorted = value;
	}

}
