package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.utils.MarshalUtils;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;
import info.xonix.zlo.search.xmlfp.xjccompiled.lastMessageNumber.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 19:07:32
 */
public class XmlFp {
    private Marshaller messageMarshaller;
    private Marshaller lastMsgNumMarshaller;

    public XmlFp(final String marshallersEncoding, final boolean prettyPrint) {
        try {
            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.xjccompiled.message");
            messageMarshaller = jaxbContext.createMarshaller();

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.xjccompiled.lastMessageNumber");
            lastMsgNumMarshaller = jaxbContext.createMarshaller();

            for (Marshaller mar : new Marshaller[]{messageMarshaller, lastMsgNumMarshaller}) {
                mar.setProperty("jaxb.encoding", marshallersEncoding);
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

    public String lastMessageNumberToXml(int num) {
        try {
            return MarshalUtils.marshal(lastMsgNumMarshaller,
                    new ObjectFactory().createLastMessageNumber((long) num));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }
}
