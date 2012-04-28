package info.xonix.zlo.search.daemons.old;

import info.xonix.utils.daemon.IteratingDaemon;
import info.xonix.zlo.search.dao.DbDict;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
 * Date: 05.04.2008
 * Time: 18:53:55
 */
public abstract class DbMessagesIteratingDaemon extends IteratingDaemon {

    private DbDict dbDict = AppSpringContext.get(DbDict.class);
    private AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    private static final Logger logger = Logger.getLogger(DbMessagesIteratingDaemon.class);

    private class DbMessagesIterationProcess extends Process {

        protected int getFromIndex() {
            return dbDict.getInt(getForumId(), getIteratingVariableName(), 0);
        }

        protected int getEndIndex() {
            return appLogic.getLastSavedMessageNumber(getForumId());
        }

        protected void perform(int from, int to) {
            doWithMessages(appLogic.getMessages(getForumId(), from, to + 1));
            dbDict.setInt(getForumId(), getIteratingVariableName(), to);
        }

        protected boolean processException(Exception ex) {
            return false;
        }

        protected void cleanUp() {
        }
    }

    protected DbMessagesIteratingDaemon(String forumId, int processPerTime) {
        super(forumId, processPerTime, 0, 1000);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected Process createProcess() {
        return new DbMessagesIterationProcess();
    }

    protected void doReset() {
        dbDict.setInt(getForumId(), getIteratingVariableName(), 0);
    }

    protected abstract void doWithMessages(List<Message> msgs);

    protected abstract String getIteratingVariableName();
}
