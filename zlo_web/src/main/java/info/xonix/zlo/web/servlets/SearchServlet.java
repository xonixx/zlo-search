package info.xonix.zlo.web.servlets;

import info.xonix.utils.SafeUtils;
import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.SearchResultPaginatedList;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.domain.SearchRequest;
import info.xonix.zlo.search.domain.SearchResult;
import info.xonix.zlo.search.domain.SortBy;
import info.xonix.zlo.search.logic.*;
import info.xonix.zlo.search.logic.exceptions.ExceptionCategory;
import info.xonix.zlo.search.logic.exceptions.ExceptionsLogger;
import info.xonix.zlo.search.model.SearchLog;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.HtmlUtils;
import info.xonix.zlo.search.utils.obscene.ObsceneAnalyzer;
import info.xonix.zlo.web.rss.RssFormer;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.utils.CookieUtils;
import info.xonix.zlo.web.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;
import org.eclipse.jetty.util.MultiMap;
import org.springframework.dao.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static info.xonix.zlo.search.config.Config.SMART_QUERY_PARSER;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:31:41
 * TODO: Refactor this!!!
 */
public class SearchServlet extends BaseServlet {
    private static final Logger log = Logger.getLogger(SearchServlet.class);

    public static final int MAX_RESULTS_LIMIT = 200000;

    private final Config config = AppSpringContext.get(Config.class);
    private final AppLogic appLogic = AppSpringContext.get(AppLogic.class);
    private final AuditLogic auditLogic = AppSpringContext.get(AuditLogic.class);
    private final ExceptionsLogger exceptionsLogger = AppSpringContext.get(ExceptionsLogger.class);
    private final SearchLogic searchLogic = AppSpringContext.get(SearchLogicImpl.class);
    private final ObsceneAnalyzer obsceneAnalyzer = AppSpringContext.get(ObsceneAnalyzer.class);
    private final UserLogic userLogic = AppSpringContext.get(UserLogic.class);

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
    public static final String QS_IS_ROOT = "isRoot";

    public static final String QS_SEARCH_TYPE = "st";
    public static final String SEARCH_TYPE_ALL = "all";
    public static final String SEARCH_TYPE_ADVANCED = "adv";
    public static final String SEARCH_TYPE_EXACT_PHRASE = "exct";

    public static final String QS_SUBMIT = "submitBtn";

    public static final String QS_SORT = "sort";

    public static final String REQ_HIGHLIGHT_WORDS = "hw";

    // request keys
    public static final String REQ_SEARCH_RESULT = "searchResult";
    public static final String REQ_PAGINATED_LIST = "paginatedList";
    public static final String REQ_PAGE_SIZE = QS_PAGE_SIZE;

    public static final String ERROR = "error";
    public static final String DEBUG = "debug";

    public static final String JSP_SEARCH = "/WEB-INF/jsp/Search.jsp";

    public static DateFormat FROM_TO_DATE_FORMAT = DateFormats.ddMMyyyy_dots;

    private final RssFormer rssFormer = new RssFormer();

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        final MultiMap<String> params = SMART_QUERY_PARSER.parseUrlencodedParams(request.getQueryString());

        String text = smartGetParam(request, params, QS_TEXT);
        String nick = smartGetParam(request, params, QS_NICK);
        String host = smartGetParam(request, params, QS_HOST);

        final String topicCodeStr = request.getParameter(QS_TOPIC_CODE);

        final boolean inTitle = StringUtils.isNotEmpty(request.getParameter(QS_IN_TITLE));
        final boolean inBody = StringUtils.isNotEmpty(request.getParameter(QS_IN_BODY));

