package info.xonix.zlo.search.doubleindex;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.site.SiteAccessor;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 13.12.2007
 * Time: 2:57:59
 */
public class DoubleIndexSearcher {
    private static final Logger logger = Logger.getLogger(DoubleIndexSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

    public static final String BIG_INDEX_DIR =      "1";
    public static final String SMALL_INDEX_DIR =    "2";

    private String indexesDir;

    private Sort renewingSort;

    private boolean isReopeningSmall = false;
    private boolean isReopeningBig = false;

    private long lastCreateTime = -1;

    private final Object closeLock = new Object();

    private Date renewDate;

    private DoubleIndexSearcher(String dir, Sort renewingSort) {
        this.renewingSort = renewingSort;
        this.indexesDir = dir;
        renewDate = new Date();
    }

    public DoubleIndexSearcher(SiteAccessor site, Sort renewingSort) {
        this(site.getINDEX_DIR_DOUBLE(), renewingSort);
    }

    public String getBigPath() {
        String path = indexesDir + "/" + BIG_INDEX_DIR;
        createDirIfAbsent(path);
        return path;
    }

    public String getSmallPath() {
        String path = indexesDir + "/" + SMALL_INDEX_DIR;
        createDirIfAbsent(path);
        return path;
    }

    private void createDirIfAbsent(String path) {
        File f = new File(path);
        if (!f.exists()) {
            logger.info("Creating dir: " + path);
            if (!f.mkdirs()) {
                logger.warn("Error creating dir...");
            }
        }
    }

    private IndexReader bigReader;
    public IndexReader getBigReader() {
        if (bigReader == null) {
            try {
                bigReader = IndexReader.open(getBigPath());
            } catch (IOException e) {
                logger.error("Can't create bigReader... Creating empty one...");
                try {
                    createEmptyIndex(getBigPath());
                    bigReader = IndexReader.open(getBigPath());
                } catch (IOException e1) {
                    logger.error("Can't create empty big reader: ", e1);
                }
            }
        } else {
            startRecreatingReaderIfNeeded(bigReader);
        }
        return bigReader;
    }

    public void setBigReader(IndexReader bigReader) {
        this.bigReader = bigReader;
    }

    private IndexReader smallReader;
    public IndexReader getSmallReader() {
        if (smallReader == null) {
            try {
                smallReader = IndexReader.open(getSmallPath());
                renewDate = new Date();
            } catch (IOException e) {
                logger.error("Can't create smallReader... Creating empty one...");
                try {
                    createEmptyIndex(getSmallPath());
                    smallReader = IndexReader.open(getSmallPath());
                } catch (IOException e1) {
                    logger.error("Can't create empty small reader: ", e1);
                }
            }
        } else {
            // not in separate thread because it is fast && with thread -> AlreadyClosedException on reader can happen
            if (needToRecreateReader(smallReader)) {
                performReopen(smallReader);
            }
        }
        return smallReader;
    }

    public void setSmallReader(IndexReader smallReader) {
        this.smallReader = smallReader;
    }

    private void startRecreatingReaderIfNeeded(IndexReader r) {
        if (needToRecreateReader(r)) {
            synchronized(this) {
                if (needToRecreateReader(r)) {
                    startReopeningThread(r);
                }
            }
        }
    }

    private boolean needToRecreateReader(IndexReader r) {
        boolean isSmall = r == smallReader;
        try {
            if (isSmall)
                return !smallReader.isCurrent()
                        && !isReopeningSmall
                        && System.currentTimeMillis() - lastCreateTime > PERIOD_RECREATE_INDEXER;
            else // big
                return !bigReader.isCurrent()
                        && !isReopeningBig;
        } catch (IOException e) {
            logger.error(e);
            return true;
        }
    }

    private void performReopen(IndexReader r) {
        boolean isSmall = r == smallReader;
        logger.debug(MessageFormat.format("Start recreating {0} indexReader...", isSmall ? "small" : "big"));
        if (isSmall)
            isReopeningSmall = true;
        else
            isReopeningBig = true;

        IndexReader ir = null,
                    oldIndexReader = isSmall ? smallReader : bigReader;

        try {
            ir = IndexReader.open(
                    isSmall
                            ? getSmallPath()
                            : getBigPath());
        } catch (IOException e) {
            logger.error("Error while recreating index reader: " + e);
        }

        // search to form memory caches
        IndexSearcher is = null;
        try {
            is = new IndexSearcher(ir);
            is.search(new MatchAllDocsQuery(), renewingSort);
        } catch (IOException e) {
            logger.error("Problems recreating smallReader: ", e);
            return;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (isSmall) {
            smallReader = ir;
            lastCreateTime = System.currentTimeMillis();
        } else { // big
            bigReader = ir;
        }

        synchronized(closeLock) { // to give ability to perform searh if oldReader already used
            clean(oldIndexReader);
        }

        logger.info("Successfuly recreated.");

        if (isSmall)
            isReopeningSmall = false;
        else
            isReopeningBig = false;

        renewDate = new Date();
    }

    private void startReopeningThread(final IndexReader r) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                performReopen(r);
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

    public DoubleHits search(Query query) throws IOException {
        return new DoubleHits1(
                new IndexSearcher(getBigReader()).search(query, Sort.INDEXORDER),
                new IndexSearcher(getSmallReader()).search(query, Sort.INDEXORDER)
        );
    }

    public void drop() throws IOException {
        createEmptyIndex(getBigPath());    
        createEmptyIndex(getSmallPath());
    }

    public void close() {
        clean(bigReader);
        clean(smallReader);
    }

    public void moveSmallToBig() throws IOException {
        logger.info("Start moving small to big...");

        IndexWriter bigIndexWriter = new IndexWriter(getBigPath(), ZloMessage.constructAnalyzer());
        IndexReader smlR = getSmallReader();
        logger.info("Moving small to big...");
        bigIndexWriter.addIndexesNoOptimize(new Directory[]{FSDirectory.getDirectory(getSmallPath())}); // add small to big, w/o optimize

        smlR.close();
        bigIndexWriter.close();

        logger.info("Cleaning small index...");
        createEmptyIndex(getSmallPath()); // empty small index
        logger.info("Done moving small to big.");
    }

    public void optimize() throws IOException {
        logger.info("Optimizing...");
        IndexWriter iw = new IndexWriter(getBigPath(), ZloMessage.constructAnalyzer());
        iw.optimize();
        iw.close();
        iw = new IndexWriter(getSmallPath(), ZloMessage.constructAnalyzer());
        iw.optimize();
        iw.close();
        logger.info("Done.");
    }

    private void createEmptyIndex(String path) throws IOException {
        IndexWriter indexWriter = new IndexWriter(path, ZloMessage.constructAnalyzer(), true);
        indexWriter.setUseCompoundFile(true);
        indexWriter.close();
    }

    public void clearLocks() {
        try {
            Directory d = FSDirectory.getDirectory(getSmallPath());
            d.clearLock("write.lock");
            d.close();

            d = FSDirectory.getDirectory(getBigPath());
            d.clearLock("write.lock");
            d.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Date getRenewDate() {
        return renewDate;
    }
}
