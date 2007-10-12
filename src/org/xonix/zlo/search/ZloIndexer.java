package org.xonix.zlo.search;

import org.apache.lucene.index.IndexWriter;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import static org.xonix.zlo.search.DAO.DAOException;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.test.storage.ZloStorage;

import java.io.File;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 */
public class ZloIndexer {
    private static Logger logger = Logger.getLogger(ZloIndexer.class);
    private IndexingSource source;
    private static File INDEX_DIR = new File(Config.INDEX_DIR);

    private static final int INDEX_PER_TIME = 50;

/*    static {
        try {
            FileHandler fileHandler = new FileHandler("__log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
/*    static {
        FileAppender appender = new FileAppender();
        appender.setName("fileAppender");
        appender.setFile("__log.txt");
        logger.addAppender(appender);
    }*/

    public ZloIndexer(IndexingSource source) {
        this(source, false);
    }

    public ZloIndexer(IndexingSource source, boolean reindex) {
        this.source = source;
        if (reindex) {
            logger.info("Clearing index dir...");
            for (File f : INDEX_DIR.listFiles()) {
                if(!f.delete()) {
                    logger.warn("Problems with delting file: " + f.getAbsolutePath());
                }
            }
        }
    }

    public void indexRange(int startNum, int endNum) {
        Exception ex = null;
        try {
            IndexWriter writer = new IndexWriter(INDEX_DIR, ZloMessage.constructAnalyzer(), true);
            logger.info("Indexing to directory '" + INDEX_DIR + "' range (" + startNum + " - " + endNum + ") ...");
            indexMsgs(writer, startNum, endNum);
            logger.info("Optimizing...");
            writer.optimize();
            writer.close();
        } catch (IOException e) {
            ex = e;
        } catch (DAOException e) {
            ex = e;
        }
        if (ex != null) {
            logger.error("Exception occured: " + ex);
        }
    }

    private void indexMsgs(IndexWriter writer, final int startNum, final int endNum) throws DAOException, IOException {
        int start = startNum, end;
        while (start < endNum) {
            if (start + INDEX_PER_TIME > endNum) {
                end = endNum;
            } else {
                end = start + INDEX_PER_TIME;
            }
            logger.info("Indexing part (" + start + " - " + end + ") ...");
            for (ZloMessage msg : source.getMessages(start, end)) {
                if (msg.getStatus() == ZloMessage.Status.OK) {
                    logger.debug("Addind: " + msg.getNum());
                    writer.addDocument(msg.getDocument());
                } else {
                    logger.debug("Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
                }
            }
            start = end;
        }
    }

    public static void main(String[] args) {
//        new ZloIndexer(DAO.Site.SOURCE).indexRange(3765000, 3765010);
        new ZloIndexer(DAO.DB.SOURCE, true).indexRange(1, 31000);
//        new ZloIndexer(new ZloStorage(), true).indexRange(ZloStorage.FROM, ZloStorage.TO);
    }
}
