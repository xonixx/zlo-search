package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.dao.DbDictImpl;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
 * Date: 05.04.2008
 * Time: 18:53:55
 */
public abstract class DbMessagesIteratingDaemon extends Daemon {

    private static final Logger logger = Logger.getLogger("DbMessagesIteratingDaemon");
    private int processPerTime = 1000; // default
    private DbDictImpl dbDict;

    private class DbMessagesIterationProcess extends Process {

        protected int getFromIndex() {
            return dbDict.getInt(getIteratingVariableName(), 0);
        }

        protected int getEndIndex() {
            return getSite().getLastMessageNumber();
        }

        protected void perform(int from, int to) {
            doWithMessages(getSite().getDB().getMessages(from, to + 1));
            dbDict.setInt(getIteratingVariableName(), to);
        }

        protected boolean processException(Exception ex) {
            return false;
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

    protected void reset() {
        dbDict.setInt(getIteratingVariableName(), 0);
    }

    protected abstract void doWithMessages(List<Message> msgs);

    protected abstract String getIteratingVariableName();
}
