package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.model.Message;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public interface ForumAdapter {
    public long getLastMessageNumber() throws ForumAccessException;

    public Message getMessage(long messageId) throws ForumAccessException;
}
