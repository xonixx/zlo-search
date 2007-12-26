package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.TimeUtils;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.DoubleIndexSearcher;

import java.io.IOException;
import java.text.MessageFormat;

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
        private int end = -1;
        
        protected void doOneIteration() {
            try {
                if (indexFrom == -1) {
                    int inIndex = ZloSearcher.getLastIndexedNumber();
                    indexFrom = (Config.USE_DOUBLE_INDEX
                            ? (inIndex != -1 
                                ? inIndex  // more reliable
                                : DbManager.getLastIndexedNumber() + 1)
                            : DbManager.getLastIndexedNumber()) + 1;
                }

                if (end == -1)
                    end = DbManager.getLastMessageNumber();

                int indexTo;

                if (indexFrom + INDEX_PER_TIME - 1 < end) {
                    indexTo = indexFrom + INDEX_PER_TIME - 1;
                } else {
                    end = DbManager.getLastMessageNumber();
                    if (indexFrom + INDEX_PER_TIME - 1 < end)
                        indexTo = indexFrom + INDEX_PER_TIME - 1;
                    else
                        indexTo = end;
                }

                if (indexFrom <= indexTo) {
                    indexer.index(indexFrom, indexTo);
                }

                indexFrom = indexTo + 1;

                while (indexFrom > end) {
                    logger.debug("Sleeping " + INDEX_PERIOD / 1000 / 60f + " min...");
                    sleepSafe(INDEX_PERIOD);
                    end = DbManager.getLastMessageNumber();
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
                indexer.getWriter().close();
            } catch (IOException e) {
                logger.warn("Can't close writer: ", e);
            }
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

    public static void main(String[] args) {
        logger.info(MessageFormat.format("Starting indexing to {0} index...", Config.USE_DOUBLE_INDEX ? "double" : "simple"));
        if (Config.USE_DOUBLE_INDEX) {
            logger.info("Clearing lock...");
            try {
                Directory d = FSDirectory.getDirectory(new DoubleIndexSearcher(null).getSmallPath());
                d.clearLock("write.lock");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new IndexerDaemon().start();
    }
}
