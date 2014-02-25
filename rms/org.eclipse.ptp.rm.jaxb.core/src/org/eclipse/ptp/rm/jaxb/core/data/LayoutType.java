//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.14 at 08:46:16 AM CET 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for layout-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="layout-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="fill-layout" type="{http://eclipse.org/ptp/rm}fill-layout-type"/>
 *         &lt;element name="row-layout" type="{http://eclipse.org/ptp/rm}row-layout-type"/>
 *         &lt;element name="grid-layout" type="{http://eclipse.org/ptp/rm}grid-layout-type"/>
 *         &lt;element name="form-layout" type="{http://eclipse.org/ptp/rm}form-layout-type"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "layout-type", propOrder = {
		"fillLayout",
		"rowLayout",
		"gridLayout",
		"formLayout"
})
public class LayoutType {

	@XmlElement(name = "fill-layout")
	protected FillLayoutType fillLayout;
	@XmlElement(name = "row-layout")
	protected RowLayoutType rowLayout;
	@XmlElement(name = "grid-layout")
	protected GridLayoutType gridLayout;
	@XmlElement(name = "form-layout")
	protected FormLayoutType formLayout;

	/**
	 * Gets the value of the fillLayout property.
	 * 
	 * @return
	 *         possible object is {@link FillLayoutType }
	 * 
	 */
	public FillLayoutType getFillLayout() {
		return fillLayout;
	}

	/**
	 * Sets the value of the fillLayout property.
	 * 
	 * @param value
	 *            allowed object is {@link FillLayoutType }
	 * 
	 */
	public void setFillLayout(FillLayoutType value) {
		this.fillLayout = value;
	}

	/**
	 * Gets the value of the rowLayout property.
	 * 
	 * @return
	 *         possible object is {@link RowLayoutType }
	 * 
	 */
	public RowLayoutType getRowLayout() {
		return rowLayout;
	}

	/**
	 * Sets the value of the rowLayout property.
	 * 
	 * @param value
	 *            allowed object is {@link RowLayoutType }
	 * 
	 */
	public void setRowLayout(RowLayoutType value) {
		this.rowLayout = value;
	}

	/**
	 * Gets the value of the gridLayout property.
	 * 
	 * @return
	 *         possible object is {@link GridLayoutType }
	 * 
	 */
	public GridLayoutType getGridLayout() {
		return gridLayout;
	}

	/**
	 * Sets the value of the gridLayout property.
	 * 
	 * @param value
	 *            allowed object is {@link GridLayoutType }
	 * 
	 */
	public void setGridLayout(GridLayoutType value) {
		this.gridLayout = value;
	}

	/**
	 * Gets the value of the formLayout property.
	 * 
	 * @return
	 *         possible object is {@link FormLayoutType }
	 * 
	 */
	public FormLayoutType getFormLayout() {
		return formLayout;
	}

	/**
	 * Sets the value of the formLayout property.
	 * 
	 * @param value
	 *            allowed object is {@link FormLayoutType }
	 * 
	 */
	public void setFormLayout(FormLayoutType value) {
		this.formLayout = value;
	}

}
