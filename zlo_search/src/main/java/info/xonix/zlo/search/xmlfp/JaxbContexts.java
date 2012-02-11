package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:42
 */
public class JaxbContexts {
    static final JAXBContext MESSAGE_JAXB_CONTEXT;
    static final JAXBContext LAST_MSG_NUM_JAXB_CONTEXT;

    static {
        try {
            MESSAGE_JAXB_CONTEXT = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated.message");
            LAST_MSG_NUM_JAXB_CONTEXT = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated.lastMessageNumber");
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating JAXB contexts", e);
        }
    }
}
