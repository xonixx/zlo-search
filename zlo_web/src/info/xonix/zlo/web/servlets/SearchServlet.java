package info.xonix.zlo.web.servlets;

import de.nava.informa.core.CategoryIF;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.impl.basic.Category;
import de.nava.informa.impl.basic.Channel;
import de.nava.informa.impl.basic.Item;
import info.xonix.zlo.search.*;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbAccessor;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.db.DbManager;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.utils.HtmlUtils;
import info.xonix.zlo.web.CookieUtils;
import info.xonix.zlo.web.RequestCache;
import info.xonix.zlo.web.rss.ZloRss20Exporter;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

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
    public static final String REQ_PAGE_SIZE = QS_PAGE_SIZE;

    public static final String ERROR = "error";
    public static final String DEBUG = "debug";

    public static final String JSP_SEARCH = "/WEB-INF/jsp/Search.jsp";

    public static DateFormat FROM_TO_DATE_FORMAT = Config.DateFormats.DF_3;

    private static RequestCache cache = new RequestCache();

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        String topicCodeStr = request.getParameter(QS_TOPIC_CODE);
        String text = request.getParameter(QS_TEXT);

        boolean inTitle = StringUtils.isNotEmpty(request.getParameter(QS_IN_TITLE));
        boolean inBody = StringUtils.isNotEmpty(request.getParameter(QS_IN_BODY));

        boolean inReg = StringUtils.isNotEmpty(request.getParameter(QS_IN_REG));
        boolean inHasUrl = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_URL));
        boolean inHasImg = StringUtils.isNotEmpty(request.getParameter(QS_IN_HAS_IMG));

        boolean isRssAsked = request.getParameter(QS_RSS) != null;

        String nick = request.getParameter(QS_NICK);
        String host = request.getParameter(QS_HOST);
        String fromDateStr = request.getParameter(QS_FROM_DATE);
        String toDateStr = request.getParameter(QS_TO_DATE);
        String pageSizeStrInd = request.getParameter(QS_PAGE_SIZE);

        ErrorMessage errorMsg = null;
        request.setAttribute(DEBUG, Config.DEBUG);

        // to refresh site from cookies to req
        setSiteInReq(request, response);

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
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        ;
                    }
                } else {
                    pageSize = Integer.parseInt(Config.NUMS_PER_PAGE[0]);
                }
            }
            request.setAttribute(REQ_PAGE_SIZE, pageSize);

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

            request.setAttribute(QS_TO_DATE, FROM_TO_DATE_FORMAT.format(toDate));
            request.setAttribute(QS_FROM_DATE, FROM_TO_DATE_FORMAT.format(fromDate));

            text = preprocessSearchText(text, searchType);
            nick = preprocessSearchNick(nick);

            SearchRequest searchRequest = new SearchRequest(
                    getSite(request), text,
                    inTitle, inBody, inReg, inHasUrl, inHasImg,
                    nick, host, topicCode, fromDate, toDate,
                    SEARCH_TYPE_ALL.equals(searchType));

            if (searchRequest.canBeProcessed()) {

                request.setAttribute(REQ_HIGHLIGHT_WORDS, FoundTextHighlighter.formHighlightedWords(text));

                if (StringUtils.isEmpty(request.getParameter(QS_DATES))) {
                    searchRequest.setFromDate(null);
                    searchRequest.setToDate(null);
                }

                int searchHash = searchRequest.hashCode();

                SearchResult searchResult;
                SearchResult prevSearchResult = cache.get(searchHash);

                if (prevSearchResult == null
                        || prevSearchResult.isNotTheSameSearch(searchRequest)
                        || prevSearchResult.isOld() // this should enforce re-search while indexing, so VV seems not needed more
//                        || StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER)) // not turning pages, but searching // commenting by now todo: check if this is not needed more
                        ) {

                    try {
                        searchResult = searchRequest.performSearch();
                    } catch (BooleanQuery.TooManyClauses e) { // �������� ��� � ������ ������ +�*
                        errorMsg = ErrorMessage.TooComplexSearch;
                        throw e;
                    } catch (ZloSearcher.ParseException e) {
                        errorMsg = ErrorMessage.InvalidQueryString;
                        errorMsg.setData(e.getQuery());
                        throw e;
                    }

                    cache.put(searchHash, searchResult);
                } else {
                    searchResult = prevSearchResult;
                    searchResult.setNewSearch(false); // means we use result of previous search
                    logger.info("Cached search: " + searchResult.getQuery());
                }

                if (searchResult != null) {
                    // log only initial search. Moved here - because now cached for all users,
                    // but log need to be performed nevertheless for every user
                    if (StringUtils.isEmpty(request.getParameter(QS_PAGE_NUMBER))) {
                        logRequest(request, searchResult.getQuery().toString());
                    }

                    request.setAttribute(REQ_SEARCH_RESULT, searchResult);

                    ZloPaginatedList paginatedList = (ZloPaginatedList) searchResult.createPaginatedList(getSite(request));

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

                    paginatedList.refreshCurrentList();
                } else {
                    logger.error("searchResult == null. This should not happen!");
                }
            } else if (StringUtils.isNotEmpty(request.getParameter(QS_SUBMIT))) {
                errorMsg = ErrorMessage.MustSelectCriterion;
                throw new Exception();
            } else {
                request.setAttribute(REQ_SEARCH_RESULT, null);
                showStatistics(request);
            }
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

        if (isRssAsked)
            formRss(request, response);
        else
            request.forwardTo(JSP_SEARCH);
    }

    private void formRss(ForwardingRequest request, HttpServletResponse response) {
        if (request.getAttribute(ERROR) != null) {
            // process error
        } else {
            response.setContentType("application/rss+xml");
            response.setCharacterEncoding("windows-1251");

            SearchResult searchResult = (SearchResult) request.getAttribute(REQ_SEARCH_RESULT);
            ZloPaginatedList pl = (ZloPaginatedList) searchResult.getPaginatedList();
            List msgsList = pl.getList();
            Date firstMsgDate = msgsList != null && msgsList.size() > 0 ? ((ZloMessage) msgsList.get(0)).getDate() : null; // the youngest msg (max date)

            logger.info("RSS request. User-Agent: " + request.getHeader("User-Agent") + ", If-Modified-Since: " + request.getHeader("If-Modified-Since"));

            if (firstMsgDate != null) {
                Date lastModifiedDate = new Date(request.getDateHeader("If-Modified-Since"));

                if (lastModifiedDate.before(firstMsgDate)) {
                    response.setDateHeader("Last-Modified", firstMsgDate.getTime());
                    logger.info("Feed is modified, lastMsgFeedDate=" + firstMsgDate);
                } else {
                    logger.info("Sending 304 Not modified: If-Modified-Since Date is _not before_ lastMsgFeedDate=" + firstMsgDate);
                    response.setStatus(304); // Not Modified
                    return;
                }
            }

            SearchRequest lastSearch = searchResult.getLastSearch();

            ChannelIF ch = new Channel();

            List<String> l = new ArrayList<String>(3);
            for (String s : Arrays.asList(lastSearch.getText(), lastSearch.getNick(), lastSearch.getHost())) {
                if (StringUtils.isNotEmpty(s))
                    l.add(s);
            }
            String chTitle = "Board search: " + StringUtils.join(l, ", ");

            ch.setTitle(chTitle);

            try {
                ch.setLocation(new URL(String.format("http://%s/search?%s", Config.WEBSITE_DOMAIN, request.getQueryString().replace("rss&", ""))));
                ch.setDescription(lastSearch.describeToString());
                ch.setLanguage("ru");
                ch.setTtl(120); // 2 hours

                FoundTextHighlighter hl = new FoundTextHighlighter();
                hl.setHighlightWords(FoundTextHighlighter.formHighlightedWords(lastSearch.getText()));

                if (msgsList != null) {
                    for (Object m1 : msgsList) {
                        ZloMessage m = (ZloMessage) m1;
                        Item it = new Item();

                        Site s = m.getSite();

                        // highlighting of all feed takes to many time & cpu
                        it.setTitle(HtmlUtils.unescapeHtml(m.getTitle()));
                        it.setDescription(m.getBody());
                        it.setCreator(m.getNick() + "@" + m.getHost());
                        it.setDate(m.getDate());
                        it.setCategories(Arrays.asList((CategoryIF) new Category(m.getTopic())));
                        it.setLink(new URL(String.format("http://%s/msg?site=%s&num=%s&hw=%s", Config.WEBSITE_DOMAIN, s.getNum(), m.getNum(), HtmlUtils.urlencode(hl.getWordsStr()))));
                        it.setComments(new URL(String.format("http://%s%s%s", s.getSITE_URL(), s.getREAD_QUERY(), m.getNum())));

                        ch.addItem(it);
                    }
                }

                ZloRss20Exporter exporter = new ZloRss20Exporter(response.getWriter(), "windows-1251");

                exporter.write(ch);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showStatistics(ForwardingRequest request) throws DbException {
        DbManager dbm = getSite(request).getDbManager();
        request.setAttribute(QS_LAST_MSGS, new int[]{dbm.getLastMessageNumber(), dbm.getLastIndexedNumber()});
        request.setAttribute(QS_LAST_MSGS_DATES, new Date[]{dbm.getLastSavedDate(), dbm.getLastIndexedDate()});
    }

    private void logRequest(ForwardingRequest request, String query) {
        try {
            DbAccessor.getInstance("search_log").getDbManager().logRequest(
                    getSite(request).getNum(),
                    RequestUtils.getClientIp(request),
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
