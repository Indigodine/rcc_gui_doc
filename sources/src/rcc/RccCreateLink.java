
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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destination-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-output" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source-port" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destination-port" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "name",
    "sourceName",
    "destinationName",
    "sourceOutput",
    "sourcePort",
    "destinationPort",
    "bufferSize"
})
@XmlRootElement(name = "rccCreateLink")
public class RccCreateLink {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "source-name", required = true)
    protected String sourceName;
    @XmlElement(name = "destination-name", required = true)
    protected String destinationName;
    @XmlElement(name = "source-output", required = true)
    protected String sourceOutput;
    @XmlElement(name = "source-port", required = true)
    protected String sourcePort;
    @XmlElement(name = "destination-port", required = true)
    protected String destinationPort;
    @XmlElement(name = "buffer-size")
    protected int bufferSize;

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
     * Gets the value of the destinationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * Sets the value of the destinationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationName(String value) {
        this.destinationName = value;
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
     * Gets the value of the destinationPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationPort() {
        return destinationPort;
    }

    /**
     * Sets the value of the destinationPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationPort(String value) {
        this.destinationPort = value;
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
