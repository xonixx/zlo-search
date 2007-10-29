package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Hits;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Author: Vovan
 * Date: 26.09.2007
 * Time: 16:39:25
 */
public class ZloPaginatedList implements PaginatedList {
    private List fullList;
    private int pageNumber;
    private int objectsPerPage;

    private static final Logger logger = Logger.getLogger(ZloPaginatedList.class);

    private static class HitsToMessagesList implements List<ZloMessage> {

        private Hits hits;


        public HitsToMessagesList(Hits hits) {
            this.hits = hits;
        }

        public ZloMessage get(int index) {
            try {
                return ZloMessage.fromDocument(hits.doc(index), index);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public int size() {
            return hits.length();
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        public List<ZloMessage> subList(int fromIndex, int toIndex) {
            int[] indexes = new int[toIndex - fromIndex];
            try {
                for (int i=fromIndex; i<toIndex; i++) {
                    indexes[i-fromIndex] = Integer.parseInt(hits.doc(i).get(ZloMessage.URL_NUM));
                }
            } catch (IOException e) {
                logger.error("Error while getting doc from index: " + e);
            }

            try {
                return DbManager.getMessages(indexes, fromIndex);
            } catch (DbException e) {
                logger.error("DBexception while getting msgs from DB: " + e);
                throw new RuntimeException(e);
            }
        }

        // затычки ---VVV
        public boolean contains(Object o) {
            return false;
        }

        public Iterator<ZloMessage> iterator() {
            return null;
        }

        public Object[] toArray() {
            return new Object[0];
        }

        public <T> T[] toArray(T[] a) {
            return null;
        }

        public boolean add(ZloMessage zloMessage) {
            return false;
        }

        public boolean remove(Object o) {
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return false;
        }

        public boolean addAll(Collection<? extends ZloMessage> c) {
            return false;
        }

        public boolean addAll(int index, Collection<? extends ZloMessage> c) {
            return false;
        }

        public boolean removeAll(Collection<?> c) {
            return false;
        }

        public boolean retainAll(Collection<?> c) {
            return false;
        }

        public void clear() {
        }

        public ZloMessage set(int index, ZloMessage element) {
            return null;
        }

        public void add(int index, ZloMessage element) {
        }

        public ZloMessage remove(int index) {
            return null;
        }

        public int indexOf(Object o) {
            return 0;
        }

        public int lastIndexOf(Object o) {
            return 0;
        }

        public ListIterator<ZloMessage> listIterator() {
            return null;
        }

        public ListIterator<ZloMessage> listIterator(int index) {
            return null;
        }
    }

    public static PaginatedList fromZloSearchResult(ZloSearchResult zloResult) {
        final Hits hits = zloResult.getHits();
        ZloPaginatedList zpl = new ZloPaginatedList(new HitsToMessagesList(hits));
        zpl.setPageNumber(1); // initially 1 page
        return zpl;
    }

    public ZloPaginatedList(List fullList) {
        this.fullList = fullList;
    }

    public List getFullList() {
        return fullList;
    }

    public void setFullList(List fullList) {
        this.fullList = fullList;
    }

    public List getList() {
        int from = (pageNumber - 1) * objectsPerPage;
        return fullList.subList(from, Math.min(from + objectsPerPage, getFullListSize()));
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
        return fullList.size();
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
