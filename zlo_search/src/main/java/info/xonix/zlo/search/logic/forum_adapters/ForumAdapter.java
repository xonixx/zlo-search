package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.model.Message;

import java.util.List;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public interface ForumAdapter {
    public long getLastMessageNumber(String forumId) throws ForumAccessException;

    public Message getMessage(String forumId, long messageId) throws ForumAccessException;

    /**
     * @param forumId
     * @param from (including)
     * @param to (excluding)
     * @return
     * @throws ForumAccessException
     */
    List<Message> getMessages(String forumId, long from, long to) throws ForumAccessException;

    public String prepareMessageUrl(long messageId);

    public String prepareUserProfileUrl(String userId, String userName);

    public String getForumUrl();

    public String getForumHost();

    public String getForumTitle();

    /**
     * This function is necessary for implementing saved msg view when msg forum url entered to search
     * @param messageUrl messageUrl
     * @return message id or -1 if in can't be exctracted
     */
    public long extractMessageIdFromMessageUrl(String messageUrl);

    public boolean supportsParents();
}
