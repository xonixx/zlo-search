package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;

import java.util.List;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface DbManager {
    void saveMessagesFast(Site site, List<Message> msgs) throws DbException;

    void saveMessagesFast(Site site, List<Message> msgs, boolean updateIfExists) throws DbException;

    Message getMessageByNumber(Site site, int num) throws DbException;

    List<Message> getMessagesByRange(Site site, int start, int end) throws DbException;

    List<Message> getMessages(Site site, int[] nums, int fromIndex) throws DbException;

    int getLastMessageNumber(Site site) throws DbException;

    // TODO: introduce right dmo

    void logRequest(int siteNum, String host, String userAgent,
                    String reqText, String reqNick, String reqHost,
                    String reqQuery, String reqQueryString, String referer, boolean rssAsked) throws DbException;
}
