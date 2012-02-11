package info.xonix.zlo.search.xmlfp;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.xmlfp.jaxb_generated.message.Author;
import info.xonix.zlo.search.xmlfp.jaxb_generated.message.Content;
import info.xonix.zlo.search.xmlfp.jaxb_generated.message.Info;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: gubarkov
 * Date: 11.02.12
 * Time: 22:27
 */
class Convert {
    public static Message fromJaxbMessage(info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message jaxbMessage) {
        final MessageStatus messageStatus = messageStatusFromString(jaxbMessage.getStatus());

        if (messageStatus == MessageStatus.OK) {
            final Author author = jaxbMessage.getAuthor();
            final Content content = jaxbMessage.getContent();
            final Content.Category category = content.getCategory();
            final Info info = jaxbMessage.getInfo();

            return new Message(
                    null, // must set after!
                    author.getName(),
                    null, // TODO: implement altname in xmlfp ?
                    author.getHost(),

                    category.getValue(),
                    category.getId(),

                    StringUtils.trim(content.getTitle()),
                    StringUtils.trim(content.getBody()),

                    new Date(info.getDate().toGregorianCalendar().getTime().getTime()),
                    author.isRegistered() == null ? false : author.isRegistered(),
                    (int) info.getId(),
                    (int) (info.getParentId() == null ? Message.NO_PARENT : info.getParentId()),

                    messageStatus.getInt()
            );
        } else {
            return Message.withStatus(messageStatus);
        }
    }

    public static info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message toJaxbMessage(Message message) {
        if (message == null) { // TODO: ?
            message = new Message();
        }

        Site site = message.getSite();
        info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message jaxbMessage = new info.xonix.zlo.search.xmlfp.jaxb_generated.message.Message();

        jaxbMessage.setStatus(messageStatusToString(message.getStatus()));

        if ("ok".equals(jaxbMessage.getStatus())) {

            final Content content = new Content();
            jaxbMessage.setContent(content);

            content.setTitle(message.getTitle());
            content.setBody(message.getBody());


            final Content.Category category = new Content.Category();
            content.setCategory(category);

            category.setValue(message.getTopic());
            category.setId(message.getTopicCode());


            final Info info = new Info();
            jaxbMessage.setInfo(info);

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(message.getDate());

            info.setDate(new XMLGregorianCalendarImpl(cal));
            info.setParentId((long) message.getParentNum());
            info.setId((long) message.getNum());
            info.setMessageUrl("http://" + message.getSite().getSiteUrl() + site.getReadQuery() + message.getNum());


            final Author author = new Author();
            jaxbMessage.setAuthor(author);

            author.setName(message.getNick());
            author.setHost(message.getHost());
            author.setRegistered(message.isReg());
        }

        return jaxbMessage;
    }

    private static final String MESSAGE_STATUS_UNKNOWN = "unknown";

    private static final BiMap<MessageStatus, String> MESSAGE_STATUS_TO_JAXB_STATUS = ImmutableBiMap.of(
            MessageStatus.OK, "ok",
            MessageStatus.DELETED, "deleted",
            MessageStatus.SPAM, "spam",
            MessageStatus.UNKNOWN, MESSAGE_STATUS_UNKNOWN);

    private static String messageStatusToString(final MessageStatus messageStatus) {
        final String jaxbStatus = MESSAGE_STATUS_TO_JAXB_STATUS.get(messageStatus);
        return jaxbStatus != null ? jaxbStatus : MESSAGE_STATUS_UNKNOWN;
    }

    private static MessageStatus messageStatusFromString(String jaxbStatus) {
        final MessageStatus messageStatus = MESSAGE_STATUS_TO_JAXB_STATUS.inverse().get(jaxbStatus);
        return messageStatus != null ? messageStatus : MessageStatus.UNKNOWN;
    }
}
