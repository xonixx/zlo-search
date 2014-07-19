package info.xonix.zlo.search.dao;

import info.xonix.utils.ConfigUtils;
import info.xonix.zlo.search.model.SearchLog;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.substring;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:55:48
 */
public class AuditDaoImpl extends DaoImplBase
        implements AuditDao {

    private static Properties props = ConfigUtils.loadProperties("info/xonix/zlo/search/db/sql.properties");
    private final String SQL_LOG_REQUEST = props.getProperty("sql.log.request");
    private final String SQL_LOG_EXCEPTION = props.getProperty("sql.log.exception");

    @Override
    public void saveSearchRequest(int siteNum, SearchLog searchLog) {
        getSimpleJdbcTemplate().update(SQL_LOG_REQUEST,
                siteNum,
                substring(searchLog.getClientIp(), 0, 100),
                substring(searchLog.getUserAgent(), 0, 200),

                substring(searchLog.getSearchText(), 0, 200),
                substring(searchLog.getSearchNick(), 0, 100),
                substring(searchLog.getSearchHost(), 0, 100),

                substring(searchLog.getSearchQuery(), 0, 200),
                substring(searchLog.getSearchQueryString(), 0, 400),
                substring(searchLog.getReferer(), 0, 100),
                searchLog.isRssAsked(),
                searchLog.isAdminRequest());
    }

    @Override
    public void storeException(String exception, String stackTrace, String msg, String source, String category) {
//        System.out.println("Storing exception: " + exception + " source=" + source + " category=" + category);
        getSimpleJdbcTemplate().update(SQL_LOG_EXCEPTION,
                exception,
                stackTrace,
                msg,
                source,
                category,
                new Timestamp(new Date().getTime()));
    }
}
