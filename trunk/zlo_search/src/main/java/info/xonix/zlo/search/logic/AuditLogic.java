package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.model.SearchLog;

/**
 * User: Vovan
 * Date: 05.07.2010
 * Time: 0:34:58
 */
public interface AuditLogic {
    void logSearchEvent(SearchLog searchLog);
}
