package info.xonix.zlo.search.model;

import info.xonix.zlo.search.ZloPaginatedList;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.displaytag.pagination.PaginatedList;

import java.util.Date;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:59:08
 */
public class SearchResult {

    public static final Logger log = Logger.getLogger(SearchResult.class);

    private DoubleHits doubleHits;
    private Query query;

    private SearchRequest lastSearch;

    private boolean newSearch = true; // by default after created
    private Date searchDate;
    private DoubleIndexSearcher doubleIndexSearcher;

    public SearchResult() {
    }

/*    public DoubleHits getHits() {
        return doubleHits;
    }*/

    public void setHits(DoubleHits doubleHits) {
        this.doubleHits = doubleHits;
    }

    public void setSearchDateNow() {
        searchDate = new Date(); // now
    }

/*    public void setHits(Hits hits) {
        setHits(new DoubleHits(hits));
    }*/

    public void setQuery(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
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

    private ZloPaginatedList paginatedList;

    public PaginatedList createPaginatedList(Site site) {
        if (paginatedList == null) {
            paginatedList = new ZloPaginatedList(doubleHits, site);
            paginatedList.setPageNumber(1);
        } else {
            // TODO: ???
            paginatedList.setSite(site);
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
     *
     * @return
     */
    public boolean isOld() {
        boolean old = searchDate.before(doubleIndexSearcher.getRenewDate());
        if (old)
            log.info("Search result for " + query + " is old.");
        return old;
    }
}
