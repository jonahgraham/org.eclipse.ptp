//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.15 at 04:31:32 PM CET 
//


package org.eclipse.ptp.rm.lml.internal.core.elements;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A group of charts. Could be displayed as overlay-diagrams.
 * 
 * <p>Java class for chartgroup_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="chartgroup_type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}gobject_type">
 *       &lt;sequence>
 *         &lt;element name="chart" type="{http://www.llview.de}chart_type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "chartgroup_type", propOrder = {
    "chart"
})
public class ChartgroupType
    extends GobjectType
 implements Serializable {

    @XmlElement(required = true)
    protected List<ChartType> chart;

    /**
     * Gets the value of the chart property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chart property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChartType }
     * 
     * 
     */
    public List<ChartType> getChart() {
        if (chart == null) {
            chart = new ArrayList<ChartType>();
        }
        return this.chart;
    }

}
