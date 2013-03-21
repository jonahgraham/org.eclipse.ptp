//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.21 at 10:32:34 AM EDT 
//


package org.eclipse.ptp.etfw.jaxb.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EtfwToolProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EtfwToolProcessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="controlData" type="{http://eclipse.org/ptp/etfw}ControlDataType" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="execTool" type="{http://eclipse.org/ptp/etfw}ExecToolType"/>
 *           &lt;element name="postProcTool" type="{http://eclipse.org/ptp/etfw}PostProcToolType"/>
 *           &lt;element name="buildTool" type="{http://eclipse.org/ptp/etfw}BuildToolType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="prepend-execution" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="recompile" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="explicit-execution" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EtfwToolProcessType", namespace = "http://eclipse.org/ptp/etfw", propOrder = {
    "controlData",
    "execToolOrPostProcToolOrBuildTool"
})
public class EtfwToolProcessType {

    protected ControlDataType controlData;
    @XmlElements({
        @XmlElement(name = "execTool", type = ExecToolType.class),
        @XmlElement(name = "postProcTool", type = PostProcToolType.class),
        @XmlElement(name = "buildTool", type = BuildToolType.class)
    })
    protected List<Object> execToolOrPostProcToolOrBuildTool;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "prepend-execution")
    protected Boolean prependExecution;
    @XmlAttribute(name = "recompile")
    protected Boolean recompile;
    @XmlAttribute(name = "explicit-execution")
    protected Boolean explicitExecution;

    /**
     * Gets the value of the controlData property.
     * 
     * @return
     *     possible object is
     *     {@link ControlDataType }
     *     
     */
    public ControlDataType getControlData() {
        return controlData;
    }

    /**
     * Sets the value of the controlData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlDataType }
     *     
     */
    public void setControlData(ControlDataType value) {
        this.controlData = value;
    }

    /**
     * Gets the value of the execToolOrPostProcToolOrBuildTool property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the execToolOrPostProcToolOrBuildTool property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecToolOrPostProcToolOrBuildTool().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExecToolType }
     * {@link PostProcToolType }
     * {@link BuildToolType }
     * 
     * 
     */
    public List<Object> getExecToolOrPostProcToolOrBuildTool() {
        if (execToolOrPostProcToolOrBuildTool == null) {
            execToolOrPostProcToolOrBuildTool = new ArrayList<Object>();
        }
        return this.execToolOrPostProcToolOrBuildTool;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the prependExecution property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrependExecution() {
        if (prependExecution == null) {
            return false;
        } else {
            return prependExecution;
        }
    }

    /**
     * Sets the value of the prependExecution property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrependExecution(Boolean value) {
        this.prependExecution = value;
    }

    /**
     * Gets the value of the recompile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRecompile() {
        if (recompile == null) {
            return false;
        } else {
            return recompile;
        }
    }

    /**
     * Sets the value of the recompile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRecompile(Boolean value) {
        this.recompile = value;
    }

    /**
     * Gets the value of the explicitExecution property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isExplicitExecution() {
        if (explicitExecution == null) {
            return false;
        } else {
            return explicitExecution;
        }
    }

    /**
     * Sets the value of the explicitExecution property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExplicitExecution(Boolean value) {
        this.explicitExecution = value;
    }

}
