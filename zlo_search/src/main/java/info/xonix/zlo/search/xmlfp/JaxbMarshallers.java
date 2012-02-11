package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static info.xonix.zlo.search.xmlfp.JaxbContexts.LAST_MSG_NUM_JAXB_CONTEXT;
import static info.xonix.zlo.search.xmlfp.JaxbContexts.MESSAGE_JAXB_CONTEXT;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:38
 */
class JaxbMarshallers {
    static final Marshaller MESSAGE_MARSHALLER;
    static final Marshaller LAST_MSG_NUM_MARSHALLER;

    static {
        try {
            MESSAGE_MARSHALLER = MESSAGE_JAXB_CONTEXT.createMarshaller();
            LAST_MSG_NUM_MARSHALLER = LAST_MSG_NUM_JAXB_CONTEXT.createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating JAXB marshallers");
        }

    }

    static List<Marshaller> all() {
        return Collections.unmodifiableList(Arrays.asList(
                MESSAGE_MARSHALLER,
                LAST_MSG_NUM_MARSHALLER
        ));
    }
}
