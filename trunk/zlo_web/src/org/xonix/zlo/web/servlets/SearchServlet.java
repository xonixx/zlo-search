package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.config.ErrorMessages;
import org.xonix.zlo.search.ZloSearchResult;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.xonix.zlo.web.ZloPaginatedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:31:41
 */
public class SearchServlet extends ForwardingServlet {
    // query string params
    public static final String QS_TOPIC = ZloMessage.TOPIC;
    public static final String QS_BODY = ZloMessage.BODY;
    public static final String QS_TITLE = ZloMessage.TITLE;
    public static final String QS_NICK = ZloMessage.NICK;
    public static final String QS_HOST = ZloMessage.HOST;
    public static final String QS_SITE = "site";
    public static final String QS_DATES = "dates";
    public static final String QS_FROM_DATE = "fd";
    public static final String QS_TO_DATE = "td";
    public static final String QS_PAGE_SIZE = "pageSize";
    public static final String QS_PAGE_NUMBER = "page";
    public static final String QS_SUBMIT = "submit";

    // session keys
    public static final String SESS_SEARCH_RESULT = "searchResult";
    public static final String SESS_SITE_ROOT = "siteRoot";
    public static final String SESS_PAGE_SIZE = QS_PAGE_SIZE;

    public static final String ERROR = "error";

    public static final String JSP_SEARCH = "/Search.jsp";

    public static SimpleDateFormat FROM_TO_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        String topicCode = request.getParameter(QS_TOPIC);
        String title = request.getParameter(QS_TITLE);
        String body = request.getParameter(QS_BODY);
        String nick = request.getParameter(QS_NICK);
        String host = request.getParameter(QS_HOST);
        String fromDateStr = request.getParameter(QS_FROM_DATE);
        String toDateStr = request.getParameter(QS_TO_DATE);
        String pageSizeStrInd = request.getParameter(QS_PAGE_SIZE);

        HttpSession session = request.getSession(true); // create if session not started

        int pageSize = 100; // default
        if (StringUtils.isNotEmpty(pageSizeStrInd)) {
            try {
                pageSize = Integer.parseInt(Config.NUMS_PER_PAGE[Integer.parseInt(pageSizeStrInd)]);
                rememberInCookie(response, QS_PAGE_SIZE, pageSizeStrInd);
            } catch(NumberFormatException ex){
                ;
            } catch (ArrayIndexOutOfBoundsException ex) {
                ;
            }
        } else {
            String pageSizeStrIndCookie = recallFromCookie(request, QS_PAGE_SIZE);
            if (StringUtils.isNotEmpty(pageSizeStrIndCookie)) {
                try {
                    pageSize = Integer.parseInt(Config.NUMS_PER_PAGE[Integer.parseInt(pageSizeStrIndCookie)]);
                    request.setParameter(QS_PAGE_SIZE, pageSizeStrIndCookie);
                } catch (NumberFormatException ex) {
                    ;
                }
            } else {
                pageSize = Integer.parseInt(Config.NUMS_PER_PAGE[0]);
            }
        }
        session.setAttribute(SESS_PAGE_SIZE, pageSize);

        Date fromDate;
        Date toDate;
        if (StringUtils.isEmpty(toDateStr)) {
            toDate = new Date(); // now
        } else {
            try {
                toDate = FROM_TO_DATE_FORMAT.parse(toDateStr);
            } catch (ParseException e) {
                request.setAttribute(ERROR, ErrorMessages.ToDateInvalid);
                request.forwardTo(JSP_SEARCH);
                return;
            }
        }

