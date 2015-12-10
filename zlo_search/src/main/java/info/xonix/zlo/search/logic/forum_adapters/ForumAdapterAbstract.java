package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.domain.Message;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 2:14
 */
public abstract class ForumAdapterAbstract implements ForumAdapter {
    private final static Logger log = Logger.getLogger(ForumAdapterAbstract.class);

    private int retryCount = 3;

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * @param forumId forumId
     * @param from    (including)
     * @param to      (excluding)
     * @return list of received messages
     * @throws ForumAccessException if can't get msg from site
     */
    @Override
    public List<Message> getMessages(String forumId, long from, long to) throws ForumAccessException {
        if (to < from) {
            throw new IllegalArgumentException("to (" + to + ") < from (" + from + ")");
        }

        List<Message> messages = new ArrayList<Message>((int) (to - from));

        for (long messageId = from; messageId < to; messageId++) {
            long t1 = System.currentTimeMillis();

            messages.add(getMessageWithRetrying(forumId, messageId));

            long delta = System.currentTimeMillis() - t1;
            long toSleep = 1000 / GetForum.params(forumId).getIndexerLimitPerSecond() - delta;
            if (toSleep > 0) {
                try {
                    Thread.sleep(toSleep);
                } catch (InterruptedException e) {
                    log.warn("This should not happen!", e);
                }
            }
        }

        return messages;
    }

    public Message getMessageWithRetrying(String forumId, long messageId) throws ForumAccessException {
        for (int i = 0; i < retryCount; i++) {
            try {
                return getMessage(forumId, messageId);
            } catch (ForumAccessException e) {
                if (i == (retryCount - 1)) { // last iteration
                    throw e;
                }

                log.warn("Problem getting message #" + messageId + " for " + forumId + ". Retrying...");
            }
        }

        throw new IllegalStateException("We should not get here...");
    }

    @Override
    public long extractMessageIdFromMessageUrl(String messageUrl) {
        return -1;
    }

    @Override
    public String getForumHost() {
        try {
            return new URI(getForumUrl()).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't parse forum url", e);
        }
    }
}
