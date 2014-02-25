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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * One point in a diagram. Could define one bar in a bar
 * chart.
 * 
 * 
 * <p>
 * Java class for datapoint_type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="datapoint_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="oid" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datapoint_type")
@XmlSeeAlso({
		DatarectType.class
})
public class DatapointType {

	@XmlAttribute(required = true)
	protected double x;
	@XmlAttribute(required = true)
	protected double y;
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NCName")
	protected String oid;

	/**
	 * Gets the value of the x property.
	 * 
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the value of the x property.
	 * 
	 */
	public void setX(double value) {
		this.x = value;
	}

	/**
	 * Gets the value of the y property.
	 * 
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the value of the y property.
	 * 
	 */
	public void setY(double value) {
		this.y = value;
	}

	/**
	 * Gets the value of the oid property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Sets the value of the oid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOid(String value) {
		this.oid = value;
	}

}
