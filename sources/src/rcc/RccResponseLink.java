
package rcc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dest-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-output" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-port" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dest-port" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="buffer-size" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "error",
    "errorMessage",
    "name",
    "sourceName",
    "destName",
    "sourceOutput",
    "sourcePort",
    "destPort",
    "bufferSize"
})
@XmlRootElement(name = "rccResponseLink")
public class RccResponseLink {

    @XmlElement(name = "Error")
    protected int error;
    @XmlElement(name = "ErrorMessage", required = true)
    protected String errorMessage;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "source-name", required = true)
    protected String sourceName;
    @XmlElement(name = "dest-name", required = true)
    protected String destName;
    @XmlElement(name = "source-output", required = true)
    protected String sourceOutput;
    @XmlElement(name = "source-port", required = true)
    protected String sourcePort;
    @XmlElement(name = "dest-port", required = true)
    protected String destPort;
    @XmlElement(name = "buffer-size")
    protected int bufferSize;

    /**
     * Gets the value of the error property.
     * 
     */
    public int getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     */
    public void setError(int value) {
        this.error = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
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
     * Gets the value of the sourceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Sets the value of the sourceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceName(String value) {
        this.sourceName = value;
    }

    /**
     * Gets the value of the destName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestName() {
        return destName;
    }

    /**
     * Sets the value of the destName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestName(String value) {
        this.destName = value;
    }

    /**
     * Gets the value of the sourceOutput property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceOutput() {
        return sourceOutput;
    }

    /**
     * Sets the value of the sourceOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceOutput(String value) {
        this.sourceOutput = value;
    }

    /**
     * Gets the value of the sourcePort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcePort() {
        return sourcePort;
    }

    /**
     * Sets the value of the sourcePort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcePort(String value) {
        this.sourcePort = value;
    }

    /**
     * Gets the value of the destPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestPort() {
        return destPort;
    }

    /**
     * Sets the value of the destPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestPort(String value) {
        this.destPort = value;
    }

    /**
     * Gets the value of the bufferSize property.
     * 
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets the value of the bufferSize property.
     * 
     */
    public void setBufferSize(int value) {
        this.bufferSize = value;
    }

}
