package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static info.xonix.zlo.search.xmlfp.JaxbContexts.JAXB_CONTEXT;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:43
 */
class JaxbUnmarshallers {
    static final Unmarshaller UNMARSHALLER_XMLFP;

    static {
        try {
             UNMARSHALLER_XMLFP = JAXB_CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating unmarshallers", e);
        }
    }
}
