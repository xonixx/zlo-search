package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domain.SearchRequest;
import info.xonix.zlo.search.domain.SearchResult;
import info.xonix.zlo.search.index.IndexManager;
import info.xonix.zlo.search.index.doubleindex.DoubleIndexManager;
import org.apache.lucene.search.Sort;

import java.io.IOException;

/**
 * User: Vovan
 * Date: 19.12.10
 * Time: 18:30
 */
public interface SearchLogic {
    void optimizeIndex(String forumId);

    SearchResult search(SearchRequest searchRequest, int limit) throws SearchException;

    Sort getDateSort();

    void dropIndex(String forumId) throws IOException;

    int[] search(String forumId, String searchString, int skip, int limit) throws SearchException;
}
