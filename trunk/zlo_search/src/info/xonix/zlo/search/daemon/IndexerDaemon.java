package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.IndexerLogic;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger log = Logger.getLogger(IndexerDaemon.class);

//    private Site site;
    private AppLogic appLogic = AppSpringContext.get(AppLogic.class);
    private IndexerLogic indexerLogic = AppSpringContext.get(IndexerLogic.class);

    protected Logger getLogger() {
        return log;
    }

    private class IndexingProcess extends Process {
        public IndexingProcess() {
            super();
        }

        protected int getFromIndex() {
            return appLogic.getLastIndexedNumber(getSite());
        }

        protected int getEndIndex() {
            return appLogic.getLastMessageNumber(getSite());
        }

        protected void perform(int from, int to) {
//            try {
            indexerLogic.index(getSite(), from, to);
//            } catch (IOException e) {
//                throw new DAOException(e);
//            }
        }

        protected boolean processException(Exception e) {
/*            if (e instanceof DbException) {
                getLogger().warn(getSiteName() + " - Problem with db: " + e.getClass());
                return true;
            } else if (e instanceof IOException) {
                log.error(getSiteName() + " - IOException while indexing, probably something with index...", e);
                return true;
            }
            return false;*/
            log.error("Exception while indexing", e);
            return true;
        }

        protected void cleanUp() {
/*            try {
                getIndexer().getWriter().close();
            } catch (IOException e) {
                log.warn(getSiteName() + " - Can't close writer: ", e);
            }*/
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

/*    public IndexerDaemon() {
        super();
        setParams();
    }*/

    protected IndexerDaemon(Site site) {
        super(site);
        setParams();
    }

    private void setParams() {
        setDoPerTime(getSite().getIndexerIndexPerTime());
        setSleepPeriod(getSite().getIndexerIndexPeriod());
        setRetryPeriod(getSite().getIndexerReconnectPeriod());
    }

/*    public static void main(String[] args) {
        new IndexerDaemon().start();
    }*/

    protected void start() {
        log.info(MessageFormat.format("Starting indexing to {0} index...", Config.USE_DOUBLE_INDEX ? "double" : "simple"));

        // this is for clearing in case of not graceful exit
        if (Config.USE_DOUBLE_INDEX) {
            log.info("Clearing lock...");
            new DoubleIndexSearcher(getSite(), null).clearLocks();
        }
        super.start();
    }
}
