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

    protected IndexerDaemon(Site site) {
        super(site);
        setParams();
    }

    private void setParams() {
        setDoPerTime(getSite().getIndexerIndexPerTime());
        setSleepPeriod(getSite().getIndexerIndexPeriod());
        setRetryPeriod(getSite().getIndexerReconnectPeriod());
    }

    protected void start() {
        final Site site = getSite();
        log.info("Starting indexing to " + site.getName() + " index (double index)...");

        // this is for clearing in case of not graceful exit
        log.info("Clearing lock...");
        new DoubleIndexManager(site, null).clearLocks();

        super.start();
    }
}
