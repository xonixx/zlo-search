package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:31:41
 */
public class SearchServlet extends ForwardingServlet {

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryString = formQueryString(request);

        if (StringUtils.isNotEmpty(queryString)){
            request.setAttribute("searchResult", ZloSearcher.search(queryString));
        }

        request.setAttribute("debug", "true".equalsIgnoreCase(getServletContext().getInitParameter("debug")));
        request.setAttribute("siteRoot", "zlo.rt.mipt.ru");
        //request.setAttribute("error", "Fuck!!!");

        response.setCharacterEncoding("UTF-8");
        request.forwardTo("/Search.jsp");
    }

    private String formQueryString(ForwardingRequest request) {
        StringBuffer res = new StringBuffer();

        if (StringUtils.isNotEmpty(request.getParameter("body")))
            res.append(request.getParameter("body"));

        if (StringUtils.isNotEmpty(request.getParameter("title")))
            res.append(" +title:(").append(request.getParameter("title")).append(")");

        if (StringUtils.isNotEmpty(request.getParameter("nick")))
            res.append(" +nick:").append(request.getParameter("nick"));

        if (StringUtils.isNotEmpty(request.getParameter("host")))
            res.append(" +host:").append(request.getParameter("host"));

        return res.toString();
    }
}
