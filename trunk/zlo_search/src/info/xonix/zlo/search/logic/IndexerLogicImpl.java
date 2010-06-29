package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.DbManager;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.model.Site;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 */
public class IndexerLogicImpl /*extends SiteSource*/ implements IndexerLogic {
    private static Logger logger = Logger.getLogger(IndexerLogicImpl.class);

//    private File indexDir;

    private int indexPerTime;
    private IndexWriter writer;
    private boolean reindex;
    private Analyzer analyzer;

    private DbManager dbManager;

    public IndexerLogicImpl(/*Site site*/) {
//        super(site);
        setIndexPerTime(100);
        setAnalyzer(Message.constructAnalyzer());
        setIndexDir(new File(Config.USE_DOUBLE_INDEX ? site.getIndexDirDouble() + "/" + DoubleIndexSearcher.SMALL_INDEX_DIR : Config.INDEX_DIR));
    }

    // todo: inject

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

/*    public File getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(File indexDir) {
        this.indexDir = indexDir;
    }*/

    public int getIndexPerTime() {
        return indexPerTime;
    }

    public void setIndexPerTime(int indexPerTime) {
        this.indexPerTime = indexPerTime;
    }

    public IndexWriter getWriter() {
        if (writer == null) {
            try {
                if (indexDir.list().length == 0)
                    reindex = true;

                writer = new IndexWriter(indexDir, analyzer, reindex);
            } catch (IOException e) {
                logger.error("Can't create writer", e);
            }
        }
        return writer;
    }

    public void indexRange(int startNum, int endNum) {
        Exception ex = null;
        try {
            IndexWriter writer = getWriter();
            logger.info("Indexing to directory '" + indexDir + "' range (" + startNum + " - " + endNum + ") ...");
            indexMsgs(startNum, endNum);
            logger.info("Optimizing...");
            writer.optimize();
            writer.close();
        } catch (IOException e) {
            ex = e;
        }
        if (ex != null) {
            logger.error("Exception occured: " + ex);
        }
    }

    private void indexMsgs(final int startNum, final int endNum) throws DAOException, IOException {
        int start = startNum, end;
        while (start < endNum) {
            if (start + indexPerTime > endNum) {
                end = endNum;
            } else {
                end = start + indexPerTime;
            }
            logger.info("Indexing part (" + start + " - " + end + ") ...");
            addMessagesToIndex(start, end);
            start = end;
        }
    }

    private void addMessagesToIndex(int start, int end) throws DAOException, IOException {
        IndexWriter writer = getWriter();
        for (Message msg : getSite().getDB().getMessages(start, end)) {
            if (msg.getStatus() == MessageStatus.OK) {
                logger.debug(getSiteName() + " - Addind: " + (Config.DEBUG ? msg : msg.getNum()));
                writer.addDocument(msg.getDocument());
            } else {
                logger.debug(getSiteName() + " - Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
            }
        }
        writer.flush();
    }

    /*
    for use in indexing daemon
    indexes end marks msgs in db as indexed
    indexes [from, to] including...
     */

    public void index(Site site, int from, int to) throws IOException {
        logger.info(String.format(site.getName() + " - Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));
        try {
            addMessagesToIndex(from, to + 1);
        } catch (DAOException e) {
            throw new DbException(e.getCause());
        }
        logger.info(site.getName() + " - Setting last indexed: " + to);
        dbManager.setLastIndexedNumber(to);
    }
}
