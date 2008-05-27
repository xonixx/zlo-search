package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.Site;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger logger = Logger.getLogger("IndexerDaemon");

    protected Logger getLogger() {
        return logger;
    }

    private class IndexingProcess extends Process {
        public IndexingProcess() {
            super();
        }

        protected int getFromIndex() throws DAOException {
            return getSite().getDbManager().getLastIndexedNumber();
        }

        protected int getEndIndex() throws DAOException {
            return getSite().getDbManager().getLastMessageNumber();
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
        setParams();
    }

    protected IndexerDaemon(Site site) {
        super(site);
        setParams();
    }

    private void setParams() {
        setDoPerTime(getSite().getINDEXER_INDEX_PER_TIME());
        setSleepPeriod(getSite().getINDEXER_INDEX_PERIOD());
        setRetryPeriod(getSite().getINDEXER_RECONNECT_PERIOD());
    }

    public static void main(String[] args) {
        new IndexerDaemon().start();
    }

    protected void start() {
        logger.info(MessageFormat.format("Starting indexing to {0} index...", Config.USE_DOUBLE_INDEX ? "double" : "simple"));

        // this is for clearing in case of not graceful exit
        if (Config.USE_DOUBLE_INDEX) {
            logger.info("Clearing lock...");
            new DoubleIndexSearcher(getSite(), null).clearLocks();
        }
        super.start();
    }
}
