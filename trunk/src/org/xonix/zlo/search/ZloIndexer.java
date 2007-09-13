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

    public void indexRange(int startNum, int endNum) {
        final File INDEX_DIR = new File(Config.INDEX_DIR);
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
        }
    }

    private void indexMsgs(IndexWriter writer, int startNum, int endNum) throws IOException{
        for (int i=startNum; i<=endNum; i++) {
            ZloMessage msg = DAO.Site.getMessageByNumber(i);
            if (msg != null) {
                log.info("Saving: "+msg);
                writer.addDocument(msg.getDocument());
            }
        }
    }

    public static void main(String[] args) {
        new ZloIndexer().indexRange(3765000, 3765010);
    }
}
