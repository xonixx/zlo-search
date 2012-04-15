package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.config.forums.ForumParams;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.index.doubleindex.DoubleIndexManager;
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
            return appLogic.getLastIndexedNumber(getForumId());
        }

        @Override
        protected int getEndIndex() {
            return appLogic.getLastSavedMessageNumber(getForumId());
        }

        @Override
        protected void perform(int from, int to) throws IndexerException {
            indexerLogic.index(getForumId(), from, to);
        }

        @Override
        protected boolean processException(Exception e) {
            log.error("Exception while indexing", e);

            exceptionsLogger.logException(e,
                    "Exception in indexer daemon: " + getForumId(),
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

    protected IndexerDaemon(String forumId) {
        super(forumId);
        setParams();
    }

    private void setParams() {
        final ForumParams params = GetForum.params(getForumId());
        setDoPerTime(params.getIndexerIndexPerTime());
        setSleepPeriod(params.getIndexerIndexPeriod());
        setRetryPeriod(params.getIndexerReconnectPeriod());
    }

    protected void start() {
        log.info("Starting indexing to " + getForumId() + " index (double index)...");

        // this is for clearing in case of not graceful exit
        log.info("Clearing lock...");
        DoubleIndexManager.create(getForumId(), null).clearLocks();

        super.start();
    }
}
