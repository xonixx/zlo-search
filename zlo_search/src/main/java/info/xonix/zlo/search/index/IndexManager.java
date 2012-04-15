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
        final IndexReader reader = getReader();
//        final IndexReader bigReader = getBigReader();
//        final IndexReader smallReader = getSmallReader();

        // TODO: optimize - lazy search big index
        // TODO: optimize numDocs
        final IndexSearcher indexSearcher = new IndexSearcher(reader);
//        final IndexSearcher indexSearcherBig = new IndexSearcher(bigReader);
//        final IndexSearcher indexSearcherSmall = new IndexSearcher(smallReader);

        final TopFieldDocs topFieldDocs;
//        final TopFieldDocs topFieldDocsBig;
//        final TopFieldDocs topFieldDocsSmall;

        if (limit < 0) {
            throw new IllegalArgumentException("limit = " + limit + " < 0"); // TODO
/*            final Sort sort = Sort.INDEXORDER;

            topFieldDocsSmall = indexSearcherSmall.search(query, null, smallReader.numDocs(), sort);
            topFieldDocsBig = indexSearcherBig.search(query, null, bigReader.numDocs(), sort);

            return new DoubleHitsImpl(
                    topFieldDocsBig, indexSearcherBig,
                    topFieldDocsSmall, indexSearcherSmall);*/
        } else {
            final Sort reversedIndexOrderSort = SearchLogicImpl.REVERSED_INDEX_ORDER_SORT;

            topFieldDocs = indexSearcher.search(query, null, limit, reversedIndexOrderSort);
//            topFieldDocsSmall = indexSearcherSmall.search(query, null, limit, reversedIndexOrderSort);

            /*int limitBig = limit - topFieldDocsSmall.scoreDocs.length;
            if (limitBig == 0) {
                limitBig = 1; // we have to search anyway to get totalHits
            }
            topFieldDocsBig = indexSearcherBig.search(query, null, limitBig, reversedIndexOrderSort);

            return new DoubleHitsReversedIndexSortImpl(
                    topFieldDocsBig, indexSearcherBig,
                    topFieldDocsSmall, indexSearcherSmall,
                    limit);*/

            return new Hits(topFieldDocs, indexSearcher);// TODO
        }
    }

    public IndexReader getReader() {
        return null; // TODO
    }

    public IndexWriter createWriter() throws IOException {
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                LuceneVersion.VERSION, config.getMessageAnalyzer());

        return new IndexWriter(IndexUtils.dir(indexDir), indexWriterConfig);
    }

    public void drop() {
//       IndexUtils.createEmptyIndex();
    }

    public void close() {
        IndexUtils.clean(getReader());
    }
}
