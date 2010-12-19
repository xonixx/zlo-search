package info.xonix.zlo.search;

import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageFields;
import info.xonix.zlo.search.spring.AppSpringContext;
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
public class ZloPaginatedList implements PaginatedList {
    private static final Logger log = Logger.getLogger(ZloPaginatedList.class);

    private List currentList;
    private int pageNumber;
    private int objectsPerPage;
    private DoubleHits hits;

    private Site site;
    private MessagesDao messagesDao = AppSpringContext.get(MessagesDao.class);

    public ZloPaginatedList(DoubleHits hits, Site site) {
        this.site = site;
        this.hits = hits;
    }

    public List getList() {
        return currentList;
    }

    public List<Message> subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex)
            return null;

        int[] indexes = new int[toIndex - fromIndex];
        try {
            for (int i = fromIndex; i < toIndex; i++) {
                indexes[i - fromIndex] = Integer.parseInt(hits.doc(i).get(MessageFields.URL_NUM));
            }
        } catch (IOException e) {
            log.error("Error while getting doc from index: " + e, e);
        }

        List<Message> messages = messagesDao.getMessages(site, indexes);

        for (Message message : messages) {
            message.setHitId(fromIndex++);
        }

        return messages;
    }

    public void refreshCurrentList() {
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
