package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.jaxb_generated.lastMessageNumber.ObjectFactory;
import info.xonix.zlo.search.xmlfp.utils.MarshalUtils;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;

import javax.xml.bind.JAXBElement;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:59
 */
public class XmlFpUtils {
    public static String messageToXml(Message message) {
        try {
            return MarshalUtils.marshal(JaxbMarshallers.MESSAGE_MARSHALLER, Convert.toJaxbMessage(message));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message messageFromXml(String xml) throws XmlFpException {
        try {
            return Convert.fromJaxbMessage(
                    MarshalUtils.<info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message>unmarshal(
                            JaxbUnmarshallers.MESSAGE_UNMARSHALLER, xml));
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static String lastMessageNumberToXml(int num) {
        try {
            return MarshalUtils.marshal(JaxbMarshallers.LAST_MSG_NUM_MARSHALLER,
                    new ObjectFactory().createLastMessageNumber((long) num));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static long lastMessageNumberFromXml(String xml) throws XmlFpException {
        try {
            final JAXBElement<Long> res = MarshalUtils.unmarshal(JaxbUnmarshallers.LAST_MSG_NUM_UNMARSHALLER, xml);
            return res.getValue();
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }
}
