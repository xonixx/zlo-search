package info.xonix.zlo.search.logic.forum_adapters;

import info.xonix.zlo.search.domainobj.Site;
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
    private Site site;

    @Autowired
    private MessageRetriever messageRetriever;

    public WwwconfForumAdapter(Site site) {
        this.site = site;
    }

    @Override
    public long getLastMessageNumber() throws ForumAccessException {
        try {
            return messageRetriever.getLastMessageNumber(site);
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get last number from: " + site.getName(), e);
        }
    }

    @Override
    public Message getMessage(long messageId) throws ForumAccessException {
        try {
            return messageRetriever.getMessage(site, (int) messageId, 0); // retries = 0 as we implement retries on ForumAdapter level
        } catch (RetrieverException e) {
            throw new ForumIoException("Can't get message #" + messageId + " from site: " + site.getName(), e);
        } catch (PageParseException e) {
            throw new ForumFormatException("Can't get message #" + messageId + " from site: " + site.getName(), e);
        }
    }

    @Override
    public String prepareMessageUrl(long messageId) {
        return "http://" + site.getSiteUrl() + site.getReadQuery() + messageId;
    }

    @Override
    public String prepareUserProfileUrl(long userId, String userName) {
        return "http://" + site.getSiteUrl() + site.getUinfoQuery() + userName; // TODO: correct encoding of userName
    }
}
