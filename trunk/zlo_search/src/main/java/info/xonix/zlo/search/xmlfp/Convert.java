package info.xonix.zlo.search.xmlfp;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.xmlfp.jaxb_generated.*;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * User: gubarkov
 * Date: 11.02.12
 * Time: 22:27
 */
class Convert {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static Message fromJaxbMessage(info.xonix.zlo.search.xmlfp.jaxb_generated.Message jaxbMessage) {
        final MessageStatus messageStatus = messageStatusFromString(jaxbMessage.getStatus());

        if (messageStatus == MessageStatus.OK) {
            final Author author = jaxbMessage.getAuthor();
            final Content content = jaxbMessage.getContent();
            final Info info = jaxbMessage.getInfo();

            return new Message(
                    null, // must set after!
                    author.getName(),
                    null, // TODO: implement altname in xmlfp ?
                    author.getHost(),

                    content.getCategory(),
                    -1,

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

    public static Messages toJaxbMessages(List<Message> messages) {
        Messages jaxbMessages = OBJECT_FACTORY.createMessages();

        for (Message message : messages) {
            jaxbMessages.getMessage().add(toJaxbMessage(message));
        }

        return jaxbMessages;
    }

    public static info.xonix.zlo.search.xmlfp.jaxb_generated.Message toJaxbMessage(Message message) {
        if (message == null) { // TODO: ?
            message = new Message();
        }

        Site site = message.getSite();
        info.xonix.zlo.search.xmlfp.jaxb_generated.Message jaxbMessage = new info.xonix.zlo.search.xmlfp.jaxb_generated.Message();

        final MessageStatus messageStatus = message.getStatus();
        jaxbMessage.setStatus(messageStatusToString(messageStatus));

        if (messageStatus == MessageStatus.OK) {

            jaxbMessage.setStatus(null); // null = OK

            final Content content = OBJECT_FACTORY.createContent();
            jaxbMessage.setContent(content);

            content.setTitle(message.getTitle());
            content.setBody(message.getBody());

            content.setCategory(message.getTopic());

            final Info info = OBJECT_FACTORY.createInfo();
            jaxbMessage.setInfo(info);

            GregorianCalendar cal = new GregorianCalendar();
            // TODO: normalize TZ
//            GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            // TODO: note: msg datetime MUST be UTC(GMT) based
            cal.setTime(message.getDate());

            info.setDate(new XMLGregorianCalendarImpl(cal));
            info.setParentId((long) message.getParentNum());
            info.setId((long) message.getNum());
            info.setMessageUrl("http://" + message.getSite().getSiteUrl() + site.getReadQuery() + message.getNum());


            final Author author = OBJECT_FACTORY.createAuthor();
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
        return messageStatus != null ? messageStatus : MessageStatus.OK; // omitted status = OK
    }

    public static Forum toJaxbForum(Site site) {
        Forum forum = OBJECT_FACTORY.createForum();

        forum.setName(site.getSiteDescription());
        forum.setUrl("http://" + site.getSiteUrl() + "/");
        forum.setType("tree");
        forum.setCharset(site.getSiteCharset());

        final Forum.XmlfpUrls xmlFpInfo = OBJECT_FACTORY.createForumXmlfpUrls();
        xmlFpInfo.setLastMessageNumberUrl("xmlfp.jsp?xmlfp=lastMessageNumber&site=" + site.getSiteNumber());
        xmlFpInfo.setMessageUrl("xmlfp.jsp?xmlfp=message&num=" + XmlFpUrlsSubstitutions.MESSAGE_ID + "&site=" + site.getSiteNumber());
        forum.setXmlfpUrls(xmlFpInfo);

        final Forum.ForumUrls forumUrls = OBJECT_FACTORY.createForumForumUrls();
        forumUrls.setMessageUrl("http://" + site.getSiteUrl() + site.getReadQuery() + XmlFpUrlsSubstitutions.MESSAGE_ID);
        forumUrls.setUserProfileUrl("http://" + site.getSiteUrl() + site.getUinfoQuery() + XmlFpUrlsSubstitutions.USER_NAME);
        forum.setForumUrls(forumUrls);

        return forum;
    }
}
