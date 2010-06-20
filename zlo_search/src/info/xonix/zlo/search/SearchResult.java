package info.xonix.zlo.search;

import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.ZloMessageAccessor;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.displaytag.pagination.PaginatedList;

import java.util.Date;
import java.util.Iterator;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:59:08
 */
public class SearchResult implements Iterable {

    public static final Logger logger = Logger.getLogger(SearchResult.class);

    private DoubleHits doubleHits;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private QueryParser parser;
    private Query query;

    private SearchRequest lastSearch;

    private boolean newSearch = true; // by default after created
    private Date searchDate;
    private DoubleIndexSearcher doubleIndexSearcher;

    public SearchResult() {
    }

    public DoubleHits getHits() {
        return doubleHits;
    }

    public void setHits(DoubleHits doubleHits) {
        this.doubleHits = doubleHits;
    }

    public void setSearchDateNow() {
        searchDate = new Date(); // now
    }

    public void setHits(Hits hits) {
        setHits(new DoubleHits(hits));
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

/*    private class MsgsIterator implements Iterator<ZloMessageAccessor> {
        Iterator hitsIterator = doubleHits.iterator();

        public boolean hasNext() {
            return hitsIterator.hasNext();
        }

        public ZloMessageAccessor next() {
//            return ZloMessage.fromHit((Hit) hitsIterator.next());
            return new ZloMessageLazy((Hit) hitsIterator.next());
        }

        public void remove() {
        }
    }*/

    public Iterator<ZloMessageAccessor> iterator() {
//        return new MsgsIterator();
        return null;
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

    private PaginatedList paginatedList;
    public PaginatedList createPaginatedList(Site site) {
        if (paginatedList == null) {
            paginatedList = new ZloPaginatedList(doubleHits, site);
            ((ZloPaginatedList) paginatedList).setPageNumber(1);
        } else {
            ((ZloPaginatedList) paginatedList).setSite(site);
        }
        return paginatedList;
    }

    public PaginatedList getPaginatedList() {
        return paginatedList;
    }

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }

    public void setDoubleIndexSearcher(DoubleIndexSearcher dis) {
        this.doubleIndexSearcher = dis;
    }

    /**
     * if creationDate is before renewing indexReader -> reader will be closed
     * @return
     */
    public boolean isOld() {
        boolean old = searchDate.before(doubleIndexSearcher.getRenewDate());
        if (old)
            logger.info("Search result for " + query + " is old.");
        return old;
    }
}
