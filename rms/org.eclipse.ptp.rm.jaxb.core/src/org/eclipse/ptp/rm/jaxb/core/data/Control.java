//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.09 at 02:55:11 PM CST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * The implementation will construct a variable map serving as the resource
 * manager "environment"; these are dereferenced in the configuration file via
 * ${rm:name} or ${rm:name#getterName}; in addition to the properties specified
 * at the top level of the control tree, all attributes and parsers will be
 * mapped for reference. The following hard-coded variables are added at
 * runtime:
 * 
 * control.user.name control.address monitor.user.name monitor.address
 * 
 * available_queues
 * 
 * jobId
 * 
 * executablePath progArgs directory The top level of the tree represents the
 * available commands/methods on all resource managers, places for defining
 * stdout or stderr parsers, for defining the structure of a batch script to be
 * used, for defining (job) attributes, and finally for describing how to
 * construct the UI (Launch Configuration Tab).
 * 
 * 
 * <p>
 * Java class for control complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="control">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="property" type="{http://org.eclipse.ptp/rm}property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="job-attribute" type="{http://org.eclipse.ptp/rm}job-attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="managed-files" type="{http://org.eclipse.ptp/rm}managed-files" minOccurs="0"/>
 *         &lt;element name="script" type="{http://org.eclipse.ptp/rm}script" minOccurs="0"/>
 *         &lt;element name="start-up-command" type="{http://org.eclipse.ptp/rm}command" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="3">
 *           &lt;element name="submit-interactive" type="{http://org.eclipse.ptp/rm}command"/>
 *           &lt;element name="submit-batch" type="{http://org.eclipse.ptp/rm}command"/>
 *           &lt;element name="submit-debug" type="{http://org.eclipse.ptp/rm}command"/>
 *         &lt;/choice>
 *         &lt;element name="get-job-status" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="terminate-job" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="suspend-job" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="resume-job" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="hold-job" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="release-job" type="{http://org.eclipse.ptp/rm}command" minOccurs="0"/>
 *         &lt;element name="shut-down-command" type="{http://org.eclipse.ptp/rm}command" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="launch-tab" type="{http://org.eclipse.ptp/rm}launch-tab" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "control", propOrder = { "property", "jobAttribute", "managedFiles", "script", "startUpCommand",
		"submitInteractiveOrSubmitBatchOrSubmitDebug", "getJobStatus", "terminateJob", "suspendJob", "resumeJob", "holdJob",
		"releaseJob", "shutDownCommand", "launchTab" })
public class Control {

	protected List<Property> property;
	@XmlElement(name = "job-attribute")
	protected List<JobAttribute> jobAttribute;
	@XmlElement(name = "managed-files")
	protected ManagedFiles managedFiles;
	protected Script script;
	@XmlElement(name = "start-up-command")
	protected List<Command> startUpCommand;
	@XmlElementRefs({ @XmlElementRef(name = "submit-batch", namespace = "http://org.eclipse.ptp/rm", type = JAXBElement.class),
			@XmlElementRef(name = "submit-debug", namespace = "http://org.eclipse.ptp/rm", type = JAXBElement.class),
			@XmlElementRef(name = "submit-interactive", namespace = "http://org.eclipse.ptp/rm", type = JAXBElement.class) })
	protected List<JAXBElement<Command>> submitInteractiveOrSubmitBatchOrSubmitDebug;
	@XmlElement(name = "get-job-status")
	protected Command getJobStatus;
	@XmlElement(name = "terminate-job")
	protected Command terminateJob;
	@XmlElement(name = "suspend-job")
	protected Command suspendJob;
	@XmlElement(name = "resume-job")
	protected Command resumeJob;
	@XmlElement(name = "hold-job")
	protected Command holdJob;
	@XmlElement(name = "release-job")
	protected Command releaseJob;
	@XmlElement(name = "shut-down-command")
	protected List<Command> shutDownCommand;
	@XmlElement(name = "launch-tab")
	protected LaunchTab launchTab;

	/**
	 * Gets the value of the getJobStatus property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getGetJobStatus() {
		return getJobStatus;
	}

	/**
	 * Gets the value of the holdJob property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getHoldJob() {
		return holdJob;
	}

	/**
	 * Gets the value of the jobAttribute property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the jobAttribute property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getJobAttribute().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JobAttribute }
	 * 
	 * 
	 */
	public List<JobAttribute> getJobAttribute() {
		if (jobAttribute == null) {
			jobAttribute = new ArrayList<JobAttribute>();
		}
		return this.jobAttribute;
	}

	/**
	 * Gets the value of the launchTab property.
	 * 
	 * @return possible object is {@link LaunchTab }
	 * 
	 */
	public LaunchTab getLaunchTab() {
		return launchTab;
	}

	/**
	 * Gets the value of the managedFiles property.
	 * 
	 * @return possible object is {@link ManagedFiles }
	 * 
	 */
	public ManagedFiles getManagedFiles() {
		return managedFiles;
	}

	/**
	 * Gets the value of the property property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the property property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getProperty().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Property }
	 * 
	 * 
	 */
	public List<Property> getProperty() {
		if (property == null) {
			property = new ArrayList<Property>();
		}
		return this.property;
	}

	/**
	 * Gets the value of the releaseJob property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getReleaseJob() {
		return releaseJob;
	}

	/**
	 * Gets the value of the resumeJob property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getResumeJob() {
		return resumeJob;
	}

	/**
	 * Gets the value of the script property.
	 * 
	 * @return possible object is {@link Script }
	 * 
	 */
	public Script getScript() {
		return script;
	}

	/**
	 * Gets the value of the shutDownCommand property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the shutDownCommand property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getShutDownCommand().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Command }
	 * 
	 * 
	 */
	public List<Command> getShutDownCommand() {
		if (shutDownCommand == null) {
			shutDownCommand = new ArrayList<Command>();
		}
		return this.shutDownCommand;
	}

	/**
	 * Gets the value of the startUpCommand property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the startUpCommand property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getStartUpCommand().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Command }
	 * 
	 * 
	 */
	public List<Command> getStartUpCommand() {
		if (startUpCommand == null) {
			startUpCommand = new ArrayList<Command>();
		}
		return this.startUpCommand;
	}

	/**
	 * Gets the value of the submitInteractiveOrSubmitBatchOrSubmitDebug
	 * property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the
	 * submitInteractiveOrSubmitBatchOrSubmitDebug property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSubmitInteractiveOrSubmitBatchOrSubmitDebug().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link Command }{@code >} {@link JAXBElement }
	 * {@code <}{@link Command }{@code >} {@link JAXBElement }{@code <}
	 * {@link Command }{@code >}
	 * 
	 * 
	 */
	public List<JAXBElement<Command>> getSubmitInteractiveOrSubmitBatchOrSubmitDebug() {
		if (submitInteractiveOrSubmitBatchOrSubmitDebug == null) {
			submitInteractiveOrSubmitBatchOrSubmitDebug = new ArrayList<JAXBElement<Command>>();
		}
		return this.submitInteractiveOrSubmitBatchOrSubmitDebug;
	}

	/**
	 * Gets the value of the suspendJob property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getSuspendJob() {
		return suspendJob;
	}

	/**
	 * Gets the value of the terminateJob property.
	 * 
	 * @return possible object is {@link Command }
	 * 
	 */
	public Command getTerminateJob() {
		return terminateJob;
	}

	/**
	 * Sets the value of the getJobStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setGetJobStatus(Command value) {
		this.getJobStatus = value;
	}

	/**
	 * Sets the value of the holdJob property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setHoldJob(Command value) {
		this.holdJob = value;
	}

	/**
	 * Sets the value of the launchTab property.
	 * 
	 * @param value
	 *            allowed object is {@link LaunchTab }
	 * 
	 */
	public void setLaunchTab(LaunchTab value) {
		this.launchTab = value;
	}

	/**
	 * Sets the value of the managedFiles property.
	 * 
	 * @param value
	 *            allowed object is {@link ManagedFiles }
	 * 
	 */
	public void setManagedFiles(ManagedFiles value) {
		this.managedFiles = value;
	}

	/**
	 * Sets the value of the releaseJob property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setReleaseJob(Command value) {
		this.releaseJob = value;
	}

	/**
	 * Sets the value of the resumeJob property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setResumeJob(Command value) {
		this.resumeJob = value;
	}

	/**
	 * Sets the value of the script property.
	 * 
	 * @param value
	 *            allowed object is {@link Script }
	 * 
	 */
	public void setScript(Script value) {
		this.script = value;
	}

	/**
	 * Sets the value of the suspendJob property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setSuspendJob(Command value) {
		this.suspendJob = value;
	}

	/**
	 * Sets the value of the terminateJob property.
	 * 
	 * @param value
	 *            allowed object is {@link Command }
	 * 
	 */
	public void setTerminateJob(Command value) {
		this.terminateJob = value;
	}

}
