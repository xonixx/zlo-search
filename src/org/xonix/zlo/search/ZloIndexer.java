package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.DAOException;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManagerSource;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.File;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 */
public class ZloIndexer extends DbManagerSource {
    private static Logger logger = Logger.getLogger(ZloIndexer.class);

    private File INDEX_DIR;

    private static final int INDEX_PER_TIME = 50;

    private IndexWriter writer;
    private boolean reindex;

    public ZloIndexer(Site site) {
        super(site);
        INDEX_DIR = new File(Config.USE_DOUBLE_INDEX ? site.INDEX_DIR_DOUBLE + "/" + DoubleIndexSearcher.SMALL_INDEX_DIR : Config.INDEX_DIR);
    }

    public IndexWriter getWriter() {
        if (writer == null) {
            try {
                if (INDEX_DIR.list().length == 0)
                    reindex = true;

                writer = new IndexWriter(INDEX_DIR, ZloMessage.constructAnalyzer(), reindex);
            } catch (IOException e) {
                logger.error("Can't create writer", e);
            }
        }
        return writer;
    }

    private void indexRange(int startNum, int endNum) {
        Exception ex = null;
        try {
            IndexWriter writer = getWriter();
            logger.info("Indexing to directory '" + INDEX_DIR + "' range (" + startNum + " - " + endNum + ") ...");
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
            if (start + INDEX_PER_TIME > endNum) {
                end = endNum;
            } else {
                end = start + INDEX_PER_TIME;
            }
            logger.info("Indexing part (" + start + " - " + end + ") ...");
            addMessagesToIndex(start, end);
            start = end;
        }
    }

    private void addMessagesToIndex(int start, int end) throws DAOException, IOException {
        IndexWriter writer = getWriter();
        for (ZloMessage msg : getDB().getMessages(start, end)) {
            if (msg.getStatus() == ZloMessage.Status.OK) {
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
    public void index(int from, int to) throws IOException {
        logger.info(String.format("Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));
        try {
            addMessagesToIndex(from, to + 1);
        } catch (DAOException e) {
            throw new DbException(e.getCause());
        }
        logger.info("Setting last indexed: " + to);
        getDB().getDbManager().setLastIndexedNumber(to);
    }
}
