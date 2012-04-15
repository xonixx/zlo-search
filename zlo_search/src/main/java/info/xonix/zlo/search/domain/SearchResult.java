package info.xonix.zlo.search.domain;

import info.xonix.zlo.search.index.doubleindex.DoubleHits;
import info.xonix.zlo.search.index.doubleindex.DoubleIndexManager;
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

    private String forumId;
    private Date searchDate;
    private DoubleIndexManager doubleIndexManager;

    public SearchResult(String forumId, Query query, DoubleIndexManager dis, DoubleHits doubleHits) {
        this.forumId = forumId;
        this.doubleIndexManager = dis;
        this.query = query;
        this.doubleHits = doubleHits;
        searchDate = new Date(); // now
    }

    public String getForumId() {
        return forumId;
    }

    public DoubleHits getDoubleHits() {
        return doubleHits;
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

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }

    /**
     * if creationDate is before renewing indexReader -> reader will be closed
     * or previous search limit is less then new search limit
     *
     * @param limit new search limit
     * @return old or not
     */
    public boolean isOld(int limit) {
        boolean oldByDate = searchDate.before(doubleIndexManager.getRenewDate());
        if (oldByDate) {
            log.info("Search result for " + query + " is old by index.");
        }

        boolean oldByLimit = doubleHits.oldByLimit(limit);
        if (oldByLimit) {
            log.info("Search result for " + query + " is old by limit.");
        }

        return oldByDate || oldByLimit;
    }
}
