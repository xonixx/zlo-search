package info.xonix.zlo.search.index;

import info.xonix.utils.factory.StringFactory;
import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.SearchLogicImpl;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
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

    private static StringFactory<IndexManager> indexManagerFactory = new StringFactory<IndexManager>() {
        @Override
        protected IndexManager create(String forumId) {
            return new IndexManager(config.getIndexDir(forumId));
        }
    };

    private IndexManager(String indexDirPath) {
        // TODO: check indexDir to exist & be folder
        indexDir = new File(indexDirPath);
    }

    public static IndexManager get(String forumId) {
        return indexManagerFactory.get(forumId);
    }

    public static Collection<IndexManager> all() {
        return Collections.unmodifiableCollection(indexManagerFactory.all());
    }

    public Hits search(Query query, int limit) throws IOException {
        final IndexSearcher indexSearcher = getSearcher();
        final TopFieldDocs topFieldDocs;

        if (limit < 0) {
            throw new IllegalArgumentException("limit = " + limit + " < 0"); // TODO
        } else {
            final Sort reversedIndexOrderSort = SearchLogicImpl.REVERSED_INDEX_ORDER_SORT;

            topFieldDocs = indexSearcher.search(query, null, limit, reversedIndexOrderSort);

            return new Hits(topFieldDocs, indexSearcher);
        }
    }

    public File getIndexDir() {
        return indexDir;
    }

    public IndexReader getReader() throws IOException {
        if (indexReader == null) {
            indexReader = IndexReader.open(getWriter(), true);
        } else {
            final IndexReader updatedReader = IndexReader.openIfChanged(indexReader, getWriter(), true);
            if (updatedReader != null) {
                indexReader = updatedReader;
            }
        }

        return indexReader;
    }

    public IndexWriter getWriter() throws IOException {
        if (indexWriter == null) {
            indexWriter = createWriter();
        }

        return indexWriter;
    }

    private IndexWriter createWriter() throws IOException {
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                LuceneVersion.VERSION, config.getMessageAnalyzer());

        return new IndexWriter(IndexUtils.dir(indexDir), indexWriterConfig);
    }

    public IndexSearcher getSearcher() throws IOException {
        return new IndexSearcher(getReader());
    }

    public void drop() throws IOException {
        IndexUtils.createEmptyIndex(indexDir);
    }

/*    public void close() throws IOException {
        IndexUtils.close(getReader());
    }*/

    public void clearLocks() {
        try {
            log.info("Clearing lock: " + indexDir.getAbsolutePath());

            final Directory d = IndexUtils.dir(indexDir);
            d.clearLock(WRITE_LOCK_FILE);
            d.close();
        } catch (IOException e) {
            log.error("Error clearing lock", e);
        }
    }
}
