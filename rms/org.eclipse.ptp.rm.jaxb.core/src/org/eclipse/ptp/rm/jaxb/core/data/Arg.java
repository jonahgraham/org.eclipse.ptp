//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.30 at 11:18:33 AM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * 
 * The text of the argument will be parsed for references to the resource
 * manager environment.
 * 
 * 
 * <p>
 * Java class for arg complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="arg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="isUndefinedIfMatches" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arg", propOrder = { "content" })
public class Arg {

	@XmlValue
	protected String content;
	@XmlAttribute
	protected String isUndefinedIfMatches;

	/**
	 * 
	 * The text of the argument will be parsed for references to the resource
	 * manager environment.
	 * 
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Gets the value of the isUndefinedIfMatches property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIsUndefinedIfMatches() {
		return isUndefinedIfMatches;
	}

	/**
	 * 
	 * The text of the argument will be parsed for references to the resource
	 * manager environment.
	 * 
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setContent(String value) {
		this.content = value;
	}

	/**
	 * Sets the value of the isUndefinedIfMatches property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setIsUndefinedIfMatches(String value) {
		this.isUndefinedIfMatches = value;
	}

}
