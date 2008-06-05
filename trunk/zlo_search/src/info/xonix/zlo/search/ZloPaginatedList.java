package info.xonix.zlo.search;

import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.site.SiteSource;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.io.IOException;
import java.util.List;

/**
 * Author: Vovan
 * Date: 26.09.2007
 * Time: 16:39:25
 */
public class ZloPaginatedList extends SiteSource implements PaginatedList {
    private List currentList;
    private int pageNumber;
    private int objectsPerPage;
    private DoubleHits hits;

    private static final Logger logger = Logger.getLogger(ZloPaginatedList.class);

    public ZloPaginatedList(DoubleHits hits, Site site) {
        super(site);
        this.hits = hits;
    }

    public List getList() {
        return currentList;
    }

    public List<ZloMessage> subList(int fromIndex, int toIndex) throws DbException {
        if (fromIndex == toIndex)
            return null;

        int[] indexes = new int[toIndex - fromIndex];
        try {
            for (int i=fromIndex; i<toIndex; i++) {
                indexes[i-fromIndex] = Integer.parseInt(hits.doc(i).get(ZloMessage.FIELDS.URL_NUM));
            }
        } catch (IOException e) {
            logger.error("Error while getting doc from index: " + e);
        }

        return getSite().getDbManager().getMessages(indexes, fromIndex);
    }

    public void refreshCurrentList() throws DbException {
        int from = (pageNumber - 1) * objectsPerPage;
        currentList = subList(from, Math.min(from + objectsPerPage, getFullListSize()));
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getObjectsPerPage() {
        return objectsPerPage;
    }

    public void setObjectsPerPage(int pageSize) {
        this.objectsPerPage = pageSize;
    }

    public int getFullListSize() {
        return hits.length();
    }

    public String getSortCriterion() {
        return null;
    }

    public SortOrderEnum getSortDirection() {
        return null;
    }

    public String getSearchId() {
        return null;
    }
}
