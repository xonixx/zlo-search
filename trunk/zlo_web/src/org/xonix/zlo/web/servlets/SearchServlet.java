package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.log4j.Logger;
import org.xonix.zlo.search.*;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbUtils;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.config.ErrorMessage;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.ZloPaginatedList;
import org.xonix.zlo.web.CookieUtils;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:31:41
 */
public class SearchServlet extends ForwardingServlet {
    private static final Logger logger = Logger.getLogger(SearchServlet.class);

    public static final String ON = "on";
    // query string params
    public static final String QS_TOPIC_CODE = "topic";
    public static final String QS_TEXT = "text";
    public static final String QS_NICK = ZloMessage.FIELDS.NICK;
    public static final String QS_HOST = ZloMessage.FIELDS.HOST;
    public static final String QS_SITE = "site";
    public static final String QS_DATES = "dates";
    public static final String QS_FROM_DATE = "fd";
    public static final String QS_TO_DATE = "td";
    public static final String QS_PAGE_SIZE = "pageSize";
    public static final String QS_PAGE_NUMBER = "page";

    public static final String QS_LAST_MSGS = "lastMsgs";

    public static final String QS_IN_TITLE = "inTitle";
    public static final String QS_IN_BODY = "inBody";

    public static final String QS_IN_REG = "reg";
    public static final String QS_IN_HAS_URL = "hasUrl";
    public static final String QS_IN_HAS_IMG = "hasImg";

    public static final String QS_SUBMIT = "submit";

    // session keys
    public static final String SESS_SEARCH_RESULT = "searchResult";
    public static final String SESS_SITE_ROOT = "siteRoot";
    public static final String SESS_PAGE_SIZE = QS_PAGE_SIZE;

    public static final String ERROR = "error";
    public static final String DEBUG = "debug";

    public static final String JSP_SEARCH = "/Search.jsp";

    public static SimpleDateFormat FROM_TO_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        String topicCodeStr = request.getParameter(QS_TOPIC_CODE);
        String text = request.getParameter(QS_TEXT);

        boolean inTitle = StringUtils.isNotEmpty(request.getParameter(QS_IN_TITLE));
        boolean inBody = StringUtils.isNotEmpty(request.getParameter(QS_IN_BODY));

