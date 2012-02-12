package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:42
 */
class JaxbContexts {
    static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated");
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating JAXB contexts", e);
        }
    }
}
