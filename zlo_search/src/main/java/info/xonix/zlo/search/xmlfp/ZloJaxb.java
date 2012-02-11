package info.xonix.zlo.search.xmlfp;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.xmlfp.xjccompiled.lastMessageNumber.ObjectFactory;
import info.xonix.zlo.search.xmlfp.xjccompiled.message.Author;
import info.xonix.zlo.search.xmlfp.xjccompiled.message.Content;
import info.xonix.zlo.search.xmlfp.xjccompiled.message.Info;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.GregorianCalendar;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 19:07:32
 */
public class ZloJaxb {
    private Marshaller messageMarshaller;
    private Marshaller lastMsgNumMarshaller;

    public ZloJaxb(final String marshallersEncoding, final boolean prettyPrint) {
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


    public String zloMessageToXml(Message m) {
        if (m == null) {
            m = new Message();
        }
        Site site = m.getSite();
        info.xonix.zlo.search.xmlfp.xjccompiled.message.Message jaxbMessage = new info.xonix.zlo.search.xmlfp.xjccompiled.message.Message();

        jaxbMessage.setStatus(
                m.getStatus() == MessageStatus.DELETED ? "deleted"
                        : m.getStatus() == MessageStatus.SPAM ? "spam"
                        : m.getStatus() == null ? "notExists"
                        : m.getStatus() == MessageStatus.OK ? "ok" : "unknown"
        );

        if ("ok".equals(jaxbMessage.getStatus())) {
            Content content = new Content();
            jaxbMessage.setContent(content);
            content.setTitle(m.getTitle());
            content.setBody(m.getBody());

            final Content.Category category = new Content.Category();
            category.setValue(m.getTopic());
            category.setId(m.getTopicCode());

            content.setCategory(category);

            Info info = new Info();
            jaxbMessage.setInfo(info);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(m.getDate());
            info.setDate(new XMLGregorianCalendarImpl(cal));
            info.setParentId((long) m.getParentNum());
            info.setId((long) m.getNum());
            info.setMessageUrl("http://" + m.getSite().getSiteUrl() + site.getReadQuery() + m.getNum());

            Author author = new Author();
            jaxbMessage.setAuthor(author);
            author.setName(m.getNick());
            author.setHost(m.getHost());
            author.setRegistered(m.isReg());
        }

        return marshall(messageMarshaller, jaxbMessage);
    }

    public String lastMessageNumberToXml(int num) {
        return marshall(lastMsgNumMarshaller,
                new ObjectFactory().createLastMessageNumber((long) num));
    }

    private static String marshall(Marshaller m, Object o) {
        String res = null;
        try {
            StringWriter sw = new StringWriter();
            m.marshal(o, sw);
            res = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return res;
    }
}