        boolean inReg = StringUtils.isNotEmpty(request.getParameter(QS_IN_REG));
        boolean inHasUrl = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_URL));
        boolean inHasImg = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_IMG));

        String nick = request.getParameter(QS_NICK);
        String host = request.getParameter(QS_HOST);
        String fromDateStr = request.getParameter(QS_FROM_DATE);
        String toDateStr = request.getParameter(QS_TO_DATE);
        String pageSizeStrInd = request.getParameter(QS_PAGE_SIZE);

        ErrorMessage errorMsg = null;
        request.setAttribute(DEBUG, Config.DEBUG);

        try {
            HttpSession session = request.getSession(true); // create if session not started

            // set default topic code
            if (StringUtils.isEmpty(topicCodeStr)) {
                request.setParameter(QS_TOPIC_CODE, "-1"); // all
            }

            int topicCode;
            try {
                topicCode = Integer.parseInt(topicCodeStr);
            } catch (NumberFormatException e) {
                topicCode = -1; // all
            }

            // default values for checkboxes
            if (StringUtils.isEmpty(request.getParameter(QS_SUBMIT))) { // initial load
                // by default search both in title & body
                request.setParameter(QS_IN_TITLE, ON);
                request.setParameter(QS_IN_BODY, ON);
            }

            int pageSize = 100; // default
            if (StringUtils.isNotEmpty(pageSizeStrInd)) {
                try {
                    pageSize = Integer.parseInt(Config.NUMS_PER_PAGE[Integer.parseInt(pageSizeStrInd)]);
                    CookieUtils.rememberInCookie(response, QS_PAGE_SIZE, pageSizeStrInd);
                } catch (NumberFormatException ex) {
                    ;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ;
                }
            } else {
                String pageSizeStrIndCookie = CookieUtils.recallFromCookie(request, QS_PAGE_SIZE);
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
                    errorMsg = ErrorMessage.ToDateInvalid;
                    throw e;
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
                    errorMsg = ErrorMessage.FromDateInvalid;
                    throw e;
                }
            }

            session.setAttribute(QS_TO_DATE, FROM_TO_DATE_FORMAT.format(toDate));
            session.setAttribute(QS_FROM_DATE, FROM_TO_DATE_FORMAT.format(fromDate));

            SearchRequest searchRequest = new SearchRequest(text, inTitle, inBody,
                    inReg, inHasUrl, inHasImg, nick, host, topicCode, fromDate, toDate);

            if (searchRequest.canBeProcessed()) {

                if (StringUtils.isEmpty(request.getParameter(QS_DATES))) {
                    searchRequest.setFromDate(null);
                    searchRequest.setToDate(null);
                }

                SearchResult searchResult;

                if (session.getAttribute(SESS_SEARCH_RESULT) == null
                        || ((SearchResult) session.getAttribute(SESS_SEARCH_RESULT)).isNotTheSameSearch(searchRequest)
                        || StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER)) // turning pages
                        ) {

                    try {
                        searchResult = searchRequest.performSearch();
                        logRequest(request, searchResult.getQuery().toString());
                    } catch (BooleanQuery.TooManyClauses e) { // например как в поиске текста +с*
                        errorMsg = ErrorMessage.TooComplexSearch;
                        throw e;
                    } catch (ZloSearcher.ParseException e) {
                        errorMsg = ErrorMessage.InvalidQueryString;
                        errorMsg.setData(e.getQuery());
                        throw e;
                    }

                    session.setAttribute(SESS_SEARCH_RESULT, searchResult);
                } else {
                    searchResult = (SearchResult) session.getAttribute(SESS_SEARCH_RESULT);
                    searchResult.setNewSearch(false); // means we use result of previous search
                }
                if (searchResult != null) {
                    ZloPaginatedList paginatedList = (ZloPaginatedList) searchResult.getPaginatedList();
                    paginatedList.setObjectsPerPage(pageSize);
                    if (StringUtils.isNotEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                        try {
                            int pageNumber = Integer.parseInt(request.getParameter(QS_PAGE_NUMBER));
                            pageNumber = pageNumber <= 0 ? 1 : pageNumber;
                            paginatedList.setPageNumber(pageNumber);
                        } catch (NumberFormatException e) {
                            paginatedList.setPageNumber(1);
                        }
                    }

                    paginatedList.refreshCurrentList();
                }
            } else if (StringUtils.isNotEmpty(request.getParameter(QS_SUBMIT))) {
                errorMsg = ErrorMessage.MustSelectCriterion;
                throw new Exception();
            } else {
                session.setAttribute(SESS_SEARCH_RESULT, null);
                showStatistics(request);
            }

            String siteInCookie;
            if (StringUtils.isNotEmpty(request.getParameter(QS_SITE))) {
                String site;
                try {
                    site = Config.SITES[Integer.parseInt(request.getParameter(QS_SITE))];
                } catch (Exception e) {
                    site = Config.SITES[0];
                    request.setParameter(QS_SITE, "0");
                }
                session.setAttribute(SESS_SITE_ROOT, site);
                CookieUtils.rememberInCookie(response, QS_SITE, request.getParameter(QS_SITE));
            } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, QS_SITE))) {
                request.setParameter(QS_SITE, siteInCookie); // for drop-down
                session.setAttribute(SESS_SITE_ROOT, Config.SITES[Integer.parseInt(siteInCookie)]); // for search result list
            } else {
                session.setAttribute(SESS_SITE_ROOT, Config.SITES[0]);
            }
        } catch (DbException e) {
            errorMsg = ErrorMessage.DbError;
        } catch (Exception e) {
            ;
        }

        if (errorMsg != null)
            request.setAttribute(ERROR, errorMsg);

        request.forwardTo(JSP_SEARCH);
    }

    private void showStatistics(ForwardingRequest request) throws DbException {
        request.setAttribute(QS_LAST_MSGS, DbManager.getLastMessageNums());
    }

    private void logRequest(ForwardingRequest request, String query) {
        try {
            DbManager.logRequest(
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    request.getParameter(QS_TEXT),
                    query,
                    request.getHeader("Referer"));
        } catch (DbException e) {
            logger.error("Can't log user request" + e.getClass());
        }
    }

    public void destroy() {
        super.destroy();
        logger.info("Destroying search servlet. Cleaning...");
        ZloSearcher.clean();
        DbUtils.clean();
        logger.info("Collecting garbage...");
        System.gc();
        logger.info("Done.");
    }
}
