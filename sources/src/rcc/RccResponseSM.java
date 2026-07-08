
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
 *         &lt;element name="SMState" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SMTransition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SMError" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "smState",
    "smTransition",
    "smError"
})
@XmlRootElement(name = "rccResponseSM")
public class RccResponseSM {

    @XmlElement(name = "Error")
    protected int error;
    @XmlElement(name = "ErrorMessage", required = true)
    protected String errorMessage;
    @XmlElement(name = "SMState")
    protected int smState;
    @XmlElement(name = "SMTransition")
    protected int smTransition;
    @XmlElement(name = "SMError")
    protected int smError;

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
     * Gets the value of the smState property.
     * 
     */
    public int getSMState() {
        return smState;
    }

    /**
     * Sets the value of the smState property.
     * 
     */
    public void setSMState(int value) {
        this.smState = value;
    }

    /**
     * Gets the value of the smTransition property.
     * 
     */
    public int getSMTransition() {
        return smTransition;
    }

    /**
     * Sets the value of the smTransition property.
     * 
     */
    public void setSMTransition(int value) {
        this.smTransition = value;
    }

    /**
     * Gets the value of the smError property.
     * 
     */
    public int getSMError() {
        return smError;
    }

    /**
     * Sets the value of the smError property.
     * 
     */
    public void setSMError(int value) {
        this.smError = value;
    }

}
