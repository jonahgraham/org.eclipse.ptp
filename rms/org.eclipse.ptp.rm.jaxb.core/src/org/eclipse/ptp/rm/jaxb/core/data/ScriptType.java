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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for script-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="script-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="file-staging-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="line" type="{http://eclipse.org/ptp/rm}line-type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="insertEnvironmentAfter" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
 *       &lt;attribute name="deleteAfterSubmit" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "script-type", propOrder = { "fileStagingLocation", "line" })
public class ScriptType {

	@XmlElement(name = "file-staging-location")
	protected String fileStagingLocation;
	@XmlElement(required = true)
	protected List<LineType> line;
	@XmlAttribute(name = "insertEnvironmentAfter")
	protected Integer insertEnvironmentAfter;
	@XmlAttribute(name = "deleteAfterSubmit")
	protected Boolean deleteAfterSubmit;

	/**
	 * Gets the value of the fileStagingLocation property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getFileStagingLocation() {
		return fileStagingLocation;
	}

	/**
	 * Sets the value of the fileStagingLocation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFileStagingLocation(String value) {
		this.fileStagingLocation = value;
	}

	/**
	 * Gets the value of the line property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the line
	 * property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLine().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link LineType }
	 * 
	 * 
	 */
	public List<LineType> getLine() {
		if (line == null) {
			line = new ArrayList<LineType>();
		}
		return this.line;
	}

	/**
	 * Gets the value of the insertEnvironmentAfter property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public int getInsertEnvironmentAfter() {
		if (insertEnvironmentAfter == null) {
			return -1;
		} else {
			return insertEnvironmentAfter;
		}
	}

	/**
	 * Sets the value of the insertEnvironmentAfter property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setInsertEnvironmentAfter(Integer value) {
		this.insertEnvironmentAfter = value;
	}

	/**
	 * Gets the value of the deleteAfterSubmit property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public boolean isDeleteAfterSubmit() {
		if (deleteAfterSubmit == null) {
			return true;
		} else {
			return deleteAfterSubmit;
		}
	}

	/**
	 * Sets the value of the deleteAfterSubmit property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setDeleteAfterSubmit(Boolean value) {
		this.deleteAfterSubmit = value;
	}

}
