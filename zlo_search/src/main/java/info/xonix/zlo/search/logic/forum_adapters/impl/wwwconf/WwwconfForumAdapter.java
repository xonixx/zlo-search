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

/*
    private String forumId() {
        return wwwconfParams.getForumId();
    }
*/

    public WwwconfParams getWwwconfParams() {
        return wwwconfParams;
    }

    @Override
    public long getLastMessageNumber(String forumId) throws ForumAccessException {
        try {
            return messageRetriever.getLastMessageNumber(wwwconfParams);
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get last number from: " + forumId, e);
        }
    }

    @Override
    public Message getMessage(String forumId, long messageId) throws ForumAccessException {
        try {
            return messageRetriever.getMessage(forumId, wwwconfParams, (int) messageId, 0); // retries = 0 as we implement retries on ForumAdapter level
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get message #" + messageId + " from site: " + forumId, e);
        } catch (PageParseException e) {
            throw new ForumFormatException("Can't get message #" + messageId + " from site: " + forumId, e);
        }
    }

    @Override
    public String prepareMessageUrl(long messageId) {
        return forumMsgUrlStart() + messageId;
    }

    private String forumMsgUrlStart() {
        return "http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getReadQuery();
    }

    @Override
    public String prepareUserProfileUrl(long userId, String userName) {
        return "http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getUinfoQuery() + userName; // TODO: correct encoding of userName
    }

    @Override
    public String getForumUrl() {
        final String siteUrl = wwwconfParams.getSiteUrl();
        return "http://" + siteUrl + (siteUrl.endsWith(".cgi") ? "" : "/");
    }

    @Override
    public String getForumTitle() {
        return wwwconfParams.getSiteDescription();
    }

    @Override
    public long extractMessageIdFromMessageUrl(String messageUrl) {
        final long defaultResult = super.extractMessageIdFromMessageUrl(messageUrl);

        if (messageUrl == null) {
            return defaultResult;
        }

        messageUrl = messageUrl.trim();

        final String msgUrlStart = forumMsgUrlStart();

        if (messageUrl.startsWith(msgUrlStart)) {
            try {
                return Long.parseLong(messageUrl.substring(msgUrlStart.length()));
            } catch (NumberFormatException e) {
                return defaultResult;
            }
        }

        return defaultResult;
    }
}
