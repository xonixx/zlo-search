package info.xonix.zlo.search.index.doubleindex;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldDocs;

import java.io.IOException;

/**
 * User: Vovan
 * Date: 23.03.11
 * Time: 16:07
 */
public class DoubleHitsReversedIndexSortImpl extends DoubleHits {
    private final int totalHits;
    private int limit;

    public DoubleHitsReversedIndexSortImpl(TopFieldDocs topFieldDocsBig, IndexSearcher indexSearcherBig,
                                           TopFieldDocs topFieldDocsSmall, IndexSearcher indexSearcherSmall,
                                           int limit) {
        super(topFieldDocsBig, indexSearcherBig, topFieldDocsSmall, indexSearcherSmall);

        totalHits = topFieldDocsSmall.totalHits + topFieldDocsBig.totalHits;
        this.limit = limit;
    }

    @Override
    public int length() {
        return totalHits;
    }

    @Override
    public Document doc(int n) throws IOException {
        final ScoreDoc[] scoreDocsSmall = topFieldDocsSmall.scoreDocs;
        final ScoreDoc[] scoreDocsBig = topFieldDocsBig != null
                ? topFieldDocsBig.scoreDocs
                : null;

        int sl = scoreDocsSmall.length;
//        int bl = scoreDocsBig.length;

        if (n < sl) {
            return indexSearcherSmall.doc(scoreDocsSmall[n].doc);
        } else {
            if (scoreDocsBig != null) {
                return indexSearcherBig.doc(scoreDocsBig[n - sl].doc);
            } else {
                throw new IllegalStateException("n=" + n + " too big");
            }
        }
    }

    @Override
    public int getLimit() {
        return limit;
    }
}
