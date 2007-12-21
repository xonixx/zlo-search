package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.config.Config;
import org.apache.lucene.index.IndexWriter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 26.11.2007
 * Time: 19:27:49
 */
public class OptimizeIndex {
    public static final Logger logger = Logger.getLogger(OptimizeIndex.class);

    public static void main(String[] args) {
        try {
            if (!Config.USE_DOUBLE_INDEX) {
                IndexWriter w = new ZloIndexer(DAO.DB.SOURCE).getWriter();
                logger.info("Optimizing index...");
                w.optimize();
                w.close();
                logger.info("Done.");
            } else {
                DoubleIndexSearcher dis = ZloSearcher.getDoubleIndexSearcher();
                int lastIndexedInDb = DbManager.getLastIndexedNumber();
                int lastIndexedInIndex = ZloSearcher.getLastIndexedNumber();
                if (lastIndexedInIndex != lastIndexedInDb) {
                    logger.warn(MessageFormat.format("Last indexed nums not equal! db={0}, index={1}", lastIndexedInDb, lastIndexedInIndex));
                    DbManager.setLastIndexedNumber(lastIndexedInIndex);
                }
                dis.moveSmallToBig();
                dis.optimize();
                dis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