        if (StringUtils.isEmpty(fromDateStr)) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(toDate);
            cal.add(Calendar.YEAR, -Config.TIME_PERIOD_YEARS);
            cal.add(Calendar.MONTH, -Config.TIME_PERIOD_MONTHS);
            fromDate = cal.getTime();
        } else {
            try {
                fromDate = FROM_TO_DATE_FORMAT.parse(fromDateStr);
            } catch (ParseException e) {
                request.setAttribute(ERROR, ErrorMessages.FromDateInvalid);
                request.forwardTo(JSP_SEARCH);
                return;
            }
        }

        // request
        session.setAttribute(QS_TO_DATE, FROM_TO_DATE_FORMAT.format(toDate));
        session.setAttribute(QS_FROM_DATE, FROM_TO_DATE_FORMAT.format(fromDate));

        if (StringUtils.isNotEmpty(title) ||
                StringUtils.isNotEmpty(body) ||
                StringUtils.isNotEmpty(nick) ||
                StringUtils.isNotEmpty(host) ||
                StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode)) {

            if (StringUtils.isEmpty(request.getParameter(QS_DATES))) {
                fromDate = toDate = null;
            }

            ZloPaginatedList zloPaginatedList;
            if (session.getAttribute(SESS_SEARCH_RESULT) == null ||
                    ((ZloSearchResult) session.getAttribute(SESS_SEARCH_RESULT)).isNotTheSameSearch(topicCode, title, body, nick, host, fromDate, toDate)) {
                ZloSearchResult zloSearchResult = ZloSearcher.search(topicCode, title, body, nick, host, fromDate, toDate);
                session.setAttribute(SESS_SEARCH_RESULT, zloSearchResult);
                zloPaginatedList = (ZloPaginatedList) zloSearchResult.getPaginatedList();
            } else {
                zloPaginatedList = (ZloPaginatedList) ((ZloSearchResult) session.getAttribute(SESS_SEARCH_RESULT)).getPaginatedList();
            }
            zloPaginatedList.setObjectsPerPage(pageSize);
            if (StringUtils.isNotEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                try {
                    int pageNumber = Integer.parseInt(request.getParameter(QS_PAGE_NUMBER));
                    pageNumber = pageNumber <= 0 ? 1 : pageNumber;
                    zloPaginatedList.setPageNumber(pageNumber);
                } catch (NumberFormatException e) {
                    zloPaginatedList.setPageNumber(1);
                }
            }
        } else if (StringUtils.isNotEmpty(request.getParameter(QS_SUBMIT))) {
            request.setAttribute(ERROR, ErrorMessages.MustSelectCriterion);
        } else {
            session.setAttribute(SESS_SEARCH_RESULT, null);
        }

        request.setAttribute("debug", "true".equalsIgnoreCase(getServletContext().getInitParameter("debug")));

        String siteInCookie;
        if (StringUtils.isNotEmpty(request.getParameter(QS_SITE))){
            String site;
            try {
                site = Config.SITES[Integer.parseInt(request.getParameter(QS_SITE))];
            } catch (Exception e) {
                site = Config.SITES[0];
                request.setParameter(QS_SITE, "0");
            }
            session.setAttribute(SESS_SITE_ROOT, site);
            rememberInCookie(response, QS_SITE, request.getParameter(QS_SITE));
        } else if (StringUtils.isNotEmpty(siteInCookie = recallFromCookie(request, QS_SITE))){
            request.setParameter(QS_SITE, siteInCookie); // for drop-down
            session.setAttribute(SESS_SITE_ROOT, Config.SITES[Integer.parseInt(siteInCookie)]); // for search result list
        } else {
            session.setAttribute(SESS_SITE_ROOT, Config.SITES[0]);
        }

        request.forwardTo(JSP_SEARCH);
    }

    private void rememberInCookie(HttpServletResponse response, String fieldname, String value, int age) {
        Cookie cookie = new Cookie(fieldname, value);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    private void rememberInCookie(HttpServletResponse response, String fieldname, String value) {
        rememberInCookie(response, fieldname, value, Integer.MAX_VALUE); // forever    
    }

    private String recallFromCookie(HttpServletRequest request, String fieldname) {
        Cookie [] cookies = request.getCookies();

        if (cookies == null)
            return StringUtils.EMPTY;

        for (Cookie cookie: cookies) {
            if (fieldname.equals(cookie.getName()))
                return cookie.getValue();
        }
        return StringUtils.EMPTY;
    }
}
