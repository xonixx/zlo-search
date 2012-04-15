package info.xonix.zlo.search.domain;

import info.xonix.zlo.search.index.Hits;
import info.xonix.zlo.search.index.IndexManager;
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

    private Hits hits;
    private Query query;

    private SearchRequest lastSearch;

    private boolean newSearch = true; // by default after created

    private String forumId;
    private Date searchDate;
    private IndexManager indexManager;

    public SearchResult(String forumId, Query query, IndexManager indexManager, Hits hits) {
        this.forumId = forumId;
        this.indexManager = indexManager;
        this.query = query;
        this.hits = hits;
        searchDate = new Date(); // now
    }

    public String getForumId() {
        return forumId;
    }

    public Hits getHits() {
        return hits;
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
        throw new UnsupportedOperationException("isOld");

/*        boolean oldByDate = searchDate.before(indexManager.getRenewDate());
        if (oldByDate) {
            log.info("Search result for " + query + " is old by index.");
        }

        boolean oldByLimit = hits.oldByLimit(limit);
        if (oldByLimit) {
            log.info("Search result for " + query + " is old by limit.");
        }

        return oldByDate || oldByLimit;*/
    }
}
