package org.xonix.zlo.search;

import org.apache.lucene.index.IndexWriter;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.test.storage.ZloStorage;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 */
public class ZloIndexer {
    private static Logger log = Logger.getLogger(ZloIndexer.class.getName());
    private IndexingSource source;
    private static File INDEX_DIR = new File(Config.INDEX_DIR);

    static {
        try {
            FileHandler fileHandler = new FileHandler("__log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            log.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ZloIndexer(IndexingSource source) {
        this(source, false);
    }

    public ZloIndexer(IndexingSource source, boolean reindex) {
        this.source = source;
        if (reindex) {
            log.info("Clearing index dir...");
            for (File f : INDEX_DIR.listFiles()) {
                if(!f.delete()) {
                    log.warning("Problems with delting file: " + f.getAbsolutePath());
                }
            }
        }
    }

    public void indexRange(int startNum, int endNum) {
        try {
          IndexWriter writer = new IndexWriter(INDEX_DIR, ZloMessage.constructAnalyzer(), true);
          log.info("Indexing to directory '" +INDEX_DIR+ "'...");
          indexMsgs(writer, startNum, endNum);
          log.info("Optimizing...");
          writer.optimize();
          writer.close();
        } catch (IOException e) {
          System.out.println(" caught a " + e.getClass() +
           "\n with message: " + e.getMessage());
        } catch (DAO.Exception e) {
            e.printStackTrace();
        }
    }

    private void indexMsgs(IndexWriter writer, int startNum, int endNum) throws DAO.Exception{
        for (int i=startNum; i<=endNum; i++) {
            ZloMessage msg = source.getMessageByNumber(i);
            if (msg.getStatus() == ZloMessage.Status.OK) {
                log.info("Saving: "+msg);
                try {
                    writer.addDocument(msg.getDocument());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("Not saving: " + msg);
            }
        }
    }

    public static void main(String[] args) {
//        new ZloIndexer(DAO.Site.SOURCE).indexRange(3765000, 3765010);
        new ZloIndexer(new ZloStorage(), true).indexRange(ZloStorage.FROM, ZloStorage.TO);
    }
}
