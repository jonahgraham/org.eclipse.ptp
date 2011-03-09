//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.09 at 02:55:11 PM CST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for job-attribute complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="job-attribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tooltip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="choice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validator" type="{http://org.eclipse.ptp/rm}validator" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="basic" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="readOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "job-attribute", propOrder = { "description", "tooltip", "choice", "_default", "validator", "value" })
public class JobAttribute {

	@XmlElement(required = true)
	protected String description;
	protected String tooltip;
	protected String choice;
	@XmlElement(name = "default")
	protected String _default;
	protected Validator validator;
	protected Object value;
	@XmlAttribute
	protected Boolean basic;
	@XmlAttribute(required = true)
	protected String id;
	@XmlAttribute
	protected Integer max;
	@XmlAttribute
	protected Integer min;
	@XmlAttribute(required = true)
	protected String name;
	@XmlAttribute
	protected Boolean readOnly;
	@XmlAttribute
	protected String status;
	@XmlAttribute(required = true)
	protected String type;
	@XmlAttribute
	protected Boolean visible;

	/**
	 * Gets the value of the choice property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getChoice() {
		return choice;
	}

	/**
	 * Gets the value of the default property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDefault() {
		return _default;
	}

	/**
	 * Gets the value of the description property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the value of the max property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMax() {
		return max;
	}

	/**
	 * Gets the value of the min property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMin() {
		return min;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value of the status property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Gets the value of the tooltip property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the value of the validator property.
	 * 
	 * @return possible object is {@link Validator }
	 * 
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the value of the basic property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isBasic() {
		if (basic == null) {
			return false;
		} else {
			return basic;
		}
	}

	/**
	 * Gets the value of the readOnly property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isReadOnly() {
		if (readOnly == null) {
			return false;
		} else {
			return readOnly;
		}
	}

	/**
	 * Gets the value of the visible property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isVisible() {
		if (visible == null) {
			return true;
		} else {
			return visible;
		}
	}

	/**
	 * Sets the value of the basic property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setBasic(Boolean value) {
		this.basic = value;
	}

	/**
	 * Sets the value of the choice property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setChoice(String value) {
		this.choice = value;
	}

	/**
	 * Sets the value of the default property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDefault(String value) {
		this._default = value;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescription(String value) {
		this.description = value;
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
	 * Sets the value of the max property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMax(Integer value) {
		this.max = value;
	}

	/**
	 * Sets the value of the min property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMin(Integer value) {
		this.min = value;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Sets the value of the readOnly property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setReadOnly(Boolean value) {
		this.readOnly = value;
	}

	/**
	 * Sets the value of the status property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStatus(String value) {
		this.status = value;
	}

	/**
	 * Sets the value of the tooltip property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTooltip(String value) {
		this.tooltip = value;
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

	/**
	 * Sets the value of the validator property.
	 * 
	 * @param value
	 *            allowed object is {@link Validator }
	 * 
	 */
	public void setValidator(Validator value) {
		this.validator = value;
	}

	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 * 
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Sets the value of the visible property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setVisible(Boolean value) {
		this.visible = value;
	}

}
