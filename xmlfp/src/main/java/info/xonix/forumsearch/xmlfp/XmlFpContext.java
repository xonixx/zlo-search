package info.xonix.forumsearch.xmlfp;

import info.xonix.forumsearch.xmlfp.utils.jaxb.SimpleResolver;
import info.xonix.utils.ConfigUtils;
import info.xonix.utils.factory.StringFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 15:45
 */
public class XmlFpContext {
    public static final String XSD_PATH = "info/xonix/zlo/search/xmlfp/xsd/";

    public static final String MESSAGE_XSD = "message.xsd";
    public static final String MESSAGES_XSD = "messages.xsd";
    public static final String DESCRIPTOR_XSD = "descriptor.xsd";
    public static final String LAST_MESSAGE_NUMBER_XSD = "lastMessageNumber.xsd";
    
    private static final List<Marshaller> MARSHALLERS = new LinkedList<Marshaller>();
    private static final List<Unmarshaller> UNMARSHALLERS = new LinkedList<Unmarshaller>();

    public static JAXBContext getJaxbContext() {
        return JaxbContextHolder.JAXB_CONTEXT;
    }

    public static Marshaller getPermissiveMarshaller() {
        return JaxbMarshallerHolder.MARSHALLER_XMLFP;
    }

    public static Unmarshaller getPermissiveUnmarshaller() {
        return JaxbUnmarshallerHolder.UNMARSHALLER_XMLFP;
    }

    public static Marshaller getMessageMarshaller() {
        return getValidatingMarshaller(MESSAGE_XSD);
    }

    public static Unmarshaller getMessageUnmarshaller() {
        return getValidatingUnmarshaller(MESSAGE_XSD);
    }

    public static Marshaller getMessagesMarshaller() {
        return getValidatingMarshaller(MESSAGES_XSD);
    }

    public static Unmarshaller getMessagesUnmarshaller() {
        return getValidatingUnmarshaller(MESSAGES_XSD);
    }

    public static Marshaller getDescriptorMarshaller() {
        return getValidatingMarshaller(DESCRIPTOR_XSD);
    }

    public static Unmarshaller getDescriptorUnmarshaller() {
        return getValidatingUnmarshaller(DESCRIPTOR_XSD);
    }

    public static Marshaller getLastMessageNumberMarshaller() {
        return getValidatingMarshaller(LAST_MESSAGE_NUMBER_XSD);
    }

    public static Unmarshaller getLastMessageNumberUnmarshaller() {
        return getValidatingUnmarshaller(LAST_MESSAGE_NUMBER_XSD);
    }

    private static SchemaFactory schemaFactory = SchemaFactory.newInstance(
            javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

    static {
        schemaFactory.setResourceResolver(new SimpleResolver(XSD_PATH));
    }

    private static StringFactory<Marshaller> marshallersCache = new StringFactory<Marshaller>() {
        @Override
        protected Marshaller create(String xsdPath) {
            final Schema schema = resolveSchema(xsdPath);
            final Marshaller marshaller = newMarshaller();
            marshaller.setSchema(schema);
            return marshaller;
        }
    };

    private static StringFactory<Unmarshaller> unmarshallersCache = new StringFactory<Unmarshaller>() {
        @Override
        protected Unmarshaller create(String xsdPath) {
            final Schema schema = resolveSchema(xsdPath);
            final Unmarshaller unmarshaller = newUnmarshaller();
            unmarshaller.setSchema(schema);
            return unmarshaller;
        }
    };

    private static Schema resolveSchema(String xsdPath) {
        InputStream inputStream = ConfigUtils.resolvePath(xsdPath);
        if (inputStream == null) {
            inputStream = ConfigUtils.resolvePath(XSD_PATH + xsdPath);
        }
        final Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(inputStream));
        } catch (SAXException e) {
            throw new RuntimeException("Unable to create schema", e);
        }
        return schema;
    }

    private static Marshaller getValidatingMarshaller(String xsdPath) {
        return marshallersCache.get(xsdPath);
    }

    private static Unmarshaller getValidatingUnmarshaller(String xsdPath) {
        return unmarshallersCache.get(xsdPath);
    }

    private static class JaxbContextHolder {
        static final JAXBContext JAXB_CONTEXT;

        static {
            try {
                JAXB_CONTEXT = JAXBContext.newInstance("info.xonix.forumsearch.xmlfp.jaxb_generated");
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXB contexts", e);
            }
        }
    }

    private static class JaxbMarshallerHolder {
        static final Marshaller MARSHALLER_XMLFP = newMarshaller();
    }

    private static class JaxbUnmarshallerHolder {
        static final Unmarshaller UNMARSHALLER_XMLFP = newUnmarshaller();
    }

    private static Marshaller newMarshaller() {
        try {
            final Marshaller marshaller = JaxbContextHolder.JAXB_CONTEXT.createMarshaller();
            MARSHALLERS.add(marshaller);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating JAXB marshallers");
        }
    }

    private static Unmarshaller newUnmarshaller() {
        try {
            final Unmarshaller unmarshaller = JaxbContextHolder.JAXB_CONTEXT.createUnmarshaller();
            UNMARSHALLERS.add(unmarshaller);
            return unmarshaller;
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating unmarshallers", e);
        }
    }

    public static List<Marshaller> allMarshallers() {
        return Collections.unmodifiableList(MARSHALLERS);
    }
}
