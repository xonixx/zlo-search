package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.dao.DAOException;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class);

    private class MainProcess extends Process {
        public MainProcess() {
            super();
        }

        protected int getFromIndex() throws DAOException {
            return getDB().getLastMessageNumber();
        }

        protected int getEndIndex() throws DAOException {
            return getSite().getLastMessageNumber();
        }

        protected void perform(int from, int to) throws DAOException {
            getDB().saveMessages(getSite().getMessages(from, to + 1));
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon() {
        super();
        DO_PER_TIME = getSite().DB_SCAN_PER_TIME;
        SLEEP_PERIOD = getSite().DB_SCAN_PERIOD;
        RETRY_PERIOD = getSite().DB_RECONNECT_PERIOD;
    }

    protected Process createProcess() {
        return new MainProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();    
    }
}