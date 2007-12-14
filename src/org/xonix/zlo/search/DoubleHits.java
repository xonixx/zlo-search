package org.xonix.zlo.search;

import org.apache.lucene.search.Hits;
import org.apache.lucene.document.Document;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 3:51:51
 */
public class DoubleHits {
    private Hits smallHits;
    private Hits bigHits;

    public DoubleHits(Hits bigHits, Hits smallHits) {
        this.bigHits = bigHits;
        this.smallHits = smallHits;
    }

    public int length() {
        return bigHits.length() + smallHits.length();
    }

    public Document doc(int n) throws IOException {
        int bl = bigHits.length();

        if (n <= bl)
            return bigHits.doc(n);
        else
            return smallHits.doc(n - bl);
    }
}
