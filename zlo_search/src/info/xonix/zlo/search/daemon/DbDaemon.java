package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.db.DbException;
import org.apache.log4j.Logger;

import java.net.ConnectException;
import java.sql.BatchUpdateException;
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

        protected boolean processException(Exception e) {
            if (e instanceof DbException) {
                logger.warn(getSiteName() + " - Problem with db: " + e.getClass(), e);
                return true;
            } else if (e instanceof DAOException) {
                if (e.getCause() instanceof ConnectException) {
                    logger.error(getSiteName() + " - Problem with site... " + e.getCause().getClass().getName());
                } else if (e.getCause() instanceof DbException && e.getCause().getCause() instanceof BatchUpdateException) {
                    logger.error(getSiteName(), e);
                    logger.info("Resetting...");
                    reset();
                } else {
                    logger.error(getSiteName(), e);
                }
                return true;
            }

            return false;
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
        setDoPerTime(getSite().getDbScanPerTime());
        setSleepPeriod(getSite().getDbScanPeriod());
        setRetryPeriod(getSite().getDbReconnectPeriod());
    }

    protected Process createProcess() {
        return new DbProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();    
    }
}