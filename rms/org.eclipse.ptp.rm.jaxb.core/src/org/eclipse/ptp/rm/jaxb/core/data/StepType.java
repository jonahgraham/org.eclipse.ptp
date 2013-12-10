//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.05 at 08:09:06 AM EST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for step-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="step-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="cmd" type="{http://eclipse.org/ptp/rm}simple-command-type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="active" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exec_after" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "step-type", propOrder = { "cmd" })
public class StepType {

	protected List<SimpleCommandType> cmd;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "active")
	protected String active;
	@XmlAttribute(name = "exec_after")
	protected String execAfter;
	@XmlAttribute(name = "type")
	protected String type;

	/**
	 * Gets the value of the cmd property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the cmd
	 * property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCmd().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link SimpleCommandType }
	 * 
	 * 
	 */
	public List<SimpleCommandType> getCmd() {
		if (cmd == null) {
			cmd = new ArrayList<SimpleCommandType>();
		}
		return this.cmd;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the active property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getActive() {
		return active;
	}

	/**
	 * Sets the value of the active property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setActive(String value) {
		this.active = value;
	}

	/**
	 * Gets the value of the execAfter property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getExecAfter() {
		return execAfter;
	}

	/**
	 * Sets the value of the execAfter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExecAfter(String value) {
		this.execAfter = value;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

}
