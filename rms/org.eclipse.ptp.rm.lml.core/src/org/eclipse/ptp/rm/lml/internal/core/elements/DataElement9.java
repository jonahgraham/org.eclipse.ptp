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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for data_element9 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="data_element9">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}data_element">
 *       &lt;sequence>
 *         &lt;element name="el10" type="{http://www.llview.de}data_element10" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "data_element9", propOrder = {
    "el10"
})
public class DataElement9
    extends DataElement
 implements Serializable {

    protected List<DataElement10> el10;

    /**
     * Gets the value of the el10 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the el10 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEl10().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataElement10 }
     * 
     * 
     */
    public List<DataElement10> getEl10() {
        if (el10 == null) {
            el10 = new ArrayList<DataElement10>();
        }
        return this.el10;
    }

}
