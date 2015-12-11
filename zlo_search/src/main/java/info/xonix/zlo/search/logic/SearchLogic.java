package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dto.SearchRequest;
import info.xonix.zlo.search.dto.SearchResult;

import java.io.IOException;
import java.util.List;

/**
 * User: Vovan
 * Date: 19.12.10
 * Time: 18:30
 */
public interface SearchLogic {
    SearchResult search(SearchRequest searchRequest, int limit) throws SearchException;

    void dropIndex(String forumId) throws IOException;

    int[] search(String forumId, String searchString, int skip, int limit) throws SearchException;

    /**
     * Analyzes by configured message analyzer
     * @param text text to analyze
     * @return list of tokens
     */
    List<String> tokenize(String text);
}
