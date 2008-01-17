package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.db.DbException;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(IndexerDaemon.class);

    public static int INDEX_PER_TIME;
    public static int INDEX_PERIOD;
    public static int RECONNECT_PERIOD;

    private class IndexingProcess extends Process {
        private int indexFrom = -1;
        private int end = -1;

        public IndexingProcess() {
            super();
        }

        protected void doOneIteration() {
            try {
                if (indexFrom == -1) {
                    int inIndex = getSite().getZloSearcher().getLastIndexedNumber();
                    indexFrom = (Config.USE_DOUBLE_INDEX
                            ? (inIndex != -1 
                                ? inIndex  // more reliable
                                : getDbManager().getLastIndexedNumber() + 1)
                            : getDbManager().getLastIndexedNumber()) + 1;
                }

                if (end == -1)
                    end = getDbManager().getLastMessageNumber();

                int indexTo;

                if (indexFrom + INDEX_PER_TIME - 1 < end) {
                    indexTo = indexFrom + INDEX_PER_TIME - 1;
                } else {
                    end = getDbManager().getLastMessageNumber();
                    if (indexFrom + INDEX_PER_TIME - 1 < end)
                        indexTo = indexFrom + INDEX_PER_TIME - 1;
                    else
                        indexTo = end;
                }

                if (indexFrom <= indexTo) {
                    getIndexer().index(indexFrom, indexTo);
                }

                indexFrom = indexTo + 1;

                while (indexFrom > end) {
                    logger.debug("Sleeping " + INDEX_PERIOD / 1000 / 60f + " min...");
                    sleepSafe(INDEX_PERIOD);
                    end = getDbManager().getLastMessageNumber();
                }
            } catch (DbException e) {
                logger.warn("Problem with db: " + e.getClass());
                sleepSafe(RECONNECT_PERIOD);
            } catch (IOException e) {
                logger.error("IOException while indexing, probably something with index...", e);
                sleepSafe(RECONNECT_PERIOD);
            }
        }

        protected void cleanUp() {
            try {
                getIndexer().getWriter().close();
            } catch (IOException e) {
                logger.warn("Can't close writer: ", e);
            }
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

    public IndexerDaemon() {
        super();
        INDEX_PER_TIME = getSite().INDEXER_INDEX_PER_TIME;
        INDEX_PERIOD = getSite().INDEXER_INDEX_PERIOD;
        RECONNECT_PERIOD = getSite().INDEXER_RECONNECT_PERIOD;
    }

    public static void main(String[] args) {
        logger.info(MessageFormat.format("Starting indexing to {0} index...", Config.USE_DOUBLE_INDEX ? "double" : "simple"));
        if (Config.USE_DOUBLE_INDEX) {
            logger.info("Clearing lock...");
            new DoubleIndexSearcher(Site.forName(Config.getSiteEnvName()), null).clearLocks();
        }
        new IndexerDaemon().start();
    }
}
