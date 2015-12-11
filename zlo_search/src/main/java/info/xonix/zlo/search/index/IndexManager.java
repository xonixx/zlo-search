package info.xonix.zlo.search.index;

import info.xonix.utils.factory.StringFactory;
import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.SortBy;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.LuceneUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 21:48
 */
public class IndexManager {
    private final static Logger log = Logger.getLogger(IndexManager.class);

    private final static Config config = AppSpringContext.get(Config.class);

    public static final String WRITE_LOCK_FILE = "write.lock";

    private final File indexDir;
    private IndexReader indexReader;
    private IndexWriter indexWriter;
    private IndexSearcher indexSearcher;

    private static StringFactory<IndexManager> indexManagerFactory = new StringFactory<IndexManager>() {
        @Override
        protected IndexManager create(String forumId) {
            return new IndexManager(config.getIndexDir(forumId));
        }
    };

    private IndexManager(String indexDirPath) {
        indexDir = new File(indexDirPath);

        if (!indexDir.exists()) {
            log.info(indexDirPath + " not exists. Creating...");

            if (!indexDir.mkdirs()) {
                throw new RuntimeException("Failed to create folder: " + indexDirPath);
            }
        }
    }

    public static IndexManager get(String forumId) {
        return indexManagerFactory.get(forumId);
    }

    public static Collection<IndexManager> all() {
        return Collections.unmodifiableCollection(indexManagerFactory.all());
    }

    public Hits search(Query query, SortBy sortDirection, int limit) throws IOException {
        final IndexSearcher indexSearcher = getSearcher();
        final TopFieldDocs topFieldDocs;

        if (limit < 0) {
            throw new IllegalArgumentException("limit = " + limit + " < 0"); // TODO
        } else {
            topFieldDocs = indexSearcher.search(query, null, limit, sortDirection.getSort());

            return new Hits(topFieldDocs, indexSearcher);
        }
    }

    @SuppressWarnings("unused")
    public File getIndexDir() {
        return indexDir;
    }

    public IndexReader getReader() throws IOException {
        if (indexReader == null) {
            indexReader = DirectoryReader.open(getWriter(), true);
        } else {
            final IndexReader updatedReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader, getWriter(), true);
            if (updatedReader != null) {
                indexReader = updatedReader;
            }
        }

        return indexReader;
    }

    private final Object indexWriterLock = new Object();

    public IndexWriter getWriter() throws IOException {
        synchronized (indexWriterLock) {
            if (indexWriter == null) {
                indexWriter = createWriter();
            }
            return indexWriter;
        }
    }

    private IndexWriter createWriter() throws IOException {
        // only this mergepolicy preserves the order of docIds
        // see:
        // http://mail-archives.apache.org/mod_mbox/lucene-java-user/201204.mbox/%3CCADuAvzefiry%2Bch1H%2BLoVcbepfG59HCFbYYPHxcQ0a%2BZmV5jr1A%40mail.gmail.com%3E
        // http://mail-archives.apache.org/mod_mbox/lucene-java-user/201204.mbox/%3CCAL8PwkaTs8qQzmoCYPzRLHVTwbf%2Bb0Qt32g2vQYG%2B%2BMTAFoZ4Q%40mail.gmail.com%3E
        final LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
        mergePolicy.setMergeFactor(3); // aggressively merge - improve search speed

        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                LuceneVersion.VERSION, config.getMessageAnalyzer())
                .setMergePolicy(mergePolicy);

        return new IndexWriter(LuceneUtils.dir(indexDir), indexWriterConfig);
    }

    public IndexSearcher getSearcher() throws IOException {
        if (indexSearcher == null) {
            indexSearcher = new IndexSearcher(getReader());
        } else {
            IndexReader currentReader = getReader();

            if (indexSearcher.getIndexReader() != currentReader) {
                indexSearcher = new IndexSearcher(currentReader);
            }
        }
        return indexSearcher;
    }

    public void drop() throws IOException {
        synchronized (indexWriterLock) {
            if (indexWriter != null) {
                indexWriter.close();
                indexWriter = null;
            }

            LuceneUtils.createEmptyIndex(indexDir);
        }
    }

    public void clearLocks() {
        try {
            log.info("Clearing lock: " + indexDir.getAbsolutePath());

            final Directory d = LuceneUtils.dir(indexDir);
            d.clearLock(WRITE_LOCK_FILE);
            d.close();
        } catch (IOException e) {
            log.error("Error clearing lock", e);
        }
    }

    @SuppressWarnings("unused")
    public long getIndexSize() {
        return LuceneUtils.getDirSize(indexDir);
    }
}
