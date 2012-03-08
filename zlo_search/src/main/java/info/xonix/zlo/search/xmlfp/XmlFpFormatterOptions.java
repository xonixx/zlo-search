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
            final Marshaller marshaller = XmlFpContext.getMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, marshallersEncoding);/* TODO: is this even necessary? */
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint); // pretty-print
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
