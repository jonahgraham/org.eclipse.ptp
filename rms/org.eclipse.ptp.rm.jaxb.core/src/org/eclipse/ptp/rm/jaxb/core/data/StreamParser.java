//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.09 at 02:55:11 PM CST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * These are attached to the stdout or stderr streams in order to capture the
 * output of the command and add values into the resource manager environment.
 * If displayStdout or displayStderr of the command is true, the stream will be
 * passed on to an output stream will also be sent to the terminal.
 * 
 * 
 * <p>
 * Java class for stream-parser complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="stream-parser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tokenizer" type="{http://org.eclipse.ptp/rm}tokenizer"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stderr" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stream-parser", propOrder = { "tokenizer" })
public class StreamParser {

	@XmlElement(required = true)
	protected Tokenizer tokenizer;
	@XmlAttribute(required = true)
	protected String name;
	@XmlAttribute
	protected Boolean stderr;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value of the tokenizer property.
	 * 
	 * @return possible object is {@link Tokenizer }
	 * 
	 */
	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	/**
	 * Gets the value of the stderr property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isStderr() {
		if (stderr == null) {
			return false;
		} else {
			return stderr;
		}
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Sets the value of the stderr property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setStderr(Boolean value) {
		this.stderr = value;
	}

	/**
	 * Sets the value of the tokenizer property.
	 * 
	 * @param value
	 *            allowed object is {@link Tokenizer }
	 * 
	 */
	public void setTokenizer(Tokenizer value) {
		this.tokenizer = value;
	}

}
