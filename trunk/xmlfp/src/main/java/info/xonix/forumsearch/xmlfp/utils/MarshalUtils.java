package info.xonix.forumsearch.xmlfp.utils;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: gubarkov
 * Date: 11.02.12
 * Time: 22:04
 */
public class MarshalUtils {
    public static String marshal(Marshaller marshaller, Object o) throws XmlFpMarshalException {
        String res;
        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(o, sw);
            res = sw.toString();
        } catch (JAXBException e) {
            throw new XmlFpMarshalException(e, "Error marshalling");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(Unmarshaller unmarshaller, String s) throws XmlFpMarshalException {
        try {
            return (T) unmarshaller.unmarshal(new StringReader(s));
        } catch (JAXBException e) {
            throw new XmlFpMarshalException(e, "Error unmarshalling");
        }
    }
}