        final boolean inReg = StringUtils.isNotEmpty(request.getParameter(QS_IN_REG));
        final boolean inHasUrl = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_URL));
        final boolean inHasImg = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_IMG));
        final boolean isRoot = StringUtils.isNotEmpty(request.getParameter(QS_IS_ROOT));

        final boolean isRssAsked = request.getParameter(QS_RSS) != null;

        final String fromDateStr = request.getParameter(QS_FROM_DATE);
        final String toDateStr = request.getParameter(QS_TO_DATE);

        final SortBy sortDirection = SortBy.byName(request.getParameter(QS_SORT));

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

            int topicCode = NumberUtils.toInt(topicCodeStr, -1/* all */);

            // set default search type
            String searchType = request.getParameter(QS_SEARCH_TYPE);
            if (StringUtils.isEmpty(searchType)) {
                request.setParameter(QS_SEARCH_TYPE, SEARCH_TYPE_ALL);
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
                    getSite(request).getForumId(), text, isRoot,
                    inTitle, inBody, inReg, inHasUrl, inHasImg,
                    nick, host, topicCode,
                    StringUtils.isNotEmpty(fromDateStr) || StringUtils.isNotEmpty(toDateStr),
                    fromDate, toDate,
                    SEARCH_TYPE_ALL.equals(searchType),
                    sortDirection);

            if (searchRequest.canBeProcessed()) {
                final String ip = RequestUtils.getClientIp(request);

                if (userLogic.getBannedStatus(ip).isBanned()) {
                    log.warn("Search by banned IP: " + ip + ", query: " + searchRequest.describeToString());

                    errorMsg = ErrorMessage.Banned;
                    throw new Exception();
                }

                request.setAttribute(REQ_HIGHLIGHT_WORDS, FoundTextHighlighter.formHighlightedWords(text));

                if (StringUtils.isEmpty(request.getParameter(QS_DATES))) {
                    searchRequest.setFromDate(null);
                    searchRequest.setToDate(null);
                }

                final int pageNumber;
                final int objectsPerPage;

                if (isRssAsked) {
                    pageNumber = 1;
                    objectsPerPage = 50;
                } else {
                    pageNumber = getPageNumber(request);
                    objectsPerPage = pageSize;
                }

                final SearchResult searchResult;

                final int limit = getLimit(pageNumber, objectsPerPage);

                try {
                    searchResult = searchLogic.search(searchRequest, limit);
                } catch (BooleanQuery.TooManyClauses e) { // например как в поиске текста +с*
                    errorMsg = ErrorMessage.TooComplexSearch;
                    throw e;
                } catch (SearchException e) {
                    errorMsg = ErrorMessage.InvalidQueryString;
                    errorMsg.setData(e.getQuery());
                    throw e;
                }

                if (searchResult != null) {
                    // log only initial search. Moved here - because now cached for all users,
                    // but log need to be performed nevertheless for every user
                    if (StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                        logRequest(request, searchResult.getQuery().toString(), isRssAsked);
                    }

                    request.setAttribute(REQ_SEARCH_RESULT, searchResult);

                    final SearchResultPaginatedList paginatedList;

                    if (RequestUtils.isPowerUser(request)) {
                        paginatedList = new SearchResultPaginatedList(searchResult);
                    } else {
                        paginatedList = new SearchResultPaginatedList(searchResult, MAX_RESULTS_LIMIT);
                    }

                    request.setAttribute(REQ_PAGINATED_LIST, paginatedList);

                    paginatedList.setPageNumber(pageNumber);
                    paginatedList.setObjectsPerPage(objectsPerPage);

                    paginatedList.refreshCurrentList();
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

    private String smartGetParam(ForwardingRequest request, MultiMap<String> params, final String paramName) {
        String val = params.getString(paramName);
        request.setParameter(paramName, val);
        return val;
    }

    private int getLimit(int pageNumber, int objectsPerPage) {
        return pageNumber * objectsPerPage;
    }

    private int getPageNumber(ServletRequest request) {
        int pageNumber;
        final String pageNumberStr = request.getParameter(QS_PAGE_NUMBER);
        if (StringUtils.isNotEmpty(pageNumberStr)) {
            pageNumber = NumberUtils.toInt(pageNumberStr, 1);
            if (pageNumber <= 0) {
                pageNumber = 1;
            }
        } else {
            pageNumber = 1;
        }
        return pageNumber;
    }

    private int processPageSize(ForwardingRequest request, HttpServletResponse response) {
        String pageSizeStrInd = request.getParameter(QS_PAGE_SIZE);
        int idx = 0;

        if (StringUtils.isNotEmpty(pageSizeStrInd)) {
            idx = NumberUtils.toInt(pageSizeStrInd, 0);
        }

        int pageSize = SafeUtils.safeGet(config.getNumsPerPage(), idx);

        request.setAttribute(REQ_PAGE_SIZE, pageSize);

        return pageSize;
    }

    private void setPowerUserInCookies(HttpServletRequest request, HttpServletResponse response) {
        final String powerUserKey = config.getPowerUserKey();

        if (StringUtils.isNotEmpty(powerUserKey)) {
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
    }

    private void showStatistics(ForwardingRequest request) {
        String forumId = getSite(request).getForumId();

        request.setAttribute(QS_LAST_MSGS,
                new int[]{appLogic.getLastSavedMessageNumber(forumId),
                        appLogic.getLastIndexedNumber(forumId)});

        request.setAttribute(QS_LAST_MSGS_DATES,
                new Date[]{appLogic.getLastSavedDate(forumId),
                        appLogic.getLastIndexedDate(forumId)});
    }

    private void logRequest(ForwardingRequest request, String query, boolean rssAsked) {
        final String clientIp = RequestUtils.getClientIp(request);

        if (!RequestUtils.isF5Request(request)) {
            final String forumId = getSite(request).getForumId();
            final String searchText = request.getParameter(QS_TEXT);

            SearchLog searchLog = new SearchLog();

            searchLog.setForumId(forumId);
            searchLog.setClientIp(clientIp);
            searchLog.setUserAgent(request.getHeader(HttpHeader.USER_AGENT));
            searchLog.setReferer(request.getHeader(HttpHeader.REFERER));

            searchLog.setSearchText(searchText);
            searchLog.setSearchNick(request.getParameter(QS_NICK));
            searchLog.setSearchHost(request.getParameter(QS_HOST));

            searchLog.setSearchQuery(query);
            searchLog.setSearchQueryString(request.getQueryString());

            searchLog.setRssAsked(rssAsked);

            final boolean isAdminReq = RequestUtils.isPowerUser(request);
            searchLog.setAdminRequest(isAdminReq);

            auditLogic.logSearchEvent(searchLog);

            if (StringUtils.isNotEmpty(searchText) && !rssAsked && !isAdminReq) {
                if (!obsceneAnalyzer.containsObsceneWord(searchText)) {
                    appLogic.saveSearchTextForAutocomplete(forumId, searchText);
                } else {
                    log.info("! Search by obscene words: {" + searchText + "}, ip=" + clientIp);
                }
            }
        } else {
            log.warn("F5 search: ip=" + clientIp + ", query=" + query);
        }
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
