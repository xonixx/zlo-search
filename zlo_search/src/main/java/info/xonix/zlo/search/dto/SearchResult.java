package info.xonix.zlo.search.dto;

import info.xonix.zlo.search.index.Hits;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;

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

    private String forumId;

    public SearchResult(String forumId, Query query, Hits hits) {
        this.forumId = forumId;
        this.query = query;
        this.hits = hits;
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
}
