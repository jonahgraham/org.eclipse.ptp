//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.10 at 09:53:19 AM CEST 
//


package org.eclipse.ptp.rm.lml.internal.core.elements;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *     			An abstract table. Defines several columns, which can be
 *     			sorted by different datatypes. Then data is defined in
 *     			row-tags, which can be linked with object-tags.
 *     		
 * 
 * <p>Java class for table_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="table_type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}gobject_type">
 *       &lt;sequence>
 *         &lt;element name="column" type="{http://www.llview.de}column_type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="row" type="{http://www.llview.de}row_type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="contenttype" default="other">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="jobs"/>
 *             &lt;enumeration value="nodes"/>
 *             &lt;enumeration value="users"/>
 *             &lt;enumeration value="groups"/>
 *             &lt;enumeration value="classes"/>
 *             &lt;enumeration value="queues"/>
 *             &lt;enumeration value="other"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "table_type", propOrder = {
    "column",
    "row"
})
public class TableType
    extends GobjectType
 implements Serializable {

    protected List<ColumnType> column;
    protected List<RowType> row;
    @XmlAttribute
    protected String contenttype;

    /**
     * Gets the value of the column property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the column property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColumnType }
     * 
     * 
     */
    public List<ColumnType> getColumn() {
        if (column == null) {
            column = new ArrayList<ColumnType>();
        }
        return this.column;
    }

    /**
     * Gets the value of the row property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the row property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RowType }
     * 
     * 
     */
    public List<RowType> getRow() {
        if (row == null) {
            row = new ArrayList<RowType>();
        }
        return this.row;
    }

    /**
     * Gets the value of the contenttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContenttype() {
        if (contenttype == null) {
            return "other"; //$NON-NLS-1$
        } else {
            return contenttype;
        }
    }

    /**
     * Sets the value of the contenttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContenttype(String value) {
        this.contenttype = value;
    }

}
