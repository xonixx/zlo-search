package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.Topic;

import java.util.HashMap;
import java.util.List;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface DbManager {
    void saveMessagesFast(Site site, List<Message> msgs);

    void saveMessagesFast(Site site, List<Message> msgs, boolean updateIfExists);

    Message getMessageByNumber(Site site, int num);

    List<Message> getMessagesByRange(Site site, int start, int end);

    List<Message> getMessages(Site site, int[] nums, int fromIndex);

    int getLastMessageNumber(Site site);

    // TODO: introduce right dmo

    void saveSearchRequest(int siteNum, String host, String userAgent,
                           String reqText, String reqNick, String reqHost,
                           String reqQuery, String reqQueryString, String referer, boolean rssAsked);

    List<Topic> getTopicList(Site site);

    String[] getTopics(Site site);

    HashMap<String, Integer> getTopicsHashMap(Site site);
}
