package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.xmlfp.utils.jaxb.SimpleResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 15:45
 */
class XmlFpContext {
    public static final String XSD_PATH = "info/xonix/zlo/search/xmlfp/xsd/";

    public static JAXBContext getJaxbContext() {
        return JaxbContextHolder.JAXB_CONTEXT;
    }

    public static Marshaller getMarshaller() {
//        return JaxbMarshallerHolder.MARSHALLER_XMLFP;
        return getValidatingMarshaller();
    }

    public static Unmarshaller getUnmarshaller() {
//        return JaxbUnmarshallerHolder.UNMARSHALLER_XMLFP;
        return getValidatingUnmarshaller();
    }

    private static Marshaller getValidatingMarshaller() {
        return ValidatingMarshallerHolder.MARSHALLER;
    }

    private static Unmarshaller getValidatingUnmarshaller() {
        return ValidatingUnmarshallerHolder.UNMARSHALLER;
    }

    private static Marshaller newValidatingMarshaller() {
        final Marshaller marshaller = newMarshaller();
        marshaller.setSchema(getAllSchemasSchema());
        return marshaller;
    }

    private static Unmarshaller newValidatingUnmarshaller() {
        final Unmarshaller unmarshaller = newUnmarshaller();
        unmarshaller.setSchema(getAllSchemasSchema());
        return unmarshaller;
    }

    private static SchemaFactory schemaFactory = SchemaFactory.newInstance(
            javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private static Schema getAllSchemasSchema() {
        try {
            final Source[] xsdSources = getXsdSources();

            schemaFactory.setResourceResolver(new SimpleResolver(XSD_PATH));

            return schemaFactory.newSchema(xsdSources);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static Source[] getXsdSources() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            final Resource[] resources;
//            resources = resolver.getResources("info/xonix/zlo/search/xmlfp/xsd/*.xsd");
//            resources = resolver.getResources(XSD_PATH + "mess*.xsd");
            resources = resolver.getResources(XSD_PATH + "message.xsd");
//            resources = resolver.getResources(XSD_PATH + "*.xsd");

            final Source[] sources = new Source[resources.length];

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
//                sources[i] = new StreamSource(resource.getFile());
                sources[i] = new StreamSource(resource.getInputStream());
            }

            return sources;
        } catch (IOException e) {
            throw new RuntimeException("getXsdSources", e);
        }
    }


/*    private static StringFactory<Marshaller> marshallersCache = new StringFactory<Marshaller>() {
    @Override
    protected Marshaller create(String xsdPath) {
        return null;
    }
};

private static StringFactory<Unmarshaller> unmarshallersCache = new StringFactory<Unmarshaller>() {
    @Override
    protected Unmarshaller create(String xsdPath) {
        return null;
    }
};

public static Marshaller getMarshaller(String xsdPath) {
    return marshallersCache.get(xsdPath);
}

public static Unmarshaller getUnmarshaller(String xsdPath) {
    return unmarshallersCache.get(xsdPath);
}*/

    private static class JaxbContextHolder {
        static final JAXBContext JAXB_CONTEXT;

        static {
            try {
                JAXB_CONTEXT = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated");
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXB contexts", e);
            }
        }
    }

    private static class JaxbMarshallerHolder {
        static final Marshaller MARSHALLER_XMLFP = newMarshaller();

        static List<Marshaller> all() {
            return Collections.unmodifiableList(Arrays.asList(
                    MARSHALLER_XMLFP
            ));
        }
    }

    private static class JaxbUnmarshallerHolder {
        static final Unmarshaller UNMARSHALLER_XMLFP = newUnmarshaller();
    }

    private static class ValidatingMarshallerHolder {
        static final Marshaller MARSHALLER = newValidatingMarshaller();
    }

    private static class ValidatingUnmarshallerHolder {
        static final Unmarshaller UNMARSHALLER = newValidatingUnmarshaller();
    }

    private static Marshaller newMarshaller() {
        try {
            return JaxbContextHolder.JAXB_CONTEXT.createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating JAXB marshallers");
        }
    }

    private static Unmarshaller newUnmarshaller() {
        try {
            return JaxbContextHolder.JAXB_CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating unmarshallers", e);
        }
    }
}
