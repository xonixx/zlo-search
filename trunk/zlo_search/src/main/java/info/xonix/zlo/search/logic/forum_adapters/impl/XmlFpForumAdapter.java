package info.xonix.zlo.search.logic.forum_adapters.impl;

import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapterAbstract;
import info.xonix.zlo.search.logic.forum_adapters.ForumFormatException;
import info.xonix.zlo.search.logic.forum_adapters.ForumIoException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
import info.xonix.zlo.search.xmlfp.XmlFpException;
import info.xonix.zlo.search.xmlfp.XmlFpIOException;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public class XmlFpForumAdapter extends ForumAdapterAbstract {
    private ForumAccessor forumAccessor;

    public XmlFpForumAdapter(String descriptorUrl) throws ForumAccessException {
        try {
            forumAccessor = new ForumAccessor(descriptorUrl);
        } catch (XmlFpException e) {
            throw translateException(e, "Can't create XMLFP ForumAccessor for descriptorUrl: " + descriptorUrl);
        }
    }

    private ForumAccessException translateException(XmlFpException ex, String msg) {
        if (ex instanceof XmlFpIOException) {
            return new ForumIoException(msg, ex);
        } else {
            return new ForumFormatException(msg, ex);
        }
    }

    @Override
    public long getLastMessageNumber() throws ForumAccessException {
        try {
            return forumAccessor.getLastMessageNumber();
        } catch (XmlFpException e) {
            throw translateException(e, "Can't get last number");
        }
    }

    @Override
    public Message getMessage(long messageId) throws ForumAccessException {
        try {
            return forumAccessor.getMessage(messageId);
        } catch (XmlFpException e) {
            throw translateException(e, "Can't get message #" + messageId);
        }
    }

    @Override
    public String prepareMessageUrl(long messageId) {
        return forumAccessor.getForumMessageUrl(messageId);
    }

    @Override
    public String prepareUserProfileUrl(long userId, String userName) {
        return forumAccessor.getForumUserProfileUrl(userId, userName);
    }

    @Override
    public String getForumUrl() {
        return forumAccessor.getForumUrl();
    }
}
