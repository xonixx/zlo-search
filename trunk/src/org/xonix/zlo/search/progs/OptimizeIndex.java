package org.xonix.zlo.search.progs;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.Site;

import java.io.IOException;

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
                IndexWriter w = new ZloIndexer(new Site("zlo")).getWriter();
                logger.info("Optimizing index...");
                w.optimize();
                w.close();
                logger.info("Done.");
            } else {
                DoubleIndexSearcher dis = ZloSearcher.forSite(new Site("zlo")).getDoubleIndexSearcher();
/*                int lastIndexedInDb = DbManager.getLastIndexedNumber();
                int lastIndexedInIndex = ZloSearcher.getLastIndexedNumber();
                if (lastIndexedInIndex != lastIndexedInDb) {
                    logger.warn(MessageFormat.format("Last indexed nums not equal! db={0}, index={1}", lastIndexedInDb, lastIndexedInIndex));
                    DbManager.setLastIndexedNumber(lastIndexedInIndex);
                }*/
                dis.moveSmallToBig();
                dis.optimize();
                dis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
