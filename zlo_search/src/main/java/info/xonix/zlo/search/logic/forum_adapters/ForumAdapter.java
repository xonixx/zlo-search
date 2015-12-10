package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.domain.Message;

import java.util.List;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public interface ForumAdapter {
    long getLastMessageNumber(String forumId) throws ForumAccessException;

    Message getMessage(String forumId, long messageId) throws ForumAccessException;

    /**
     *
     * @param forumId           forumId
     * @param from              (including)
     * @param to                (excluding)
     * @return list of messages
     * @throws ForumAccessException
     */
    List<Message> getMessages(String forumId, long from, long to) throws ForumAccessException;

    String prepareMessageUrl(long messageId);

    String prepareUserProfileUrl(String userId, String userName);

    String getForumUrl();

    String getForumHost();

    String getForumTitle();

    /**
     * This function is necessary for implementing saved msg view when msg forum url entered to search
     *
     * @param messageUrl messageUrl
     * @return message id or -1 if in can't be exctracted
     */
    long extractMessageIdFromMessageUrl(String messageUrl);

    boolean supportsParents();
}
