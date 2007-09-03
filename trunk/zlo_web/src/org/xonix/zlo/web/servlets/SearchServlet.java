package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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

        if (StringUtils.isNotEmpty(title) ||
                StringUtils.isNotEmpty(body) ||
                StringUtils.isNotEmpty(nick) ||
                StringUtils.isNotEmpty(host) ||
                StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode)) {
            request.setAttribute("searchResult", ZloSearcher.search(topicCode, title, body, nick, host));
        }

        request.setAttribute("debug", "true".equalsIgnoreCase(getServletContext().getInitParameter("debug")));

        String siteInCookie;
        if (StringUtils.isNotEmpty(request.getParameter("site"))){
            request.setAttribute("siteRoot", Config.SITES[Integer.valueOf(request.getParameter("site"))]);
            rememberInCookie(response, "site", request.getParameter("site"));
        } else if (StringUtils.isNotEmpty(siteInCookie = recallFromCookie(request, "site"))){
            request.setAttribute("site", siteInCookie); // for drop-down
            request.setAttribute("siteRoot", Config.SITES[Integer.valueOf(siteInCookie)]); // for search result list
        } else {
            request.setAttribute("siteRoot", Config.SITES[0]);
        }

        //request.setAttribute("error", "Fuck!!!");

        //response.setCharacterEncoding("UTF-8");
        request.forwardTo("/Search.jsp");
    }

    private void rememberInCookie(HttpServletResponse response, String fieldname, String value, int age) {
        Cookie cookie = new Cookie(fieldname, value);
        cookie.setMaxAge(age); // forever
        response.addCookie(cookie);
    }

    private void rememberInCookie(HttpServletResponse response, String fieldname, String value) {
        rememberInCookie(response, fieldname, value, Integer.MAX_VALUE); // forever    
    }

    private String recallFromCookie(HttpServletRequest request, String fieldname) {
        Cookie [] cookies = request.getCookies();
        for (Cookie cookie: cookies) {
            if (fieldname.equals(cookie.getName()))
                return cookie.getValue();
        }
        return null;
    }
}
