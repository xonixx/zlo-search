package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
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

//    private File indexDir;

    private int indexPerTime;
//    private IndexWriter writer;
    private boolean reindex;

//    private DbManager dbManager;
    private Config config;
    private AppLogic appLogic;

    private SiteFactory<IndexWriter> siteToIndexWriter = new SiteFactory<IndexWriter>() {
        @Override
        protected IndexWriter create(Site site) {
            IndexWriter writer;
            try {
                File indexDir = getIndexDir(site);
                if (indexDir.list().length == 0)
                    reindex = true;

                writer = new IndexWriter(indexDir, config.getMessageAnalyzer(), reindex);
                // TODO!!!
//                writer.setMergeFactor();
            } catch (IOException e) {
                log.error("Can't create writer", e);
                throw new IndexerException(e);
            }
            return writer;
        }

        private File getIndexDir(Site site) {
            return new File(site.getIndexDirDouble() + "/" + DoubleIndexSearcher.SMALL_INDEX_DIR);
        }
    };


    public IndexerLogicImpl() {
        // TODO: move to config
        indexPerTime = 100;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setAppLogic(AppLogic appLogic) {
        this.appLogic = appLogic;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(appLogic, "appLogic");
    }

/*    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }*/

/*   public File getIndexDir(Site site) {
        return siteToIndexDir.get(site);
    }

     public void setIndexDir(File indexDir) {
        this.indexDir = indexDir;
    }

    public int getIndexPerTime() {
        return indexPerTime;
    }
*/

/*    public void setIndexPerTime(int indexPerTime) {
        this.indexPerTime = indexPerTime;
    }*/

    private IndexWriter getWriter(Site site) {
        return siteToIndexWriter.get(site);
    }

    public void indexRange(Site site, int startNum, int endNum) {
//        Exception ex = null;
        try {
            IndexWriter writer = getWriter(site);
//            log.info("Indexing to directory '" + writer.getDirectory() + "' range (" + startNum + " - " + endNum + ") ...");
            log.info("Indexing for site [" + site.getName() + "] range (" + startNum + " - " + endNum + ") ...");

            indexMsgs(site, startNum, endNum);

            // TODO!!!
            log.info("Optimizing...");

            writer.optimize();
            writer.flush();
//            writer.close();
        } catch (IOException e) {
            log.error("Exception while indexing occured: " + e);
            throw new IndexerException(e);
//            ex = e;
        }
/*        if (ex != null) {
            log.error("Exception occured: " + ex);
        }*/
    }

    private void indexMsgs(Site site, final int startNum, final int endNum) {
        int start = startNum, end;
        while (start < endNum) {
            if (start + indexPerTime > endNum) {
                end = endNum;
            } else {
                end = start + indexPerTime;
            }
            log.info("Indexing part (" + start + " - " + end + ") ...");
            addMessagesToIndex(site, start, end);
            start = end;
        }
    }

    private void addMessagesToIndex(Site site, int start, int end) {
        IndexWriter writer = getWriter(site);
        try {
            for (Message msg : appLogic.getMessages(site, start, end)) {
                if (msg.getStatus() == MessageStatus.OK) {
                    log.debug(site.getName() + " - Addind: " + (config.isDebug() ? msg : msg.getNum()));
                    writer.addDocument(msg.getDocument());
                } else {
                    log.debug(site.getName() + " - Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
                }
            }
            writer.flush();
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
    public void index(Site site, int from, int to) {
        log.info(String.format(site.getName() + " - Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));
//        try {
        addMessagesToIndex(site, from, to + 1);
//        } catch (DAOException e) {
//            throw new DbException(e.getCause());
//        }
        log.info(site.getName() + " - Setting last indexed: " + to);
        appLogic.setLastIndexedNumber(site, to);
    }
}
