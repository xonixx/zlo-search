package info.xonix.zlo.web.servlets.test;

import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.apache.commons.lang.StringUtils;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Vovan
 * Date: 23.09.2007
 * Time: 22:46:28
 */
public class TestLazy1 extends ForwardingServlet {

    private List list;

    public static class Val {
        private int field;

        public Val(int field) {
            this.field = field;
        }

        public int getField() {
            return field;
        }

        public void setField(int field) {
            this.field = field;
        }
    }

    private class PaginatedListImpl1 implements PaginatedList {
        private int pageNumber = 1;
        public List getList() {
            int from = (pageNumber-1) * getObjectsPerPage();
            return list.subList(from, from + getObjectsPerPage());
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getObjectsPerPage() {
            return 2;
        }

        public int getFullListSize() {
            return list.size();
        }

        public String getSortCriterion() {
            return null;
        }

        public SortOrderEnum getSortDirection() {
            return null;
        }

        public String getSearchId() {
            return "s-id";
        }
    }

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        Val [] res = new Val[10];

        for (int i = 0; i < res.length; i++) {
            res[i] = new Val(i);
        }

//        PaginatedListImpl paginatedList = new PaginatedListImpl(request, 2);
//        paginatedList.setList(Arrays.asList(res));
//        paginatedList.setIndex(4);
//        request.setAttribute("res", paginatedList);
        list = Arrays.asList(res);
        PaginatedListImpl1 listImpl1 = new PaginatedListImpl1();
//        request.setAttribute("res", listImpl1);
        request.setAttribute("res", res);

        if (StringUtils.isNotEmpty(request.getParameter("page"))) {
            int page = Integer.parseInt(request.getParameter("page"));
            listImpl1.setPageNumber(page);

        }

        request.forwardTo("/test2.jsp");

    }
}
