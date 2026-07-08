
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
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="loggername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="blocksize" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "username",
    "expname",
    "loggername",
    "blocksize"
})
@XmlRootElement(name = "rccCreateVmecom")
public class RccCreateVmecom {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String host;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true)
    protected String expname;
    @XmlElement(required = true)
    protected String loggername;
    protected int blocksize;

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
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the expname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpname() {
        return expname;
    }

    /**
     * Sets the value of the expname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpname(String value) {
        this.expname = value;
    }

    /**
     * Gets the value of the loggername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoggername() {
        return loggername;
    }

    /**
     * Sets the value of the loggername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoggername(String value) {
        this.loggername = value;
    }

    /**
     * Gets the value of the blocksize property.
     * 
     */
    public int getBlocksize() {
        return blocksize;
    }

    /**
     * Sets the value of the blocksize property.
     * 
     */
    public void setBlocksize(int value) {
        this.blocksize = value;
    }

}
