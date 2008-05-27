package info.xonix.zlo.search.doubleindex;

import org.apache.commons.collections.IteratorUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

import java.io.IOException;
import java.util.Iterator;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 3:51:51
 */
public class DoubleHits implements Iterable {
    protected Hits smallHits;
    protected Hits bigHits;

    public DoubleHits(Hits bigHits, Hits smallHits) {
        this.bigHits = bigHits;
        this.smallHits = smallHits;
    }

    public DoubleHits(Hits hits) {
        this(hits, null);
    }

    public int length() {
        return smallHits == null
                ? bigHits.length()
                : bigHits.length() + smallHits.length();
    }

    public Document doc(int n) throws IOException {
        if (smallHits == null)
            return bigHits.doc(n);

        int sl = smallHits.length();

        if (n < sl)
            return smallHits.doc(n);
        else
            return bigHits.doc(n - sl);
    }

    public Iterator iterator() {
        if (smallHits == null) {
            return bigHits.iterator();
        }
        return IteratorUtils.chainedIterator(bigHits.iterator(), smallHits.iterator());
    }
}
