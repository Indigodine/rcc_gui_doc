
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
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="narval-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="executable-file" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="debug-level" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "host",
    "narvalName",
    "executableFile",
    "debugLevel"
})
@XmlRootElement(name = "rccCreateSbufProducer")
public class RccCreateSbufProducer {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String host;
    @XmlElement(name = "narval-name", required = true)
    protected String narvalName;
    @XmlElement(name = "executable-file", required = true)
    protected String executableFile;
    @XmlElement(name = "debug-level", required = true)
    protected String debugLevel;

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
     * Gets the value of the host property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of the host property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Gets the value of the narvalName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarvalName() {
        return narvalName;
    }

    /**
     * Sets the value of the narvalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarvalName(String value) {
        this.narvalName = value;
    }

    /**
     * Gets the value of the executableFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExecutableFile() {
        return executableFile;
    }

    /**
     * Sets the value of the executableFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExecutableFile(String value) {
        this.executableFile = value;
    }

    /**
     * Gets the value of the debugLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebugLevel() {
        return debugLevel;
    }

    /**
     * Sets the value of the debugLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebugLevel(String value) {
        this.debugLevel = value;
    }

}
