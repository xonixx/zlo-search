package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.Site;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger("DbDaemon");

    protected Logger getLogger() {
        return logger;
    }

    private class DbProcess extends Process {
        public DbProcess() {
            super();
        }

        protected int getFromIndex() throws DAOException {
            return getSite().getDB().getLastMessageNumber();
        }

        protected int getEndIndex() throws DAOException {
            return getSite().getLastMessageNumber();
        }

        protected void perform(int from, int to) throws DAOException {
            getSite().getDB().saveMessages(getSite().getMessages(from, to + 1));
            getSite().getDbManager().setLastSavedDate(new Date());
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon() {
        super();
        setParams();
    }

    protected DbDaemon(Site site) {
        super(site);
        setParams();
    }

    private void setParams() {
        setDoPerTime(getSite().getDB_SCAN_PER_TIME());
        setSleepPeriod(getSite().getDB_SCAN_PERIOD());
        setRetryPeriod(getSite().getDB_RECONNECT_PERIOD());
    }

    protected Process createProcess() {
        return new DbProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();    
    }
}