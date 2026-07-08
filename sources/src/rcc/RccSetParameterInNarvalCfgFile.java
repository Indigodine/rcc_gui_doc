
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
 *         &lt;element name="equip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="in" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "equip",
    "name",
    "in"
})
@XmlRootElement(name = "rccSetParameterInNarvalCfgFile")
public class RccSetParameterInNarvalCfgFile {

    @XmlElement(required = true)
    protected String equip;
    @XmlElement(required = true)
    protected String name;
    protected boolean in;

    /**
     * Gets the value of the equip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEquip() {
        return equip;
    }

    /**
     * Sets the value of the equip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEquip(String value) {
        this.equip = value;
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
     * Gets the value of the in property.
     * 
     */
    public boolean isIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     * 
     */
    public void setIn(boolean value) {
        this.in = value;
    }

}
