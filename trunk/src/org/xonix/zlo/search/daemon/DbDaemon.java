package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.IndexingSource;
import org.xonix.zlo.search.dao.DAOException;
import org.xonix.zlo.search.dao.DB;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.model.ZloMessage;

import java.util.List;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class);

    public static int SCAN_PER_TIME;
    public static int SCAN_PERIOD;
    public static int RECONNECT_PERIOD;

//    private IndexingSource source;

    private class MainProcess extends Process {
        private int endSource = -1;
        private int startDb = -1;

        public MainProcess() {
            super();
        }

        protected void doOneIteration() {
            IndexingSource source = getSite();
            try {
                if (startDb == -1)
                    startDb = getDB().getLastMessageNumber() + 1;

                if (endSource == -1)
                    endSource = source.getLastMessageNumber();

                int end;

                if (startDb + SCAN_PER_TIME - 1 < endSource) {
                    end = startDb + SCAN_PER_TIME - 1;
                } else {
                    endSource = source.getLastMessageNumber();
                    if (startDb + SCAN_PER_TIME - 1 < endSource)
                        end = startDb + SCAN_PER_TIME - 1;
                    else
                        end = endSource;
                }

                if (startDb <= end) {
                    List<ZloMessage> msgs = source.getMessages(startDb, end + 1);
                    getDB().saveMessages(msgs);
                }

                startDb = end + 1;

                while (startDb > endSource) {
                    logger.debug("Sleeping " + SCAN_PERIOD / 1000 / 60f + " min...");
                    sleepSafe(SCAN_PERIOD);
                    endSource = source.getLastMessageNumber();
                }
            } catch (DAOException e) {
                if (e.getSource() instanceof DB) {
                    logger.warn("Problem with db: " + e);
                } else if (e.getSource() instanceof Site) {
                    logger.warn("Problem with site: " + e);
                }
                e.printStackTrace();
                sleepSafe(RECONNECT_PERIOD);
            }
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon() {
        super();
        SCAN_PER_TIME = getSite().DB_SCAN_PER_TIME;
        SCAN_PERIOD = getSite().DB_SCAN_PERIOD;
        RECONNECT_PERIOD = getSite().DB_RECONNECT_PERIOD;
    }

    protected Process createProcess() {
        return new MainProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();    
    }
}