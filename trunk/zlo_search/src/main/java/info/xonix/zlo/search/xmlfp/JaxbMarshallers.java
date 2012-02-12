package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static info.xonix.zlo.search.xmlfp.JaxbContexts.JAXB_CONTEXT;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:38
 */
class JaxbMarshallers {
    static final Marshaller MARSHALLER_XMLFP;

    static {
        try {
            MARSHALLER_XMLFP = JAXB_CONTEXT.createMarshaller();
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
