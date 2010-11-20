package info.xonix.zlo.search.logic.exceptions;

import info.xonix.zlo.search.dao.AuditDao;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.ExceptionUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:48:36
 */
public class ExceptionsLoggerImpl implements ExceptionsLogger, InitializingBean {
    private AuditDao auditDao;

    public void setAuditDao(AuditDao auditDao) {
        this.auditDao = auditDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(auditDao, "auditDao");
    }

    @Override
    public void logException(Throwable throwable, String msg, String source, ExceptionCategory category) {
        auditDao.storeException(
                throwable.toString(),
                ExceptionUtils.getStackTrace(throwable),
                msg != null ? msg : throwable.getLocalizedMessage(),
                source,
                category.toString());
    }

    @Override
    public void logException(Throwable throwable, String msg, Class sourceCls, ExceptionCategory category) {
        logException(throwable, msg, sourceCls.getCanonicalName(), category);
    }

    @Override
    public void logException(Throwable throwable, String msg, ExceptionCategory category) {
        logException(throwable, msg, (String) null, category);
    }

    @Override
    public void logException(Throwable throwable) {
        logException(throwable, null, (String) null, ExceptionCategory.UNKNOWN);
    }
}
