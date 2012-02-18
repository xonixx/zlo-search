package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 15:45
 */
class XmlFpContext {
    public static JAXBContext getJaxbContext() {
        return JaxbContextHolder.JAXB_CONTEXT;
    }

    public static Marshaller getMarshaller() {
        return JaxbMarshallerHolder.MARSHALLER_XMLFP;
    }

    public static Unmarshaller getUnmarshaller() {
        return JaxbUnmarshallerHolder.UNMARSHALLER_XMLFP;
    }

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
        static final Marshaller MARSHALLER_XMLFP;

        static {
            try {
                MARSHALLER_XMLFP = JaxbContextHolder.JAXB_CONTEXT.createMarshaller();
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXB marshallers");
            }

        }

        static List<Marshaller> all() {
            return Collections.unmodifiableList(Arrays.asList(
                    MARSHALLER_XMLFP
            ));
        }
    }

    private static class JaxbUnmarshallerHolder {
        static final Unmarshaller UNMARSHALLER_XMLFP;

        static {
            try {
                UNMARSHALLER_XMLFP = JaxbContextHolder.JAXB_CONTEXT.createUnmarshaller();
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating unmarshallers", e);
            }
        }
    }
}
