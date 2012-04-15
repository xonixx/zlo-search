package info.xonix.zlo.search.index;

import info.xonix.utils.factory.StringFactory;
import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.SearchLogicImpl;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;

import java.io.File;
import java.io.IOException;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 21:48
 */
public class IndexManager {

    private final static Config config = AppSpringContext.get(Config.class);

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

    public Hits search(Query query, int limit) throws IOException {
        final IndexSearcher indexSearcher = getSearcher();
        final TopFieldDocs topFieldDocs;

        if (limit < 0) {
            throw new IllegalArgumentException("limit = " + limit + " < 0"); // TODO
        } else {
            final Sort reversedIndexOrderSort = SearchLogicImpl.REVERSED_INDEX_ORDER_SORT;

            topFieldDocs = indexSearcher.search(query, null, limit, reversedIndexOrderSort);

            return new Hits(topFieldDocs, indexSearcher);// TODO
        }
    }

    public IndexReader getReader() throws IOException {
        if (indexReader == null) {
            return IndexReader.open(indexWriter, true);
        } else {
            return IndexReader.openIfChanged(indexReader);
        }
    }

    public IndexWriter createWriter() throws IOException {
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                LuceneVersion.VERSION, config.getMessageAnalyzer());

        indexWriter = new IndexWriter(IndexUtils.dir(indexDir), indexWriterConfig);

        return indexWriter;
    }

    public IndexSearcher getSearcher() throws IOException {
        return new IndexSearcher(getReader());
    }

    public void drop() throws IOException {
       IndexUtils.createEmptyIndex(indexDir);
    }

    public void close() throws IOException {
        IndexUtils.close(getReader());
    }
}
