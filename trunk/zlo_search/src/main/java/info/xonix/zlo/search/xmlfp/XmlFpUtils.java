package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.jaxb_generated.ObjectFactory;
import info.xonix.zlo.search.xmlfp.utils.MarshalUtils;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:59
 */
public class XmlFpUtils {
    public static String messageToXml(String forumId, Message message) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMarshaller(),
                    Convert.toJaxbMessage(forumId, message));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static String messagesToXml(String forumId, List<Message> messages) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMarshaller(),
                    Convert.toJaxbMessages(forumId, messages));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message messageFromXml(String xml) throws XmlFpException {
        try {
            return Convert.fromJaxbMessage(
                    MarshalUtils.<info.xonix.zlo.search.xmlfp.jaxb_generated.Message>unmarshal(
                            XmlFpContext.getUnmarshaller(), xml));
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static String lastMessageNumberToXml(int num) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMarshaller(),
                    new ObjectFactory().createLastMessageNumber((long) num));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static long lastMessageNumberFromXml(String xml) throws XmlFpException {
        try {
            final JAXBElement<Long> res = MarshalUtils.unmarshal(XmlFpContext.getUnmarshaller(), xml);
            return res.getValue();
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static String siteDescriptorToXml(String forumId) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMarshaller(),
                    Convert.toJaxbForum(forumId));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }
}
