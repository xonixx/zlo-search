package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 * TODO: handle clean-up
 */
public class IndexerLogicImpl implements IndexerLogic, InitializingBean {
    private static Logger log = Logger.getLogger(IndexerLogicImpl.class);

//    private boolean reindex;

    private Config config;
    private AppLogic appLogic;
    private ZloSearcher zloSearcher;

    private SiteFactory<IndexWriter> siteToIndexWriter = new SiteFactory<IndexWriter>() {
        @Override
        protected IndexWriter create(Site site) {
            IndexWriter writer;
            try {
                File indexDir = getIndexDir(site);
//                if (indexDir.list().length == 0)
//                    reindex = true;

                writer = new IndexWriter(FSDirectory.open(indexDir), config.getMessageAnalyzer(),
                        IndexWriter.MaxFieldLength.UNLIMITED); // for unlimited - see http://www.gossamer-threads.com/lists/lucene/java-user/91611

                writer.setMergeFactor(7); // optimized for search
            } catch (IOException e) {
                throw new RuntimeException("Can't create writer", e);
            }
            return writer;
        }

        private File getIndexDir(Site site) {
            return new File(site.getIndexDirDouble() + "/" + DoubleIndexManager.SMALL_INDEX_DIR);
        }
    };

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setAppLogic(AppLogic appLogic) {
        this.appLogic = appLogic;
    }

    public void setZloSearcher(ZloSearcher zloSearcher) {
        this.zloSearcher = zloSearcher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(appLogic, "appLogic");
        Check.isSet(zloSearcher, "zloSearcher");
    }

    private IndexWriter getWriter(Site site) {
        return siteToIndexWriter.get(site);
    }

    private void addMessagesToIndex(Site site, int start, int end) throws IndexerException {
        final IndexWriter writer = getWriter(site);
        try {
            for (Message msg : appLogic.getMessages(site, start, end)) {
                if (msg.getStatus() == MessageStatus.OK) {
                    log.debug(site.getName() + " - Addind: " + (config.isDebug() ? msg : msg.getNum()));
                    writer.addDocument(msg.getDocument());
                } else {
                    log.debug(site.getName() + " - Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
                }
            }
//            writer.flush();
            // committing changes (flush() will rollback at next write)
            writer.commit();
        } catch (IOException e) {
            log.error("Exception while adding to index", e);
            throw new IndexerException(e);
        }
    }

    /**
     * for use in indexing daemon
     * indexes end marks msgs in db as indexed
     * indexes [from, to] including...
     * TODO: this must be transactional!
     */
    @Override
    public void index(Site site, int from, int to) throws IndexerException {
        final String siteName = site.getName();

        if (from <= 1) {
            log.info("[!] Dropping index for site: " + siteName + ", as from index=" + from);
            try {
                zloSearcher.dropIndex(site);
            } catch (IOException e) {
                log.error("Error while dropping index for site: " + siteName, e);
            }
        }

        log.info(String.format(siteName + " - Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));

        addMessagesToIndex(site, from, to + 1);

        log.info(siteName + " - Setting last indexed: " + to);

        appLogic.setLastIndexedNumber(site, to);
    }
}
