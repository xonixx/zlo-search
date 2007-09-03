package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.model.ZloMessage;
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
        String topicCode = request.getParameter("topic");
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String nick = request.getParameter("nick");
        String host = request.getParameter("host");

        if (StringUtils.isNotEmpty(title) |
                StringUtils.isNotEmpty(body) |
                StringUtils.isNotEmpty(nick) |
                StringUtils.isNotEmpty(host) |
                StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode)) {
            request.setAttribute("searchResult", ZloSearcher.search(topicCode, title, body, nick, host));
        }

        request.setAttribute("debug", "true".equalsIgnoreCase(getServletContext().getInitParameter("debug")));
        request.setAttribute("siteRoot", "zlo.rt.mipt.ru");
        //request.setAttribute("error", "Fuck!!!");

        response.setCharacterEncoding("UTF-8");
        request.forwardTo("/Search.jsp");
    }
}
