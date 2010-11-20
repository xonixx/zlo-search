package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.model.SearchLog;

import java.util.Properties;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:55:48
 */
public class AuditDaoImpl extends DaoImplBase
        implements AuditDao {

    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/sql.properties");
    private final String SQL_LOG_REQUEST = props.getProperty("sql.log.request");

    @Override
    public void saveSearchRequest(int siteNum, SearchLog searchLog) {
        getSimpleJdbcTemplate().update(SQL_LOG_REQUEST,
                siteNum,
                searchLog.getClientIp(),
                searchLog.getUserAgent(),

                searchLog.getSearchText(),
                searchLog.getSearchNick(),
                searchLog.getSearchHost(),

                searchLog.getSearchQuery(),
                searchLog.getSearchQueryString(),
                searchLog.getReferer(),
                searchLog.isRssAsked());
    }

    @Override
    public void storeException(String exception, String stackTrace, String msg, String source, String category) {
        System.out.println("Storing exception: " + exception + " source=" + source + " category=" + category);
        // TODO
    }
}
