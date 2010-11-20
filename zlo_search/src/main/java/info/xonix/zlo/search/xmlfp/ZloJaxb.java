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
    private static Marshaller MESSAGE_MARSHALLER;
    private static Marshaller LAST_MSG_NUM_MARSHALLER;

    static {
        try {
            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.xjccompiled.message");
            MESSAGE_MARSHALLER = jaxbContext.createMarshaller();

            jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.xjccompiled.lastMessageNumber");
            LAST_MSG_NUM_MARSHALLER = jaxbContext.createMarshaller();

            for (Marshaller mar : new Marshaller[]{MESSAGE_MARSHALLER, LAST_MSG_NUM_MARSHALLER}) {
                mar.setProperty("jaxb.encoding", "windows-1251");
                mar.setProperty("jaxb.formatted.output", true); // pretty-print
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    public static String zloMessageToXml(Message m) {
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

            content.setCategory(new Content.Category());
            content.getCategory().setValue(m.getTopic());

            Info info = new Info();
            jaxbMessage.setInfo(info);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(m.getDate());
            info.setDate(new XMLGregorianCalendarImpl(cal));
            info.setParentId(BigInteger.valueOf(m.getParentNum()));
            info.setId(BigInteger.valueOf(m.getNum()));
            info.setMessageUrl("http://" + m.getSite().getSiteUrl() + site.getReadQuery() + m.getNum());

            Author author = new Author();
            jaxbMessage.setAuthor(author);
            author.setName(m.getNick());
            author.setHost(m.getHost());
            author.setRegistered(m.isReg());
        }

        return marshall(MESSAGE_MARSHALLER, jaxbMessage);
    }

    public static String lastNessageNumberToXml(int num) {
        return marshall(LAST_MSG_NUM_MARSHALLER,
                new ObjectFactory().createLastMessageNumber(BigInteger.valueOf(num)));
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
