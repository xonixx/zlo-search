package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.DAOException;
import org.xonix.zlo.search.dao.Site;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(IndexerDaemon.class);

    private class IndexingProcess extends Process {
        public IndexingProcess() {
            super();
        }

        protected int getFromIndex() throws DAOException {
            return getDbManager().getLastIndexedNumber();
        }

        protected int getEndIndex() throws DAOException {
            return getDbManager().getLastMessageNumber();
        }

        protected void perform(int from, int to) throws DAOException {
            try {
                getIndexer().index(from, to);
            } catch (IOException e) {
                throw new DAOException(e);
            }
        }

        protected void cleanUp() {
            try {
                getIndexer().getWriter().close();
            } catch (IOException e) {
                logger.warn(getSiteName() + " - Can't close writer: ", e);
            }
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

    public IndexerDaemon() {
        super();
        DO_PER_TIME = getSite().INDEXER_INDEX_PER_TIME;
        SLEEP_PERIOD = getSite().INDEXER_INDEX_PERIOD;
        RETRY_PERIOD = getSite().INDEXER_RECONNECT_PERIOD;
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
