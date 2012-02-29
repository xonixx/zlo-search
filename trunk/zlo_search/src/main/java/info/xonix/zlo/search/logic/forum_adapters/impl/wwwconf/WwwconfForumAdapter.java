package info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf;

import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapterAbstract;
import info.xonix.zlo.search.logic.forum_adapters.ForumFormatException;
import info.xonix.zlo.search.logic.forum_adapters.ForumIoException;
import info.xonix.zlo.search.logic.site.MessageRetriever;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.model.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:32
 */
public class WwwconfForumAdapter extends ForumAdapterAbstract {
    @Autowired
    private MessageRetriever messageRetriever;

    private WwwconfParams wwwconfParams;

    public WwwconfForumAdapter(WwwconfParams wwwconfParams) {
        this.wwwconfParams = wwwconfParams;
    }

    private String forumId() {
        return wwwconfParams.getForumId();
    }

    @Override
    public long getLastMessageNumber() throws ForumAccessException {
        try {
            return messageRetriever.getLastMessageNumber(forumId());
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get last number from: " + forumId(), e);
        }
    }

    @Override
    public Message getMessage(long messageId) throws ForumAccessException {
        try {
            return messageRetriever.getMessage(forumId(), (int) messageId, 0); // retries = 0 as we implement retries on ForumAdapter level
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get message #" + messageId + " from site: " + forumId(), e);
        } catch (PageParseException e) {
            throw new ForumFormatException("Can't get message #" + messageId + " from site: " + forumId(), e);
        }
    }

    @Override
    public String prepareMessageUrl(long messageId) {
        return "http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getReadQuery() + messageId;
    }

    @Override
    public String prepareUserProfileUrl(long userId, String userName) {
        return "http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getUinfoQuery() + userName; // TODO: correct encoding of userName
    }
}
