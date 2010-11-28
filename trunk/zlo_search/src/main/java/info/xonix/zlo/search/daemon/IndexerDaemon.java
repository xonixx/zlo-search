package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.IndexerException;
import info.xonix.zlo.search.logic.IndexerLogic;
import info.xonix.zlo.search.logic.exceptions.ExceptionCategory;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

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
        @Override
        protected int getFromIndex() {
            return appLogic.getLastIndexedNumber(getSite());
        }

        @Override
        protected int getEndIndex() {
            return appLogic.getLastSavedMessageNumber(getSite());
        }

        @Override
        protected void perform(int from, int to) throws IndexerException {
            indexerLogic.index(getSite(), from, to);
        }

        @Override
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

            exceptionsLogger.logException(e,
                    "Exception in indexer daemon: " + getSiteName(),
                    getClass(),
                    ExceptionCategory.DAEMON);

            return false;
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
        final Site site = getSite();
        log.info("Starting indexing to " + site.getName() + " index (double index)...");

        // this is for clearing in case of not graceful exit
        log.info("Clearing lock...");
        new DoubleIndexManager(site, null).clearLocks();

        super.start();
    }
}
