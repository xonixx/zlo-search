package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dao.DbManager;
import info.xonix.zlo.search.model.SearchLogEvent;
import info.xonix.zlo.search.utils.Check;
import org.springframework.beans.factory.InitializingBean;

import static org.apache.commons.lang.StringUtils.substring;

/**
 * User: Vovan
 * Date: 05.07.2010
 * Time: 0:35:08
 */
public class AuditLogicImpl implements AuditLogic, InitializingBean {
    private DbManager dbManager;

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(dbManager, "dbManager");
    }

    @Override
    public void logSearchEvent(SearchLogEvent searchLogEvent) {
        dbManager.saveSearchRequest(
                searchLogEvent.getSite().getSiteNumber(),
                substring(searchLogEvent.getClientIp(), 0, 100),
                substring(searchLogEvent.getUserAgent(), 0, 200),

                substring(searchLogEvent.getSearchText(), 0, 200),
                substring(searchLogEvent.getSearchNick(), 0, 100),
                substring(searchLogEvent.getSearchHost(), 0, 100),

                substring(searchLogEvent.getSearchQuery(), 0, 200),
                substring(searchLogEvent.getSearchQueryString(), 0, 400),
                substring(searchLogEvent.getReferer(), 0, 100),
                searchLogEvent.isRssAsked());

    }
}
