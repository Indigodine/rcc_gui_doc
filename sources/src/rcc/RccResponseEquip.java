
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
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="typestr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cpu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="narval" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exe" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="log" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="loggername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="blocksize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "type",
    "typestr",
    "host",
    "cpu",
    "narval",
    "exe",
    "log",
    "username",
    "expname",
    "loggername",
    "blocksize",
    "port",
    "id"
})
@XmlRootElement(name = "rccResponseEquip")
public class RccResponseEquip {

    @XmlElement(name = "Error")
    protected int error;
    @XmlElement(name = "ErrorMessage", required = true)
    protected String errorMessage;
    @XmlElement(required = true)
    protected String name;
    protected int type;
    @XmlElement(required = true)
    protected String typestr;
    @XmlElement(required = true)
    protected String host;
    @XmlElement(required = true)
    protected String cpu;
    @XmlElement(required = true)
    protected String narval;
    @XmlElement(required = true)
    protected String exe;
    @XmlElement(required = true)
    protected String log;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true)
    protected String expname;
    @XmlElement(required = true)
    protected String loggername;
    protected int blocksize;
    protected int port;
    @XmlElement(required = true)
    protected String id;

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
     * Gets the value of the type property.
     * 
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(int value) {
        this.type = value;
    }

    /**
     * Gets the value of the typestr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypestr() {
        return typestr;
    }

    /**
     * Sets the value of the typestr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypestr(String value) {
        this.typestr = value;
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
     * Gets the value of the cpu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * Sets the value of the cpu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpu(String value) {
        this.cpu = value;
    }

    /**
     * Gets the value of the narval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarval() {
        return narval;
    }

    /**
     * Sets the value of the narval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarval(String value) {
        this.narval = value;
    }

    /**
     * Gets the value of the exe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExe() {
        return exe;
    }

    /**
     * Sets the value of the exe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExe(String value) {
        this.exe = value;
    }

    /**
     * Gets the value of the log property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLog() {
        return log;
    }

    /**
     * Sets the value of the log property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLog(String value) {
        this.log = value;
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

    /**
     * Gets the value of the port property.
     * 
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     */
    public void setPort(int value) {
        this.port = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
