package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static info.xonix.zlo.search.xmlfp.JaxbContexts.LAST_MSG_NUM_JAXB_CONTEXT;
import static info.xonix.zlo.search.xmlfp.JaxbContexts.MESSAGE_JAXB_CONTEXT;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:43
 */
class JaxbUnmarshallers {
    static final Unmarshaller MESSAGE_UNMARSHALLER;
    static final Unmarshaller LAST_MSG_NUM_UNMARSHALLER;

    static {
        try {
             MESSAGE_UNMARSHALLER = MESSAGE_JAXB_CONTEXT.createUnmarshaller();
             LAST_MSG_NUM_UNMARSHALLER = LAST_MSG_NUM_JAXB_CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating unmarshallers", e);
        }
    }
}
