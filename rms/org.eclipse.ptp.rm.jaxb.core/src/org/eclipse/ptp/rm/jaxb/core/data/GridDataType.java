//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.05 at 08:09:06 AM EST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for grid-data-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="grid-data-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="horizontalAlign" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="verticalAlign" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="grabExcessHorizontal" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="grabExcessVertical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="horizontalSpan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="verticalSpan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="horizontalIndent" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="verticalIndent" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="minHeight" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="minWidth" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="heightHint" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="widthHint" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grid-data-type")
public class GridDataType {

	@XmlAttribute(name = "style")
	protected String style;
	@XmlAttribute(name = "horizontalAlign")
	protected String horizontalAlign;
	@XmlAttribute(name = "verticalAlign")
	protected String verticalAlign;
	@XmlAttribute(name = "grabExcessHorizontal")
	protected Boolean grabExcessHorizontal;
	@XmlAttribute(name = "grabExcessVertical")
	protected Boolean grabExcessVertical;
	@XmlAttribute(name = "horizontalSpan")
	protected Integer horizontalSpan;
	@XmlAttribute(name = "verticalSpan")
	protected Integer verticalSpan;
	@XmlAttribute(name = "horizontalIndent")
	protected Integer horizontalIndent;
	@XmlAttribute(name = "verticalIndent")
	protected Integer verticalIndent;
	@XmlAttribute(name = "minHeight")
	protected Integer minHeight;
	@XmlAttribute(name = "minWidth")
	protected Integer minWidth;
	@XmlAttribute(name = "heightHint")
	protected Integer heightHint;
	@XmlAttribute(name = "widthHint")
	protected Integer widthHint;

	/**
	 * Gets the value of the style property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the value of the style property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStyle(String value) {
		this.style = value;
	}

	/**
	 * Gets the value of the horizontalAlign property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getHorizontalAlign() {
		return horizontalAlign;
	}

	/**
	 * Sets the value of the horizontalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHorizontalAlign(String value) {
		this.horizontalAlign = value;
	}

	/**
	 * Gets the value of the verticalAlign property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getVerticalAlign() {
		return verticalAlign;
	}

	/**
	 * Sets the value of the verticalAlign property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVerticalAlign(String value) {
		this.verticalAlign = value;
	}

	/**
	 * Gets the value of the grabExcessHorizontal property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public boolean isGrabExcessHorizontal() {
		if (grabExcessHorizontal == null) {
			return false;
		} else {
			return grabExcessHorizontal;
		}
	}

	/**
	 * Sets the value of the grabExcessHorizontal property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setGrabExcessHorizontal(Boolean value) {
		this.grabExcessHorizontal = value;
	}

	/**
	 * Gets the value of the grabExcessVertical property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public boolean isGrabExcessVertical() {
		if (grabExcessVertical == null) {
			return false;
		} else {
			return grabExcessVertical;
		}
	}

	/**
	 * Sets the value of the grabExcessVertical property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setGrabExcessVertical(Boolean value) {
		this.grabExcessVertical = value;
	}

	/**
	 * Gets the value of the horizontalSpan property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getHorizontalSpan() {
		return horizontalSpan;
	}

	/**
	 * Sets the value of the horizontalSpan property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHorizontalSpan(Integer value) {
		this.horizontalSpan = value;
	}

	/**
	 * Gets the value of the verticalSpan property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getVerticalSpan() {
		return verticalSpan;
	}

	/**
	 * Sets the value of the verticalSpan property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setVerticalSpan(Integer value) {
		this.verticalSpan = value;
	}

	/**
	 * Gets the value of the horizontalIndent property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getHorizontalIndent() {
		return horizontalIndent;
	}

	/**
	 * Sets the value of the horizontalIndent property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHorizontalIndent(Integer value) {
		this.horizontalIndent = value;
	}

	/**
	 * Gets the value of the verticalIndent property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getVerticalIndent() {
		return verticalIndent;
	}

	/**
	 * Sets the value of the verticalIndent property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setVerticalIndent(Integer value) {
		this.verticalIndent = value;
	}

	/**
	 * Gets the value of the minHeight property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getMinHeight() {
		return minHeight;
	}

	/**
	 * Sets the value of the minHeight property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMinHeight(Integer value) {
		this.minHeight = value;
	}

	/**
	 * Gets the value of the minWidth property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getMinWidth() {
		return minWidth;
	}

	/**
	 * Sets the value of the minWidth property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMinWidth(Integer value) {
		this.minWidth = value;
	}

	/**
	 * Gets the value of the heightHint property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getHeightHint() {
		return heightHint;
	}

	/**
	 * Sets the value of the heightHint property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setHeightHint(Integer value) {
		this.heightHint = value;
	}

	/**
	 * Gets the value of the widthHint property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getWidthHint() {
		return widthHint;
	}

	/**
	 * Sets the value of the widthHint property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setWidthHint(Integer value) {
		this.widthHint = value;
	}

}
