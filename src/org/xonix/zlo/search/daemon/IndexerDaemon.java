package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.TimeUtils;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.DAO;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(IndexerDaemon.class);

    public static final int INDEX_PER_TIME = Integer.parseInt(Config.getProp("indexer.daemon.index.per.time"));
    public static final int INDEX_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("indexer.daemon.period.to.index"));
    public static final int RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("indexer.daemon.period.to.reconnect"));

    private static final ZloIndexer indexer = new ZloIndexer(DAO.DB.SOURCE);

    private class IndexingProcess extends Process {
        private int indexFrom = -1;
        private int indexTo = -1;
        
        protected void doOneIteration() {
            try {
                if (indexFrom == -1)
                    indexFrom = DbManager.getLastIndexedNumber() + 1;

                if (indexTo == -1)
                    indexTo = DbManager.getLastMessageNumber();

                int end;

                if (indexFrom + INDEX_PER_TIME < indexTo) {
                    end = indexFrom + INDEX_PER_TIME;
                } else {
                    indexTo = DbManager.getLastMessageNumber();
                    if (indexFrom + INDEX_PER_TIME < indexTo)
                        end = indexFrom + INDEX_PER_TIME;
                    else
                        end = indexTo;
                }

                if (indexFrom <= end) {
                    indexer.index(indexFrom, end + 1);
                }

                indexFrom = end + 1;

                while (indexFrom > indexTo) {
                    logger.debug("indexFrom=" + indexFrom + " >= indexTo=" + indexTo + ". Sleeping...");
                    sleepSafe(INDEX_PERIOD);
                    indexTo = DbManager.getLastMessageNumber();
                }
            } catch (DbException e) {
                logger.warn("Problem with db: " + e.getClass());
                sleepSafe(RECONNECT_PERIOD);
            } catch (IOException e) {
                logger.error("IOException while indexing, probably something with index...");
            }
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

    public static void main(String[] args) {
        new IndexerDaemon().start();
    }
}
