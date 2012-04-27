package info.xonix.zlo.search.daemon_impl;

import info.xonix.zlo.search.config.forums.ForumParams;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.daemon.Daemon;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.ForumLogic;
import info.xonix.zlo.search.logic.exceptions.ExceptionCategory;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class);

    private AppLogic appLogic = AppSpringContext.get(AppLogic.class);
    private ForumLogic forumLogic = AppSpringContext.get(ForumLogic.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private class DbProcess extends Process {
        @Override
        protected int getFromIndex() {
            return appLogic.getLastSavedMessageNumber(getForumId());
        }

        @Override
        protected int getEndIndex() throws ForumAccessException {
            return forumLogic.getLastMessageNumber(getForumId());
        }

        @Override
        protected void perform(int from, int to) throws ForumAccessException {
            String forumId = getForumId();
            appLogic.saveMessages(forumId, forumLogic.getMessages(forumId, from, to + 1));
            appLogic.setLastSavedDate(forumId, new Date());
        }

        protected boolean processException(Exception e) {
            exceptionsLogger.logException(e,
                    "Exception in db daemon: " + getForumId(),
                    getClass(),
                    ExceptionCategory.DAEMON);

            return false;
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon() {
        super();
        setParams();
    }

    protected DbDaemon(String forumId) {
        super(forumId);
        setParams();
    }

    private void setParams() {
        final ForumParams params = GetForum.params(getForumId());
        setDoPerTime(params.getDbScanPerTime());
        setSleepPeriod(params.getDbScanPeriod());
        setRetryPeriod(params.getDbReconnectPeriod());
    }

    protected Process createProcess() {
        return new DbProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();
    }
}