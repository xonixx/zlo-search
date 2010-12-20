package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domainobj.SearchRequest;
import info.xonix.zlo.search.domainobj.SearchResult;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import org.apache.lucene.search.Sort;

import java.io.IOException;

/**
 * User: Vovan
 * Date: 19.12.10
 * Time: 18:30
 */
public interface SearchLogic {
    DoubleIndexManager getDoubleIndexManager(Site site);

    void optimizeIndex(Site site);

    SearchResult search(SearchRequest searchRequest) throws SearchException;

    Sort getDateSort();

    void dropIndex(Site site) throws IOException;

    int[] search(Site site, String searchString, int skip, int limit) throws SearchException;
}
