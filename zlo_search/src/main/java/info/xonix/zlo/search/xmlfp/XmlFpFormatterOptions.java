package info.xonix.zlo.search.xmlfp;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 19:07:32
 */
public class XmlFpFormatterOptions {
    public XmlFpFormatterOptions(final String marshallersEncoding, final boolean prettyPrint) {
        try {
            for (Marshaller mar : JaxbMarshallers.all()) {
                mar.setProperty("jaxb.encoding", marshallersEncoding);/* TODO: is this even necessary? */
                mar.setProperty("jaxb.formatted.output", prettyPrint); // pretty-print
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
