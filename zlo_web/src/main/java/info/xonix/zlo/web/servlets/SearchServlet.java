package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.ZloPaginatedList;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.domainobj.SearchRequest;
import info.xonix.zlo.search.domainobj.SearchResult;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.*;
import info.xonix.zlo.search.logic.exceptions.ExceptionCategory;
import info.xonix.zlo.search.logic.exceptions.ExceptionsLogger;
import info.xonix.zlo.search.model.SearchLog;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.HtmlUtils;
import info.xonix.zlo.web.RequestCache;
import info.xonix.zlo.web.rss.RssFormer;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.utils.CookieUtils;
import info.xonix.zlo.web.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.dao.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:31:41
 * TODO: Refactor this!!!
 */
public class SearchServlet extends BaseServlet {
    private static final Logger log = Logger.getLogger(SearchServlet.class);

    private final Config config = AppSpringContext.get(Config.class);
    private final AppLogic appLogic = AppSpringContext.get(AppLogic.class);
    private final AuditLogic auditLogic = AppSpringContext.get(AuditLogic.class);
    private final ExceptionsLogger exceptionsLogger = AppSpringContext.get(ExceptionsLogger.class);
    private SearchLogic searchLogic = AppSpringContext.get(SearchLogicImpl.class);

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

    public static final String QS_RSS = "rss";

    public static final String QS_LAST_MSGS = "lastMsgs";
    public static final String QS_LAST_MSGS_DATES = "lastMsgs_dates";

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

    // request keys
    public static final String REQ_SEARCH_RESULT = "searchResult";
    public static final String REQ_PAGINATED_LIST = "paginatedList";
    public static final String REQ_PAGE_SIZE = QS_PAGE_SIZE;

    public static final String ERROR = "error";
    public static final String DEBUG = "debug";

    public static final String JSP_SEARCH = "/WEB-INF/jsp/Search.jsp";

    public static DateFormat FROM_TO_DATE_FORMAT = DateFormats.ddMMyyyy_dots;

    private static final RequestCache cache = new RequestCache();
    private final RssFormer rssFormer = new RssFormer();

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String topicCodeStr = request.getParameter(QS_TOPIC_CODE);
        String text = request.getParameter(QS_TEXT);

        final boolean inTitle = StringUtils.isNotEmpty(request.getParameter(QS_IN_TITLE));
        final boolean inBody = StringUtils.isNotEmpty(request.getParameter(QS_IN_BODY));

