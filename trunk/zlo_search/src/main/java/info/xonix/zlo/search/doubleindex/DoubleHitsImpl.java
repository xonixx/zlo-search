package info.xonix.zlo.search.doubleindex;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldDocs;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 27.05.2008
 * Time: 21:01:23
 *
 * @deprecated from now on
 */
@Deprecated
public class DoubleHitsImpl extends DoubleHits {
    public DoubleHitsImpl(TopFieldDocs topFieldDocsBig, IndexSearcher indexSearcherBig, TopFieldDocs topFieldDocsSmall, IndexSearcher indexSearcherSmall) {
        super(topFieldDocsBig, indexSearcherBig, topFieldDocsSmall, indexSearcherSmall);
    }

    public Document doc(int n) throws IOException {
        final ScoreDoc[] scoreDocsSmall = topFieldDocsSmall.scoreDocs;
        final ScoreDoc[] scoreDocsBig = topFieldDocsBig.scoreDocs;

        int sl = scoreDocsSmall.length;
        int bl = scoreDocsBig.length;

        if (n < sl) {
            return indexSearcherSmall.doc(scoreDocsSmall[sl - 1 - n].doc);
        } else {
            return indexSearcherBig.doc(scoreDocsBig[bl - 1 - (n - sl)].doc);
        }
    }

    @Override
    public int getLimit() {
        return -1; // no limit
    }
}
