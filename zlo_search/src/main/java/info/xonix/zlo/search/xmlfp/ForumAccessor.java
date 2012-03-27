package info.xonix.zlo.search.xmlfp;

import info.xonix.forumsearch.xmlfp.XmlFpException;
import info.xonix.forumsearch.xmlfp.XmlFpForum;
import info.xonix.zlo.search.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 27.03.12
 * Time: 19:50
 */
public class ForumAccessor {
    private XmlFpForum xmlFpForum;

    public ForumAccessor(XmlFpForum xmlFpForum) {
        this.xmlFpForum = xmlFpForum;
    }

    public List<Message> getMessageList(long from, long to) throws XmlFpException {
        final List<info.xonix.forumsearch.xmlfp.jaxb_generated.Message> messageList = xmlFpForum.getMessageList(from, to);

        List<Message> resultList = new ArrayList<Message>(messageList.size());

        for (info.xonix.forumsearch.xmlfp.jaxb_generated.Message message : messageList) {
            resultList.add(Convert.fromJaxbMessage(message));
        }

        return resultList;
    }

    public Message getMessage(long messageId) throws XmlFpException {
        return Convert.fromJaxbMessage(xmlFpForum.getMessage(messageId));
    }
}