        final boolean inReg = StringUtils.isNotEmpty(request.getParameter(QS_IN_REG));
        final boolean inHasUrl = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_URL));
        final boolean inHasImg = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_IMG));

        final boolean isRssAsked = request.getParameter(QS_RSS) != null;

        String nick = request.getParameter(QS_NICK);
        String host = request.getParameter(QS_HOST);
        final String fromDateStr = request.getParameter(QS_FROM_DATE);
        final String toDateStr = request.getParameter(QS_TO_DATE);

        ErrorMessage errorMsg = null;
        request.setAttribute(DEBUG, config.isDebug());

        // to refresh site from cookies to req
        setSiteInReq(request, response);
        setPowerUserInCookies(request, response);

        SearchRequest searchRequest = null;

        try {
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

            final int pageSize = processPageSize(request, response);

            Date fromDate;
            Date toDate;
            Calendar cal = new GregorianCalendar();
            if (StringUtils.isEmpty(toDateStr)) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                toDate = cal.getTime(); // now + 1 day
            } else {
                try {
                    toDate = FROM_TO_DATE_FORMAT.parse(toDateStr);
                } catch (ParseException e) {
                    errorMsg = ErrorMessage.ToDateInvalid;
                    throw e;
                }
            }

            if (StringUtils.isEmpty(fromDateStr)) {
//                cal.add(Calendar.YEAR, -Config.TIME_PERIOD_YEARS);
//                cal.add(Calendar.MONTH, -Config.TIME_PERIOD_MONTHS);
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -1); // last 24 h
                fromDate = cal.getTime();
            } else {
                try {
                    fromDate = FROM_TO_DATE_FORMAT.parse(fromDateStr);
                } catch (ParseException e) {
                    errorMsg = ErrorMessage.FromDateInvalid;
                    throw e;
                }
            }

            request.setAttribute(QS_TO_DATE, FROM_TO_DATE_FORMAT.format(toDate));
            request.setAttribute(QS_FROM_DATE, FROM_TO_DATE_FORMAT.format(fromDate));

            text = preprocessSearchText(text, searchType);
            nick = preprocessSearchNick(nick);

            searchRequest = new SearchRequest(
                    getSite(request), text,
                    inTitle, inBody, inReg, inHasUrl, inHasImg,
                    nick, host, topicCode,
                    StringUtils.isNotEmpty(fromDateStr) || StringUtils.isNotEmpty(toDateStr),
                    fromDate, toDate,
                    SEARCH_TYPE_ALL.equals(searchType));

            if (searchRequest.canBeProcessed()) {

                request.setAttribute(REQ_HIGHLIGHT_WORDS, FoundTextHighlighter.formHighlightedWords(text));

                if (StringUtils.isEmpty(request.getParameter(QS_DATES))) {
                    searchRequest.setFromDate(null);
                    searchRequest.setToDate(null);
                }

                int searchHash = searchRequest.hashCode();

                final SearchResult searchResult;
                final SearchResult prevSearchResult = cache.get(searchHash);

                if (prevSearchResult == null
                        || prevSearchResult.isNotTheSameSearch(searchRequest)
                        || prevSearchResult.isOld() // this should enforce re-search while indexing, so VV seems not needed more
//                        || StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER)) // not turning pages, but searching // commenting by now todo: check if this is not needed more
                        ) {

                    try {
                        searchResult = searchLogic.search(searchRequest);
                    } catch (BooleanQuery.TooManyClauses e) { // например как в поиске текста +с*
                        errorMsg = ErrorMessage.TooComplexSearch;
                        throw e;
                    } catch (SearchException e) {
                        errorMsg = ErrorMessage.InvalidQueryString;
                        errorMsg.setData(e.getQuery());
                        throw e;
                    }

                    cache.put(searchHash, searchResult);
                } else {
                    searchResult = prevSearchResult;
                    searchResult.setNewSearch(false); // means we use result of previous search
                    log.info("Cached search: " + searchResult.getQuery());
                }

                if (searchResult != null) {
                    // log only initial search. Moved here - because now cached for all users,
                    // but log need to be performed nevertheless for every user
                    if (StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                        logRequest(request, searchResult.getQuery().toString(), isRssAsked);
                    }

                    request.setAttribute(REQ_SEARCH_RESULT, searchResult);

                    final ZloPaginatedList paginatedList = ZloPaginatedList.fromSearchResult(searchResult);
                    request.setAttribute(REQ_PAGINATED_LIST, paginatedList);

                    if (isRssAsked) {
                        paginatedList.setPageNumber(1);
                        paginatedList.setObjectsPerPage(50);
                    } else {
                        paginatedList.setObjectsPerPage(pageSize);
                        if (StringUtils.isNotEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                            try {
                                int pageNumber = Integer.parseInt(request.getParameter(QS_PAGE_NUMBER));
                                pageNumber = pageNumber <= 0 ? 1 : pageNumber;
                                paginatedList.setPageNumber(pageNumber);
                            } catch (NumberFormatException e) {
                                paginatedList.setPageNumber(1);
                            }
                        } else {
                            paginatedList.setPageNumber(1);
                        }
                    }

                    paginatedList.refreshCurrentList();// todo: handle java.lang.NegativeArraySizeException here
                } else {
                    log.error("searchResult == null. This should not happen!");
                }
            } else if (StringUtils.isNotEmpty(request.getParameter(QS_SUBMIT))) {
                errorMsg = ErrorMessage.MustSelectCriterion;
                throw new Exception();
            } else {
                request.setAttribute(REQ_SEARCH_RESULT, null);
                showStatistics(request);
            }
        } catch (DataAccessException e) { // TODO: handle
            errorMsg = ErrorMessage.DbError;
            log.error("Database error", e);

            exceptionsLogger.logException(e,
                    "Database exception while user search: " +
                            searchRequest != null ? searchRequest.describeToString() : "N/A",
                    "Search servlet",
                    ExceptionCategory.WEB);

        } catch (Exception e) {
            if (errorMsg == null) {
                // unknown error
                log.error("Unknown error", e);

                exceptionsLogger.logException(e,
                        "Unknown exception while user search: " +
                                searchRequest != null ? searchRequest.describeToString() : "N/A",
                        "Search servlet",
                        ExceptionCategory.WEB);

                throw new ServletException(e);
            }
        }

        if (errorMsg != null) {
            request.setAttribute(ERROR, errorMsg);
        }

        if (isRssAsked) {
            rssFormer.formRss(request, response);
        } else {
            request.forwardTo(JSP_SEARCH);
        }
    }

    private int processPageSize(ForwardingRequest request, HttpServletResponse response) {
        int pageSize = 100; // default
        String pageSizeStrInd = request.getParameter(QS_PAGE_SIZE);
        if (StringUtils.isNotEmpty(pageSizeStrInd)) {
            try {
                pageSize = Integer.parseInt(config.getNumsPerPage()[Integer.parseInt(pageSizeStrInd)]);
                CookieUtils.rememberInCookie(response, QS_PAGE_SIZE, pageSizeStrInd);
            } catch (NumberFormatException ignored) {
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else {
            String pageSizeStrIndCookie = CookieUtils.recallFromCookie(request, QS_PAGE_SIZE);
            if (StringUtils.isNotEmpty(pageSizeStrIndCookie)) {
                try {
                    pageSize = Integer.parseInt(config.getNumsPerPage()[Integer.parseInt(pageSizeStrIndCookie)]);
                    request.setParameter(QS_PAGE_SIZE, pageSizeStrIndCookie);
                } catch (NumberFormatException ignored) {
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            } else {
                pageSize = Integer.parseInt(config.getNumsPerPage()[0]);
            }
        }
        request.setAttribute(REQ_PAGE_SIZE, pageSize);
        return pageSize;
    }

    private void setPowerUserInCookies(HttpServletRequest request, HttpServletResponse response) {
        final String powerUserKey = config.getPowerUserKey();
        final String powerUserValue = request.getParameter(powerUserKey);

        final String clientIp = RequestUtils.getClientIp(request);

        if ("0".equals(powerUserValue)) {
            log.info("Forgetting power user, ip=" + clientIp);

            CookieUtils.forgetCookie(response, powerUserKey);
        } else if (powerUserValue != null) {
            log.info("Power user enters, ip=" + clientIp);

            CookieUtils.rememberInCookie(response, powerUserKey, "1");
        }
    }

    private void showStatistics(ForwardingRequest request) {
        Site site = getSite(request);
        request.setAttribute(QS_LAST_MSGS,
                new int[]{appLogic.getLastSavedMessageNumber(site),
                        appLogic.getLastIndexedNumber(site)});
        request.setAttribute(QS_LAST_MSGS_DATES,
                new Date[]{appLogic.getLastSavedDate(site),
                        appLogic.getLastIndexedDate(site)});
    }

    private void logRequest(ForwardingRequest request, String query, boolean rssAsked) {
        SearchLog searchLog = new SearchLog();

        searchLog.setSite(getSite(request));
        searchLog.setClientIp(RequestUtils.getClientIp(request));
        searchLog.setUserAgent(request.getHeader(HttpHeader.USER_AGENT));
        searchLog.setReferer(request.getHeader(HttpHeader.REFERER));

        searchLog.setSearchText(request.getParameter(QS_TEXT));
        searchLog.setSearchNick(request.getParameter(QS_NICK));
        searchLog.setSearchHost(request.getParameter(QS_HOST));

        searchLog.setSearchQuery(query);
        searchLog.setSearchQueryString(request.getQueryString());

        searchLog.setRssAsked(rssAsked);
        searchLog.setAdminRequest(RequestUtils.isPowerUser(request));

        auditLogic.logSearchEvent(searchLog);
    }

    private String preprocessSearchText(String text, String searchType) {
        if (StringUtils.isEmpty(text))
            return text;

        // kill escaping "\" to be able search for \\host\share
        text = text.replace("\\", ".");

        // preprocess for search for urls with "?"
        if (HtmlUtils.remindsUrl(text)) {
            text = text.replace("?", ".");
        }

        if (SEARCH_TYPE_EXACT_PHRASE.equals(searchType)) {
            text = MessageFormat.format("\"{0}\"", text);
        }

        return text;
    }

    private String preprocessSearchNick(String nick) {
        if (nick == null)
            return "";
        // todo: need to index stripped nicks too!! 
        return StringUtils.strip(nick)
                .replace("\\", "\\\\"); // to be possible search for nick like \/\/0\/\/KA 
    }
}
