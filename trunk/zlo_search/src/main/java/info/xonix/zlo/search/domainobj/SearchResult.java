package info.xonix.zlo.search.domainobj;

import info.xonix.zlo.search.ZloPaginatedList;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;

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

    private Site site;
    private Date searchDate;
    private DoubleIndexManager doubleIndexManager;

    public SearchResult(Site site, Query query, DoubleIndexManager dis, DoubleHits doubleHits) {
        this.site = site;
        this.doubleIndexManager = dis;
        this.query = query;
        this.doubleHits = doubleHits;
        searchDate = new Date(); // now
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

    // paginated list can't be cached as it should not be shared among users

    public ZloPaginatedList createPaginatedList() {
        return new ZloPaginatedList(doubleHits, site);
    }

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }

    /**
     * if creationDate is before renewing indexReader -> reader will be closed
     *
     * @return
     */
    public boolean isOld() {
        boolean old = searchDate.before(doubleIndexManager.getRenewDate());
        if (old)
            log.info("Search result for " + query + " is old.");
        return old;
    }
}
