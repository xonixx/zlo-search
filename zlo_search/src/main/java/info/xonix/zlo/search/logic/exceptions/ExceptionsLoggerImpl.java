package info.xonix.zlo.search.logic.exceptions;

import info.xonix.zlo.search.dao.AuditDao;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.annotation.Nullable;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:48:36
 */
public class ExceptionsLoggerImpl implements ExceptionsLogger, InitializingBean {
    private final static Logger log = Logger.getLogger(ExceptionsLoggerImpl.class);

    @Autowired
    private AuditDao auditDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(auditDao, "auditDao");
    }

    @Override
    public void logException(Throwable throwable, @Nullable String msg, String source, ExceptionCategory category) {
        try {
            auditDao.storeException(
                    throwable.toString(),
                    ExceptionUtils.getStackTrace(throwable),
                    msg != null ? msg : throwable.getLocalizedMessage(),
                    source,
                    category.toString());

        } catch (DataAccessException e) {
            log.error("Error occured while storing exception info:" + e +
                    "\n\tmsg=" + msg +
                    "\n\tsource=" + source +
                    "\n\tcategory=" + category +
                    "\n\tthrowable=" + throwable);
        }
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
