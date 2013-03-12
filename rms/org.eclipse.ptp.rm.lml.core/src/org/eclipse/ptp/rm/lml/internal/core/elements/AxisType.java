//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.22 at 09:47:01 AM CET 
//


package org.eclipse.ptp.rm.lml.internal.core.elements;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for either x- or y-axis. Contains important data of these objects.
 * 
 * <p>Java class for axis_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="axis_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ticlabels" type="{http://eclipse.org/ptp/schemas}ticlabels_type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://eclipse.org/ptp/schemas}axis_type_constants" default="linear" />
 *       &lt;attribute name="unit" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shortlabel" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="min" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="max" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="ticcount" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="10" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "axis_type", propOrder = {
    "ticlabels"
})
public class AxisType {

    protected TiclabelsType ticlabels;
    @XmlAttribute(name = "type")
    protected AxisTypeConstants type;
    @XmlAttribute(name = "unit")
    protected String unit;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "shortlabel")
    protected String shortlabel;
    @XmlAttribute(name = "min", required = true)
    protected double min;
    @XmlAttribute(name = "max", required = true)
    protected double max;
    @XmlAttribute(name = "ticcount")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger ticcount;

    /**
     * Gets the value of the ticlabels property.
     * 
     * @return
     *     possible object is
     *     {@link TiclabelsType }
     *     
     */
    public TiclabelsType getTiclabels() {
        return ticlabels;
    }

    /**
     * Sets the value of the ticlabels property.
     * 
     * @param value
     *     allowed object is
     *     {@link TiclabelsType }
     *     
     */
    public void setTiclabels(TiclabelsType value) {
        this.ticlabels = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AxisTypeConstants }
     *     
     */
    public AxisTypeConstants getType() {
        if (type == null) {
            return AxisTypeConstants.LINEAR;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AxisTypeConstants }
     *     
     */
    public void setType(AxisTypeConstants value) {
        this.type = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the shortlabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortlabel() {
        return shortlabel;
    }

    /**
     * Sets the value of the shortlabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortlabel(String value) {
        this.shortlabel = value;
    }

    /**
     * Gets the value of the min property.
     * 
     */
    public double getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     */
    public void setMin(double value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     * 
     */
    public double getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     */
    public void setMax(double value) {
        this.max = value;
    }

    /**
     * Gets the value of the ticcount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTiccount() {
        if (ticcount == null) {
            return new BigInteger("10");
        } else {
            return ticcount;
        }
    }

    /**
     * Sets the value of the ticcount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTiccount(BigInteger value) {
        this.ticcount = value;
    }

}
