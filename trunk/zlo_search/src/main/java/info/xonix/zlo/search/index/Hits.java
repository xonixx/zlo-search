package info.xonix.zlo.search.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldDocs;

import java.io.IOException;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 22:11
 */
public class Hits {
    private TopFieldDocs topFieldDocs;
    private IndexSearcher indexSearcher;

    public Hits(TopFieldDocs topFieldDocs, IndexSearcher indexSearcher) {
        this.topFieldDocs = topFieldDocs;
        this.indexSearcher = indexSearcher;
    }

    public Document doc(int i) throws IOException {
        return indexSearcher.doc(topFieldDocs.scoreDocs[i].doc);
    }

    public int length() {
        return topFieldDocs.totalHits;
    }
}
