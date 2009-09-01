package info.xonix.zlo.search.doubleindex;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 27.05.2008
 * Time: 21:01:23
 */
public class DoubleHits1 extends DoubleHits {
    public DoubleHits1(Hits bigHits, Hits smallHits) {
        super(bigHits, smallHits);
    }

    public DoubleHits1(Hits hits) {
        super(hits);
    }

    public Document doc(int n) throws IOException {
         if (smallHits == null)
            return bigHits.doc(bigHits.length() - 1 - n);

        int sl = smallHits.length();

        if (n < sl)
            return smallHits.doc(smallHits.length() - 1 - n);
        else
            return bigHits.doc(bigHits.length() - 1 - (n - sl));
    }
}
