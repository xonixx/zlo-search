package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.ZloMessage;

import java.util.List;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface DbManager {
    void saveMessagesFast(Site site, List<ZloMessage> msgs) throws DbException;

    void saveMessagesFast(Site site, List<ZloMessage> msgs, boolean updateIfExists) throws DbException;

    ZloMessage getMessageByNumber(Site site, int num) throws DbException;

    List<ZloMessage> getMessagesByRange(Site site, int start, int end) throws DbException;

    List<ZloMessage> getMessages(Site site, int[] nums, int fromIndex) throws DbException;

    int getLastMessageNumber(Site site) throws DbException;

    // TODO: introduce right dmo
    void logRequest(int siteNum, String host, String userAgent,
                           String reqText, String reqNick, String reqHost,
                           String reqQuery, String reqQueryString, String referer, boolean rssAsked) throws DbException;
}
