package info.xonix.zlo.search.index.doubleindex;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldDocs;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 3:51:51
 */
public abstract class DoubleHits {
    protected TopFieldDocs topFieldDocsBig;
    protected IndexSearcher indexSearcherBig;
    protected TopFieldDocs topFieldDocsSmall;
    protected IndexSearcher indexSearcherSmall;

    public DoubleHits(TopFieldDocs topFieldDocsBig, IndexSearcher indexSearcherBig, TopFieldDocs topFieldDocsSmall, IndexSearcher indexSearcherSmall) {
        this.topFieldDocsBig = topFieldDocsBig;
        this.indexSearcherBig = indexSearcherBig;
        this.topFieldDocsSmall = topFieldDocsSmall;
        this.indexSearcherSmall = indexSearcherSmall;
    }

    public int length() {
        return topFieldDocsSmall.scoreDocs.length + topFieldDocsBig.scoreDocs.length;
    }

    public abstract Document doc(int n) throws IOException;

    public abstract int getLimit();

    public boolean oldByLimit(int limit) {
        final int myLimit = getLimit();

        if (myLimit < 0) {
            return false;
        }

        return limit > myLimit;
    }
}
