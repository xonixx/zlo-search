package org.xonix.zlo.search;

import org.apache.lucene.index.IndexWriter;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;

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
    static {
        try {
            FileHandler fileHandler = new FileHandler("__log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            log.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexRange(IndexingSource source, int startNum, int endNum) {
        final File INDEX_DIR = new File(Config.INDEX_DIR);
        try {
          IndexWriter writer = new IndexWriter(INDEX_DIR, ZloMessage.constructAnalyzer(), true);
          log.info("Indexing to directory '" +INDEX_DIR+ "'...");
          indexMsgs(writer, source, startNum, endNum);
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

    private void indexMsgs(IndexWriter writer, IndexingSource source, int startNum, int endNum) throws DAO.Exception{
        for (int i=startNum; i<=endNum; i++) {
            ZloMessage msg = source.getMessageByNumber(i);
            if (msg != null) {
                log.info("Saving: "+msg);
                try {
                    writer.addDocument(msg.getDocument());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ZloIndexer().indexRange(DAO.Site.SOURCE, 3765000, 3765010);
    }
}
