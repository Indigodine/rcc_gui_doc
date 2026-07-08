
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
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="permission" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="val" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="in" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dynamic" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="script" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "type",
    "permission",
    "val",
    "in",
    "dynamic",
    "script"
})
@XmlRootElement(name = "rccCreateParameter")
public class RccCreateParameter {

    @XmlElement(required = true)
    protected String equip;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String permission;
    @XmlElement(required = true)
    protected String val;
    protected boolean in;
    protected boolean dynamic;
    protected boolean script;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the permission property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the value of the permission property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPermission(String value) {
        this.permission = value;
    }

    /**
     * Gets the value of the val property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVal() {
        return val;
    }

    /**
     * Sets the value of the val property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVal(String value) {
        this.val = value;
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

    /**
     * Gets the value of the dynamic property.
     * 
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Sets the value of the dynamic property.
     * 
     */
    public void setDynamic(boolean value) {
        this.dynamic = value;
    }

    /**
     * Gets the value of the script property.
     * 
     */
    public boolean isScript() {
        return script;
    }

    /**
     * Sets the value of the script property.
     * 
     */
    public void setScript(boolean value) {
        this.script = value;
    }
}
