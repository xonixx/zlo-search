package info.xonix.zlo.search.xmlfp;

//import com.google.common.collect.BiMap;
//import com.google.common.collect.ImmutableBiMap;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfParams;
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

    public static final String STATUS_DELETED = "deleted";
    public static final String STATUS_NOT_EXISTS = "not_exists";

    public static Message fromJaxbMessage(info.xonix.zlo.search.xmlfp.jaxb_generated.Message jaxbMessage) {
        final MessageStatus messageStatus = messageStatusFromString(jaxbMessage.getStatus());

        if (messageStatus == MessageStatus.OK) {
            final Author author = jaxbMessage.getAuthor();
            final Content content = jaxbMessage.getContent();
            final Info info = jaxbMessage.getInfo();

            return new Message(
                    // must set after!
                    author.getName(),
                    null, // TODO: implement altname in xmlfp ?
                    author.getHost(),

                    content.getCategory(),
                    -1,

                    StringUtils.trim(content.getTitle()),
                    StringUtils.trim(content.getBody()),

                    new Date(info.getDate().toGregorianCalendar().getTime().getTime()),
                    author.isRegistered() == null ? false : author.isRegistered(),
                    (int) jaxbMessage.getId(),
                    (int) (info.getParentId() == null ? Message.NO_PARENT : info.getParentId()),

                    messageStatus.getInt()
            );
        } else {
            return Message.withStatus(messageStatus, (int) jaxbMessage.getId());
        }
    }

    public static Messages toJaxbMessages(WwwconfParams wwwconfParams, List<Message> messages) {
        Messages jaxbMessages = OBJECT_FACTORY.createMessages();

        for (Message message : messages) {
            jaxbMessages.getMessage().add(toJaxbMessage(wwwconfParams, message));
        }

        return jaxbMessages;
    }

    public static info.xonix.zlo.search.xmlfp.jaxb_generated.Message toJaxbMessage(
            WwwconfParams wwwconfParams, Message message) {

        if (message == null) {
            throw new NullPointerException("message");
        }

        info.xonix.zlo.search.xmlfp.jaxb_generated.Message jaxbMessage = new info.xonix.zlo.search.xmlfp.jaxb_generated.Message();

        jaxbMessage.setId((long) message.getNum());

        final MessageStatus messageStatus = message.getStatus();

        if (messageStatus != MessageStatus.OK) {
            jaxbMessage.setStatus(messageStatusToString(messageStatus));
        } else {
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
            info.setMessageUrl("http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getReadQuery() + message.getNum());


            final Author author = OBJECT_FACTORY.createAuthor();
            jaxbMessage.setAuthor(author);

            author.setName(message.getNick());
            author.setAltName(message.getAltName());
            author.setHost(message.getHost());
            author.setRegistered(message.isReg());
        }
        return jaxbMessage;
    }

/*    private static final BiMap<MessageStatus, String> MESSAGE_STATUS_TO_JAXB_STATUS = ImmutableBiMap.of(
            MessageStatus.OK, "ok",
            MessageStatus.DELETED, "deleted");*/

    private static String messageStatusToString(final MessageStatus messageStatus) {
        switch (messageStatus) {
//            case OK:
//                return "ok";
            case DELETED:
                return STATUS_DELETED;
            case NOT_EXISTS:
                return STATUS_NOT_EXISTS;
            default:
                throw new IllegalArgumentException("messageStatus:" + messageStatus);
        }

/*        final String jaxbStatus = MESSAGE_STATUS_TO_JAXB_STATUS.get(messageStatus);
        if (jaxbStatus == null) {
            throw new IllegalStateException("messageStatus:" + messageStatus);
        }
        return jaxbStatus;*/
    }

    private static MessageStatus messageStatusFromString(String jaxbStatus) {
        if (jaxbStatus == null) {// omitted status = OK
            return MessageStatus.OK;
        } else if (STATUS_DELETED.equals(jaxbStatus)) {
            return MessageStatus.DELETED;
        } else if (STATUS_NOT_EXISTS.equals(jaxbStatus)) {
            return MessageStatus.NOT_EXISTS;
        }

        throw new IllegalArgumentException("jaxbStatus: " + jaxbStatus);

/*
        final MessageStatus messageStatus = MESSAGE_STATUS_TO_JAXB_STATUS.inverse().get(jaxbStatus);
        return messageStatus != null ? messageStatus : MessageStatus.OK; // omitted status = OK
*/
    }

    public static Forum toJaxbForum(String forumId, WwwconfParams wwwconfParams) {
        Forum forum = OBJECT_FACTORY.createForum();

        forum.setName(wwwconfParams.getSiteDescription());
        forum.setUrl("http://" + wwwconfParams.getSiteUrl() + "/");
        forum.setType("tree");
        forum.setCharset(wwwconfParams.getSiteCharset());

        final Forum.XmlfpUrls xmlFpInfo = OBJECT_FACTORY.createForumXmlfpUrls();

        final int forumIntId = GetForum.descriptor(forumId).getForumIntId();

        xmlFpInfo.setLastMessageNumberUrl("xmlfp.jsp?xmlfp=lastMessageNumber&site=" + forumIntId);
        xmlFpInfo.setMessageUrl("xmlfp.jsp?xmlfp=message&num=" + XmlFpUrlsSubstitutions.MESSAGE_ID + "&site=" + forumIntId);

        final MessageListUrl messageListUrl = OBJECT_FACTORY.createMessageListUrl();
        messageListUrl.setValue("xmlfp.jsp?xmlfp=messages" +
                "&from=" + XmlFpUrlsSubstitutions.FROM +
                "&to=" + XmlFpUrlsSubstitutions.TO +
                "&site=" + forumIntId);
        messageListUrl.setMaxCount(XmlFpFormer.MAX_DELTA);
        xmlFpInfo.setMessageListUrl(messageListUrl);

        forum.setXmlfpUrls(xmlFpInfo);

        final Forum.ForumUrls forumUrls = OBJECT_FACTORY.createForumForumUrls();
        forumUrls.setMessageUrl("http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getReadQuery() + XmlFpUrlsSubstitutions.MESSAGE_ID);
        forumUrls.setUserProfileUrl("http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getUinfoQuery() + XmlFpUrlsSubstitutions.USER_NAME);
        forum.setForumUrls(forumUrls);

        return forum;
    }
}
