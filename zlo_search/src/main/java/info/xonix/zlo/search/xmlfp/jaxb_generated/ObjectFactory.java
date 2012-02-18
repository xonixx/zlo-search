
package info.xonix.zlo.search.xmlfp.jaxb_generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the info.xonix.zlo.search.xmlfp.jaxb_generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LastMessageNumber_QNAME = new QName("", "lastMessageNumber");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: info.xonix.zlo.search.xmlfp.jaxb_generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Content }
     * 
     */
    public Content createContent() {
        return new Content();
    }

    /**
     * Create an instance of {@link Forum }
     * 
     */
    public Forum createForum() {
        return new Forum();
    }

    /**
     * Create an instance of {@link Content.Tags }
     * 
     */
    public Content.Tags createContentTags() {
        return new Content.Tags();
    }

    /**
     * Create an instance of {@link Message }
     * 
     */
    public Message createMessage() {
        return new Message();
    }

    /**
     * Create an instance of {@link Info }
     * 
     */
    public Info createInfo() {
        return new Info();
    }

    /**
     * Create an instance of {@link Author }
     * 
     */
    public Author createAuthor() {
        return new Author();
    }

    /**
     * Create an instance of {@link Forum.Xmlfp }
     * 
     */
    public Forum.Xmlfp createForumXmlfp() {
        return new Forum.Xmlfp();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "lastMessageNumber")
    public JAXBElement<Long> createLastMessageNumber(Long value) {
        return new JAXBElement<Long>(_LastMessageNumber_QNAME, Long.class, null, value);
    }

}
