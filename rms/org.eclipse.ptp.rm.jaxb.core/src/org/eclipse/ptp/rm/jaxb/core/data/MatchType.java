//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.14 at 08:46:16 AM CET 
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
 * <p>
 * Java class for match-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="match-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expression" type="{http://eclipse.org/ptp/rm}regex-type" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="add" type="{http://eclipse.org/ptp/rm}add-type"/>
 *           &lt;element name="append" type="{http://eclipse.org/ptp/rm}append-type"/>
 *           &lt;element name="put" type="{http://eclipse.org/ptp/rm}put-type"/>
 *           &lt;element name="set" type="{http://eclipse.org/ptp/rm}set-type"/>
 *           &lt;element name="throw" type="{http://eclipse.org/ptp/rm}throw-type"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="moveToTop" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "match-type", propOrder = {
		"expression",
		"addOrAppendOrPut"
})
public class MatchType {

	protected RegexType expression;
	@XmlElements({
			@XmlElement(name = "add", type = AddType.class),
			@XmlElement(name = "append", type = AppendType.class),
			@XmlElement(name = "put", type = PutType.class),
			@XmlElement(name = "set", type = SetType.class),
			@XmlElement(name = "throw", type = ThrowType.class)
	})
	protected List<Object> addOrAppendOrPut;
	@XmlAttribute(name = "moveToTop")
	protected Boolean moveToTop;

	/**
	 * Gets the value of the expression property.
	 * 
	 * @return
	 *         possible object is {@link RegexType }
	 * 
	 */
	public RegexType getExpression() {
		return expression;
	}

	/**
	 * Sets the value of the expression property.
	 * 
	 * @param value
	 *            allowed object is {@link RegexType }
	 * 
	 */
	public void setExpression(RegexType value) {
		this.expression = value;
	}

	/**
	 * Gets the value of the addOrAppendOrPut property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the
	 * addOrAppendOrPut property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAddOrAppendOrPut().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link AddType } {@link AppendType } {@link PutType } {@link SetType }
	 * {@link ThrowType }
	 * 
	 * 
	 */
	public List<Object> getAddOrAppendOrPut() {
		if (addOrAppendOrPut == null) {
			addOrAppendOrPut = new ArrayList<Object>();
		}
		return this.addOrAppendOrPut;
	}

	/**
	 * Gets the value of the moveToTop property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public boolean isMoveToTop() {
		if (moveToTop == null) {
			return false;
		} else {
			return moveToTop;
		}
	}

	/**
	 * Sets the value of the moveToTop property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setMoveToTop(Boolean value) {
		this.moveToTop = value;
	}

}
