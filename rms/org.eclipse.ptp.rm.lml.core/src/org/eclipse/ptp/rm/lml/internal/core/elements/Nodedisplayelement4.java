//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.04 at 10:04:52 AM CEST 
//


package org.eclipse.ptp.rm.lml.internal.core.elements;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nodedisplayelement4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nodedisplayelement4">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}nodedisplayelement">
 *       &lt;sequence>
 *         &lt;element name="el5" type="{http://www.llview.de}nodedisplayelement5" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodedisplayelement4", propOrder = {
    "el5"
})
public class Nodedisplayelement4
    extends Nodedisplayelement
 implements Serializable {

    protected List<Nodedisplayelement5> el5;

    /**
     * Gets the value of the el5 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the el5 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEl5().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Nodedisplayelement5 }
     * 
     * 
     */
    public List<Nodedisplayelement5> getEl5() {
        if (el5 == null) {
            el5 = new ArrayList<Nodedisplayelement5>();
        }
        return this.el5;
    }

}
