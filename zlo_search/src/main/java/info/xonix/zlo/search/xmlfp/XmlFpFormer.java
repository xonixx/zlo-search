package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

/**
 * User: gubarkov
 * Date: 29.01.12
 * Time: 22:56
 */
public class XmlFpFormer {
    public static final int MAX_DELTA = 1000;
    @Autowired
    private AppLogic appLogic;

    public String getMessages(String forumId, int from, int to) {
        if (to - from > MAX_DELTA) {
            throw new IllegalArgumentException("You are trying to receive more then " +
                    MAX_DELTA + " messages!");
        }

        final List<Message> messages = appLogic.getMessages(forumId, from, to + 1); // to+1 because in xmlfp from & to included!

        return XmlFpUtils.messagesToXml(forumId, messages);
    }

    public String getMessage(String forumId, int num) {
        Message m;
        try {
            m = appLogic.getMessageByNumber(forumId, num);
        } catch (EmptyResultDataAccessException e) {
            m = Message.withStatus(MessageStatus.DELETED, num);// TODO: should mean NOT EXISTS
        }

        return XmlFpUtils.messageToXml(forumId, m);
    }

    public String lastMessageNumber(String forumId) {
        return XmlFpUtils.lastMessageNumberToXml(
                appLogic.getLastSavedMessageNumber(forumId));
    }

    public String siteXmlFpDescriptor(String forumId) {
        return XmlFpUtils.siteDescriptorToXml(forumId);
    }
}
