package org.xonix.zlo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.Iterator;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:59:08
 */
public class ZloSearchResult implements Iterable {

    private Hits hits;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private QueryParser parser;
    private Query query;

    public ZloSearchResult() {
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    public void setSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void setQueryParser(QueryParser parser) {
        this.parser = parser;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public IndexSearcher getSearcher() {
        return searcher;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public QueryParser getParser() {
        return parser;
    }

    public Query getQuery() {
        return query;
    }

    private class MsgsIterator implements Iterator<ZloMessage> {
        Iterator hitsIterator = hits.iterator();

        public boolean hasNext() {
            return hitsIterator.hasNext();
        }

        public ZloMessage next() {
            try {
                return ZloMessage.fromDocument(((Hit) hitsIterator.next()).getDocument());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void remove() {
        }
    }

    public Iterator<ZloMessage> iterator() {
        return new MsgsIterator();
    }
}
