package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.SearchLog;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:55:39
 */
public interface AuditDao {
    void saveSearchRequest(int siteNum, SearchLog searchLog);

    void storeException(String exception, String stackTrace, String msg, String source, String category);
}
