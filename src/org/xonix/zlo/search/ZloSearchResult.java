package org.xonix.zlo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.displaytag.pagination.PaginatedList;
import org.xonix.zlo.search.model.ZloMessageAccessor;
import org.xonix.zlo.search.model.ZloMessageLazy;
import org.xonix.zlo.web.ZloPaginatedList;

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

    private SearchRequest lastSearch;

    private PaginatedList paginatedList;

    private boolean newSearch = true; // by default after created

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

    private class MsgsIterator implements Iterator<ZloMessageAccessor> {
        Iterator hitsIterator = hits.iterator();

        public boolean hasNext() {
            return hitsIterator.hasNext();
        }

        public ZloMessageAccessor next() {
//            return ZloMessage.fromHit((Hit) hitsIterator.next());
            return new ZloMessageLazy((Hit) hitsIterator.next());
        }

        public void remove() {
        }
    }

    public Iterator<ZloMessageAccessor> iterator() {
        return new MsgsIterator();
    }

    public SearchRequest getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(SearchRequest lastSearch) {
        this.lastSearch = lastSearch;
    }

    public boolean isTheSameSearch(SearchRequest searchRequest) {
        return searchRequest.isTheSameSearch(lastSearch);
    }

    public boolean isNotTheSameSearch(SearchRequest searchRequest) {
        return !isTheSameSearch(searchRequest);
    }

    public PaginatedList getPaginatedList() {
        if (paginatedList == null)
            paginatedList = ZloPaginatedList.fromZloSearchResult(this);
        return paginatedList;
    }

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }
}
