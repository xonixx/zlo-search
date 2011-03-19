package info.xonix.zlo.search.doubleindex;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldDocs;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 3:51:51
 */
public abstract class DoubleHits/* implements Iterable*/ {
    //    protected Hits smallHits;
//    protected Hits bigHits;
    protected TopFieldDocs topFieldDocsBig;
    protected IndexSearcher indexSearcherBig;
    protected TopFieldDocs topFieldDocsSmall;
    protected IndexSearcher indexSearcherSmall;

/*    public DoubleHits(Hits bigHits, Hits smallHits) {
        this.bigHits = bigHits;
        this.smallHits = smallHits;
    }

    public DoubleHits(Hits hits) {
        this(hits, null);
    }*/

    public DoubleHits(TopFieldDocs topFieldDocsBig, IndexSearcher indexSearcherBig, TopFieldDocs topFieldDocsSmall, IndexSearcher indexSearcherSmall) {
        this.topFieldDocsBig = topFieldDocsBig;
        this.indexSearcherBig = indexSearcherBig;
        this.topFieldDocsSmall = topFieldDocsSmall;
        this.indexSearcherSmall = indexSearcherSmall;
    }

    public int length() {
/*        return smallHits == null
                ? bigHits.length()
                : bigHits.length() + smallHits.length();*/
        return topFieldDocsSmall.scoreDocs.length + topFieldDocsBig.scoreDocs.length;
    }

    public abstract Document doc(int n) throws IOException;/* {
        if (smallHits == null)
            return bigHits.doc(n);

        int sl = smallHits.length();

        if (n < sl)
            return smallHits.doc(n);
        else
            return bigHits.doc(n - sl);
    }*/

/*    public Iterator iterator() {
        if (smallHits == null) {
            return bigHits.iterator();
        }
        return IteratorUtils.chainedIterator(bigHits.iterator(), smallHits.iterator());
    }*/
}
