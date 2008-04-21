package info.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbDict;
import info.xonix.zlo.search.db.DbException;

import java.util.List;

/**
 * Author: Vovan
 * Date: 05.04.2008
 * Time: 18:53:55
 */
public abstract class DbMessagesIteratingDaemon extends Daemon {

    private static final Logger logger = Logger.getLogger("DbMessagesIteratingDaemon");
    private int processPerTime = 1000; // default
    private DbDict dbDict;

    private class DbMessagesIterationProcess extends Process {

        protected int getFromIndex() throws DAOException {
            return dbDict.getInt(getIteratingVariableName(), 0);
        }

        protected int getEndIndex() throws DAOException {
            return getSite().getLastMessageNumber();
        }

        protected void perform(int from, int to) throws DAOException {
            doWithMessages(getSite().getDB().getMessages(from, to + 1));
            dbDict.setInt(getIteratingVariableName(), to);
        }

        protected void cleanUp() {
        }
    }

    protected DbMessagesIteratingDaemon(Site site) {
        super(site);
        dbDict = getSite().getDbDict();
        setDoPerTime(processPerTime);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected Process createProcess() {
        return new DbMessagesIterationProcess();
    }

    protected void reset() throws DbException {
        dbDict.setInt(getIteratingVariableName(), 0);
    }

    protected abstract void doWithMessages(List<ZloMessage> msgs);
    protected abstract String getIteratingVariableName();
}
