package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.model.Message;

import java.util.List;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public interface ForumAdapter {
    public long getLastMessageNumber() throws ForumAccessException;

    public Message getMessage(long messageId) throws ForumAccessException;

    List<Message> getMessages(long from, long to) throws ForumAccessException;

    public String prepareMessageUrl(long messageId);

    public String prepareUserProfileUrl(long userId, String userName);

    public String getForumUrl();
}
