package org.xonix.zlo.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.TimeUtils;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 13.12.2007
 * Time: 2:57:59
 */
public class DoubleIndexSearcher {
    private static final Logger logger = Logger.getLogger(DoubleIndexSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

    public static final String BIG_INDEX_DIR = "1";
    public static final String SMALL_INDEX_DIR = "2";

    private String indexesDir;

    private IndexReader bigReader;
    private IndexReader smallReader;

    private Sort renewingSort;

    private boolean isReopening = false;
    private long lastCreateTime = -1;

    public DoubleIndexSearcher(String dir, Sort renewingSort) {
        this.renewingSort = renewingSort;
        this.indexesDir = dir;
    }

    public IndexReader getBigReader() {
        if (bigReader == null) {
            try {
                bigReader = IndexReader.open(indexesDir + "/" + BIG_INDEX_DIR);
            } catch (IOException e) {
                logger.error("Can't create bigReader!");
            }
        }
        return bigReader;
    }

    public IndexReader getSmallReader() {
        if (smallReader == null) {
            try {
                smallReader = IndexReader.open(indexesDir + "/" + SMALL_INDEX_DIR);
            } catch (IOException e) {
                logger.error("Can't create smallReader!");
            }
        } else {
            startRecreatingSmallReaderIfNeeded();
        }
        return smallReader;
    }

    private void startRecreatingSmallReaderIfNeeded() {
        try {
            if (needToRecreateReader()) {
                synchronized(this) {
                    if (needToRecreateReader()) {
                        startReopeningThread();
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private boolean needToRecreateReader() throws IOException {
        return !smallReader.isCurrent()
                        && System.currentTimeMillis() - lastCreateTime > PERIOD_RECREATE_INDEXER
                        && !isReopening;
    }

    private void startReopeningThread() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                isReopening = true;

                logger.info("Start recreating indexReader in separate thread...");
                IndexReader ir = null,
                            oldIndexReader = smallReader;

                try {
                    ir = IndexReader.open(Config.INDEX_DIR);
                } catch (IOException e) {
                    logger.error("Error while recreating index reader: " + e);
                }

                // search to form memory caches
                try {
                    new IndexSearcher(ir).search(new MatchAllDocsQuery(), renewingSort);
                } catch (IOException e) {
                    logger.error("Problems recreating smallReader: ", e);
                    return;
                }

                smallReader = ir;
                lastCreateTime = System.currentTimeMillis();

                clean(oldIndexReader);
                logger.info("Successfuly recreated.");

                isReopening = false;
            }
        });

        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            logger.error("Error while closing index reader: " + e.getClass());
        }
    }

    public DoubleHits search(Query query, Sort sort) throws IOException {
        return new DoubleHits(
                new IndexSearcher(getBigReader()).search(query, sort),
                new IndexSearcher(getSmallReader()).search(query, sort)
        );
    }

    public void close() {
        try {
            bigReader.close();
            smallReader.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
