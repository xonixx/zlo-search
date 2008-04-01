package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;
import org.xonix.zlo.search.SearchRequest;
import org.xonix.zlo.search.SearchResult;
import org.xonix.zlo.search.ZloPaginatedList;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.config.ErrorMessage;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbAccessor;
import org.xonix.zlo.web.CookieUtils;
import org.xonix.zlo.search.FoundTextHighlighter;
import org.xonix.zlo.search.utils.HtmlUtils;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.MessageFormat;
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
public class SearchServlet extends BaseServlet {
    private static final Logger logger = Logger.getLogger(SearchServlet.class);

    public static final String ON = "on";
    // query string params
    public static final String QS_TOPIC_CODE = "topic";
    public static final String QS_TEXT = "text";
    public static final String QS_NICK = "nick";
    public static final String QS_HOST = "host";
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

    public static final String QS_SEARCH_TYPE = "st";
    public static final String SEARCH_TYPE_ALL = "all";
    public static final String SEARCH_TYPE_ADVANCED = "adv";
    public static final String SEARCH_TYPE_EXACT_PHRASE = "exct";

    public static final String QS_SUBMIT = "submitBtn";

    public static final String REQ_HIGHLIGHT_WORDS = "hw";

    // session keys
    public static final String SESS_SEARCH_RESULT = "searchResult";
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

        // to refresh site from session to req
        setSiteInSession(request, response);

        try {
            HttpSession session = request.getSession(true); // create if session not started

            // set default topic code
            if (StringUtils.isEmpty(topicCodeStr)) {
                request.setParameter(QS_TOPIC_CODE, "-1"); // all
            }

            // set default search type
            String searchType = request.getParameter(QS_SEARCH_TYPE);
            if (StringUtils.isEmpty(searchType)) {
                request.setParameter(QS_SEARCH_TYPE, SEARCH_TYPE_ALL);
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
                    } catch(ArrayIndexOutOfBoundsException ex) {
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

            text = preprocessSearchText(text, searchType);

            SearchRequest searchRequest = new SearchRequest(getSite(request), text, inTitle, inBody, inReg, inHasUrl, inHasImg, nick, host, topicCode, fromDate, toDate);

            if (searchRequest.canBeProcessed()) {

                request.setAttribute(REQ_HIGHLIGHT_WORDS, FoundTextHighlighter.formHighlightedWords(text));

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
                    ZloPaginatedList paginatedList = (ZloPaginatedList) searchResult.createPaginatedList(getSite(request));
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

            setSiteInSession(request, response);
        } catch (DbException e) {
            errorMsg = ErrorMessage.DbError;
            logger.error(e);
        } catch (Exception e) {
            if (errorMsg == null) {
                // unknown error
                throw new ServletException(e);
            }
        }

        if (errorMsg != null)
            request.setAttribute(ERROR, errorMsg);

        request.forwardTo(JSP_SEARCH);
    }

    private void showStatistics(ForwardingRequest request) throws DbException {
        DbManager dbm = getSite(request).getDbManager();
        request.setAttribute(QS_LAST_MSGS, new int[]{dbm.getLastMessageNumber(), dbm.getLastIndexedNumber()});
    }

    private void logRequest(ForwardingRequest request, String query) {
        String remoteAddr = request.getHeader("x-forwarded-for");
        remoteAddr = StringUtils.isNotEmpty(remoteAddr) ? remoteAddr : request.getRemoteAddr();
        try {
            DbAccessor.getInstance("search_log").getDbManager().logRequest(
                    getSite(request).getNum(),
                    remoteAddr,
                    request.getHeader("User-Agent"),
                    request.getParameter(QS_TEXT),
                    request.getParameter(QS_NICK),
                    request.getParameter(QS_HOST),
                    query,
                    request.getQueryString(),
                    request.getHeader("Referer"));
        } catch (DbException e) {
            logger.error(e);
        }
    }

    private String preprocessSearchText(String text, String searchType) {
        // preprocess for search for urls with "?"
        if (HtmlUtils.remindsUrl(text)) {
            text = text.replace("?", ".");
        }
        
        if (StringUtils.isNotEmpty(text)) {
            if(SEARCH_TYPE_ALL.equals(searchType)) {
                text = text.replaceAll("(?<!-|!)\\b([^\\s]+)\\b", "+$1");
            } else if (SEARCH_TYPE_EXACT_PHRASE.equals(searchType)) {
                text = MessageFormat.format("\"{0}\"", text);
            }
        }

        return text;
    }
}
