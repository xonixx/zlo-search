package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.utils.MarshalUtils;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;
import info.xonix.zlo.search.xmlfp.jaxb_generated.lastMessageNumber.ObjectFactory;

import javax.xml.bind.*;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 19:07:32
 */
public class XmlFp {
    Marshaller messageMarshaller;
    Unmarshaller messageUnmarshaller;

    Marshaller lastMsgNumMarshaller;
    Unmarshaller lastMsgNumUnmarshaller;

    public XmlFp(final String marshallersEncoding, final boolean prettyPrint) {
        try {
            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated.message");
            messageMarshaller = jaxbContext.createMarshaller();
            messageUnmarshaller = jaxbContext.createUnmarshaller();

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated.lastMessageNumber");
            lastMsgNumMarshaller = jaxbContext.createMarshaller();
            lastMsgNumUnmarshaller = jaxbContext.createUnmarshaller();

            for (Marshaller mar : new Marshaller[]{messageMarshaller, lastMsgNumMarshaller}) {
                mar.setProperty("jaxb.encoding", marshallersEncoding);/* TODO: is this even necessary? */
                mar.setProperty("jaxb.formatted.output", prettyPrint); // pretty-print
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    public String messageToXml(Message message) {
        try {
            return MarshalUtils.marshal(messageMarshaller, Convert.toJaxbMessage(message));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public Message messageFromXml(String xml) throws XmlFpException {
        try {
            return Convert.fromJaxbMessage(
                    MarshalUtils.<info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message>unmarshal(
                            messageUnmarshaller, xml));
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public String lastMessageNumberToXml(int num) {
        try {
            return MarshalUtils.marshal(lastMsgNumMarshaller,
                    new ObjectFactory().createLastMessageNumber((long) num));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public long lastMessageNumberFromXml(String xml) throws XmlFpException {
        try {
            final JAXBElement<Long> res = MarshalUtils.unmarshal(lastMsgNumUnmarshaller, xml);
            return res.getValue();
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }
}
