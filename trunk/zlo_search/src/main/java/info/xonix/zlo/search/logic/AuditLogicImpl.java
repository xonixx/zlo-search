package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dao.AuditDao;
import info.xonix.zlo.search.model.SearchLog;
import info.xonix.zlo.search.utils.Check;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Vovan
 * Date: 05.07.2010
 * Time: 0:35:08
 */
public class AuditLogicImpl implements AuditLogic, InitializingBean {
    @Autowired
    private AuditDao auditDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(auditDao, "auditDao");
    }

    @Override
    public void logSearchEvent(SearchLog searchLog) {
        auditDao.saveSearchRequest(
                searchLog.getSite().getSiteNumber(),
                searchLog);

    }
}
