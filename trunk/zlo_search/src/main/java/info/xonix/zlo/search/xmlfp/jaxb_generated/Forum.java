
package info.xonix.zlo.search.xmlfp.jaxb_generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="charset" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="list"/>
 *               &lt;enumeration value="tree"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="xmlfpUrls">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="lastMessageNumberUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element ref="{}messageUrl"/>
 *                   &lt;element ref="{}messageListUrl" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="forumUrls">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="messageUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="userProfileUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "forum")
public class Forum {

    @XmlElement(required = true)
    protected String name;
    protected String description;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String url;
    protected String charset;
    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected Forum.XmlfpUrls xmlfpUrls;
    @XmlElement(required = true)
    protected Forum.ForumUrls forumUrls;

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the charset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the value of the charset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharset(String value) {
        this.charset = value;
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
     * Gets the value of the xmlfpUrls property.
     * 
     * @return
     *     possible object is
     *     {@link Forum.XmlfpUrls }
     *     
     */
    public Forum.XmlfpUrls getXmlfpUrls() {
        return xmlfpUrls;
    }

    /**
     * Sets the value of the xmlfpUrls property.
     * 
     * @param value
     *     allowed object is
     *     {@link Forum.XmlfpUrls }
     *     
     */
    public void setXmlfpUrls(Forum.XmlfpUrls value) {
        this.xmlfpUrls = value;
    }

    /**
     * Gets the value of the forumUrls property.
     * 
     * @return
     *     possible object is
     *     {@link Forum.ForumUrls }
     *     
     */
    public Forum.ForumUrls getForumUrls() {
        return forumUrls;
    }

    /**
     * Sets the value of the forumUrls property.
     * 
     * @param value
     *     allowed object is
     *     {@link Forum.ForumUrls }
     *     
     */
    public void setForumUrls(Forum.ForumUrls value) {
        this.forumUrls = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="messageUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element name="userProfileUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class ForumUrls {

        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String messageUrl;
        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String userProfileUrl;

        /**
         * Gets the value of the messageUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMessageUrl() {
            return messageUrl;
        }

        /**
         * Sets the value of the messageUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMessageUrl(String value) {
            this.messageUrl = value;
        }

        /**
         * Gets the value of the userProfileUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUserProfileUrl() {
            return userProfileUrl;
        }

        /**
         * Sets the value of the userProfileUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUserProfileUrl(String value) {
            this.userProfileUrl = value;
        }

    }


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
     *         &lt;element name="lastMessageNumberUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element ref="{}messageUrl"/>
     *         &lt;element ref="{}messageListUrl" minOccurs="0"/>
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
        "lastMessageNumberUrl",
        "messageUrl",
        "messageListUrl"
    })
    public static class XmlfpUrls {

        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String lastMessageNumberUrl;
        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String messageUrl;
        protected MessageListUrl messageListUrl;

        /**
         * Gets the value of the lastMessageNumberUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastMessageNumberUrl() {
            return lastMessageNumberUrl;
        }

        /**
         * Sets the value of the lastMessageNumberUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastMessageNumberUrl(String value) {
            this.lastMessageNumberUrl = value;
        }

        /**
         * Gets the value of the messageUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMessageUrl() {
            return messageUrl;
        }

        /**
         * Sets the value of the messageUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMessageUrl(String value) {
            this.messageUrl = value;
        }

        /**
         * Gets the value of the messageListUrl property.
         * 
         * @return
         *     possible object is
         *     {@link MessageListUrl }
         *     
         */
        public MessageListUrl getMessageListUrl() {
            return messageListUrl;
        }

        /**
         * Sets the value of the messageListUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link MessageListUrl }
         *     
         */
        public void setMessageListUrl(MessageListUrl value) {
            this.messageListUrl = value;
        }

    }

}
