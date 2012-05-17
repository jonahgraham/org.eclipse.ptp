//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.16 at 09:58:59 AM EDT 
//


package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tab-controller-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tab-controller-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layout" type="{http://org.eclipse.ptp/rm}layout-type" minOccurs="0"/>
 *         &lt;element name="layout-data" type="{http://org.eclipse.ptp/rm}layout-data-type" minOccurs="0"/>
 *         &lt;element name="font" type="{http://org.eclipse.ptp/rm}font-type" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="tab-folder" type="{http://org.eclipse.ptp/rm}tab-folder-type"/>
 *           &lt;element name="composite" type="{http://org.eclipse.ptp/rm}composite-type"/>
 *           &lt;element name="widget" type="{http://org.eclipse.ptp/rm}widget-type"/>
 *           &lt;element name="browse" type="{http://org.eclipse.ptp/rm}browse-type"/>
 *           &lt;element name="action" type="{http://org.eclipse.ptp/rm}push-button-type"/>
 *           &lt;element name="button-group" type="{http://org.eclipse.ptp/rm}button-group-type"/>
 *           &lt;element name="viewer" type="{http://org.eclipse.ptp/rm}attribute-viewer-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="background" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="includeWidgetValuesFrom" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="showViewConfig" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="showViewExcluded" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tab-controller-type", propOrder = {
    "title",
    "layout",
    "layoutData",
    "font",
    "tabFolderOrCompositeOrWidget"
})
public class TabControllerType {

    @XmlElement(required = true)
    protected String title;
    protected LayoutType layout;
    @XmlElement(name = "layout-data")
    protected LayoutDataType layoutData;
    protected FontType font;
    @XmlElements({
        @XmlElement(name = "tab-folder", type = TabFolderType.class),
        @XmlElement(name = "button-group", type = ButtonGroupType.class),
        @XmlElement(name = "action", type = PushButtonType.class),
        @XmlElement(name = "composite", type = CompositeType.class),
        @XmlElement(name = "widget", type = WidgetType.class),
        @XmlElement(name = "browse", type = BrowseType.class),
        @XmlElement(name = "viewer", type = AttributeViewerType.class)
    })
    protected List<Object> tabFolderOrCompositeOrWidget;
    @XmlAttribute
    protected String style;
    @XmlAttribute
    protected String background;
    @XmlAttribute
    protected String includeWidgetValuesFrom;
    @XmlAttribute
    protected Boolean showViewConfig;
    @XmlAttribute
    protected Boolean showViewExcluded;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the layout property.
     * 
     * @return
     *     possible object is
     *     {@link LayoutType }
     *     
     */
    public LayoutType getLayout() {
        return layout;
    }

    /**
     * Sets the value of the layout property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayoutType }
     *     
     */
    public void setLayout(LayoutType value) {
        this.layout = value;
    }

    /**
     * Gets the value of the layoutData property.
     * 
     * @return
     *     possible object is
     *     {@link LayoutDataType }
     *     
     */
    public LayoutDataType getLayoutData() {
        return layoutData;
    }

    /**
     * Sets the value of the layoutData property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayoutDataType }
     *     
     */
    public void setLayoutData(LayoutDataType value) {
        this.layoutData = value;
    }

    /**
     * Gets the value of the font property.
     * 
     * @return
     *     possible object is
     *     {@link FontType }
     *     
     */
    public FontType getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontType }
     *     
     */
    public void setFont(FontType value) {
        this.font = value;
    }

    /**
     * Gets the value of the tabFolderOrCompositeOrWidget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tabFolderOrCompositeOrWidget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTabFolderOrCompositeOrWidget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TabFolderType }
     * {@link ButtonGroupType }
     * {@link PushButtonType }
     * {@link CompositeType }
     * {@link WidgetType }
     * {@link BrowseType }
     * {@link AttributeViewerType }
     * 
     * 
     */
    public List<Object> getTabFolderOrCompositeOrWidget() {
        if (tabFolderOrCompositeOrWidget == null) {
            tabFolderOrCompositeOrWidget = new ArrayList<Object>();
        }
        return this.tabFolderOrCompositeOrWidget;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the background property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackground() {
        return background;
    }

    /**
     * Sets the value of the background property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackground(String value) {
        this.background = value;
    }

    /**
     * Gets the value of the includeWidgetValuesFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeWidgetValuesFrom() {
        return includeWidgetValuesFrom;
    }

    /**
     * Sets the value of the includeWidgetValuesFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeWidgetValuesFrom(String value) {
        this.includeWidgetValuesFrom = value;
    }

    /**
     * Gets the value of the showViewConfig property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isShowViewConfig() {
        if (showViewConfig == null) {
            return true;
        } else {
            return showViewConfig;
        }
    }

    /**
     * Sets the value of the showViewConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowViewConfig(Boolean value) {
        this.showViewConfig = value;
    }

    /**
     * Gets the value of the showViewExcluded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isShowViewExcluded() {
        if (showViewExcluded == null) {
            return false;
        } else {
            return showViewExcluded;
        }
    }

    /**
     * Sets the value of the showViewExcluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowViewExcluded(Boolean value) {
        this.showViewExcluded = value;
    }

}
