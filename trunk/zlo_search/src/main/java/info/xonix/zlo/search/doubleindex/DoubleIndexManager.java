package info.xonix.zlo.search.doubleindex;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
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
public class DoubleIndexManager {
    private static final Logger log = Logger.getLogger(DoubleIndexManager.class);

    private final static Config config = AppSpringContext.get(Config.class);

    public static final String BIG_INDEX_DIR = "1";
    public static final String SMALL_INDEX_DIR = "2";

    private String indexesDir;

    private Sort renewingSort;

    private boolean isReopeningSmall = false;
    private boolean isReopeningBig = false;

    private long lastCreateTime = -1;

    private final Object closeLock = new Object();

    private Date renewDate;

    private IndexReader bigReader;
    private IndexReader smallReader;

//    private Directory bigDirectory;
//    private Directory smallDirectory;

    private DoubleIndexManager(String dir, Sort renewingSort) {
        this.renewingSort = renewingSort;
        this.indexesDir = dir;
        renewDate = new Date();
    }

    public DoubleIndexManager(Site site, Sort renewingSort) {
        this(site.getIndexDirDouble(), renewingSort);
    }

    private Directory dir(File dir) throws IOException {
        return FSDirectory.open(dir);
    }

    public String getIndexesDir() {
        return indexesDir;
    }

    public File getBigPath() {
        String path = indexesDir + "/" + BIG_INDEX_DIR;
        createDirIfAbsent(path);
        return new File(path);
    }

    public File getSmallPath() {
        String path = indexesDir + "/" + SMALL_INDEX_DIR;
        createDirIfAbsent(path);
        return new File(path);
    }

    public long getSmallIndexSize() {
        return getDirSize(getSmallPath());
    }

    public long getBigIndexSize() {
        return getDirSize(getBigPath());
    }

    private long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            size += file.length();
        }
        return size;
    }

    private void createDirIfAbsent(String path) {
        File f = new File(path);
        if (!f.exists()) {
            log.info("Creating dir: " + path);
            if (!f.mkdirs()) {
                log.warn("Error creating dir...");
            }
        }
    }

    public IndexReader getBigReader() {
        if (bigReader == null) {
            final File bigPath = getBigPath();
            try {
                bigReader = IndexReader.open(dir(bigPath), true);
            } catch (IOException e) {
                log.error("Can't create bigReader... Creating empty one...");
                try {
                    createEmptyIndex(bigPath);
                    bigReader = IndexReader.open(dir(bigPath), true);
                } catch (IOException e1) {
                    log.error("Can't create empty big reader: ", e1);
                }
            }
        } else {
            startRecreatingReaderIfNeeded(bigReader);
        }
        return bigReader;
    }

    public IndexReader getSmallReader() {
        if (smallReader == null) {
            final File smallPath = getSmallPath();
            try {
                smallReader = IndexReader.open(dir(smallPath), true);
                renewDate = new Date();
            } catch (IOException e) {
                log.error("Can't create smallReader... Creating empty one...");
                try {
                    createEmptyIndex(smallPath);
                    smallReader = IndexReader.open(dir(smallPath), true);
                } catch (IOException e1) {
                    log.error("Can't create empty small reader: ", e1);
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

    private void startRecreatingReaderIfNeeded(IndexReader r) {
        if (needToRecreateReader(r)) {
            synchronized (this) {
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
                        && System.currentTimeMillis() - lastCreateTime > config.getPeriodRecreateIndexer();
            else // big
                return !bigReader.isCurrent()
                        && !isReopeningBig;
        } catch (IOException e) {
            log.error(e);
            return true;
        }
    }

    private void performReopen(IndexReader r) {
        boolean isSmall = r == smallReader;
        log.debug(MessageFormat.format("Start recreating {0} indexReader...", isSmall ? "small" : "big"));
        if (isSmall)
            isReopeningSmall = true;
        else
            isReopeningBig = true;

        IndexReader ir = null,
                oldIndexReader = isSmall ? smallReader : bigReader;

        try {
            ir = IndexReader.open(
                    dir(isSmall
                            ? getSmallPath()
                            : getBigPath()), true);
        } catch (IOException e) {
            log.error("Error while recreating index reader: " + e);
        }

        // search to form memory caches
        IndexSearcher is = null;
        try {
            is = new IndexSearcher(ir);
            is.search(new MatchAllDocsQuery(), renewingSort);
        } catch (IOException e) {
            log.error("Problems recreating smallReader: ", e);
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

        synchronized (closeLock) { // to give ability to perform searh if oldReader already used
            clean(oldIndexReader);
        }

        log.info("Successfuly recreated.");

        if (isSmall)
            isReopeningSmall = false;
        else
            isReopeningBig = false;

        renewDate = new Date();
    }

    // TODO: thread pool?

    private void startReopeningThread(final IndexReader r) {

        final Thread t = new Thread(new Runnable() {
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
            log.error("Error while closing index reader: " + e.getClass());
        }
    }

    public DoubleHits search(Query query) throws IOException {
        return new DoubleHits1( //TODO: optimize - lazy search big index
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
        log.info("Start moving small to big...");

        IndexWriter bigIndexWriter = new IndexWriter(dir(getBigPath()), config.getMessageAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        IndexReader smlR = getSmallReader();

        log.info("Moving small to big...");
        bigIndexWriter.addIndexesNoOptimize(new Directory[]{dir(getSmallPath())}); // add small to big, w/o optimize

        smlR.close();
        smallReader = null;
        bigIndexWriter.close();

        log.info("Cleaning small index...");
        createEmptyIndex(getSmallPath()); // empty small index
        log.info("Done moving small to big.");
    }

    public void optimize() throws IOException {
        log.info("Optimizing...");

        for (File path : new File[]{getBigPath(), getSmallPath()}) {
            log.info("Optimizing: " + path.getAbsolutePath());

            IndexWriter iw = new IndexWriter(dir(path), config.getMessageAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            iw.optimize();
            iw.close();
        }

        log.info("Done.");
    }

    private void createEmptyIndex(File path) throws IOException {
        final IndexWriter indexWriter = new IndexWriter(dir(path), config.getMessageAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
        indexWriter.setUseCompoundFile(true);
        indexWriter.close();
    }

    public void clearLocks() {
        try {
            for (File path : new File[]{getSmallPath(), getBigPath()}) {
                log.info("Clearing lock: " + path.getAbsolutePath());

                final Directory d = dir(path);
                d.clearLock("write.lock");
                d.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Date getRenewDate() {
        return renewDate;
    }
}