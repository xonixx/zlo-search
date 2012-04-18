package info.xonix.zlo.search.logic;

import info.xonix.utils.Check;
import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domain.SearchRequest;
import info.xonix.zlo.search.domain.SearchResult;
import info.xonix.zlo.search.domain.SortBy;
import info.xonix.zlo.search.index.IndexManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;

import static java.text.MessageFormat.format;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 * TODO: int <-> long
 */
public class SearchLogicImpl implements SearchLogic, InitializingBean {
    private static final Logger log = Logger.getLogger(SearchLogicImpl.class);
    private static final int MAX_LIMIT = 500;
    public static final Sort REVERSED_INDEX_ORDER_SORT = new Sort(new SortField(null, SortField.DOC, true));

//    test --VVV
//    public static final Sort REVERSED_INDEX_ORDER_SORT = Sort.INDEXORDER;
//    public static final Sort REVERSED_INDEX_ORDER_SORT = new Sort(new SortField(MessageFields.URL_NUM, SortField.STRING, true));

    @Autowired
    private Config config;

    public static String formQueryString(String text, boolean isRoot, boolean inTitle, boolean inBody, int topicCode,
                                         String nick, String host, Date fromDate, Date toDate,
                                         boolean inReg, boolean inHasUrl, boolean inHasImg) {

        text = FoundTextHighlighter.escapeColon(text);

        StringBuilder queryStr = new StringBuilder();

        nick = StringUtils.lowerCase(nick);
        host = StringUtils.lowerCase(host);

        if (StringUtils.isNotEmpty(text)) {
            if (inTitle && !inBody) {
                queryStr.append(format(" +{0}:({1})", MessageFields.TITLE, text));

            } else if (!inTitle && inBody) {
                queryStr.append(format(" +{0}:({1})", MessageFields.BODY, text));

            } else if (inTitle && inBody) {
                queryStr.append(format(" +({0}:({2}) OR {1}:({2}))", MessageFields.TITLE, MessageFields.BODY, text));

            } else { // !inTitle && !inBody
                queryStr.append(format(" +{0}:({2}) +{1}:({2})", MessageFields.TITLE, MessageFields.BODY, text));
            }
        }

        if (-1 != topicCode) {
            queryStr.append(format(" +{0}:{1}", MessageFields.TOPIC_CODE, topicCode));
        }

        if (StringUtils.isNotEmpty(nick)) {
            queryStr.append(format(" +{0}:(\"{1}\")", MessageFields.NICK, nick));
        }

        if (StringUtils.isNotEmpty(host)) {
            if (host.startsWith("*") || host.startsWith("?")) {
                final String hostReversed = StringUtils.reverse(host);
                queryStr.append(format(" +{0}:({1})", MessageFields.HOST_REVERSED, hostReversed));
            } else {
                queryStr.append(format(" +{0}:({1})", MessageFields.HOST, host));
            }
        }

        if (fromDate != null && toDate != null) {
            queryStr.append(format(" +{0}:[{1,date,yyyyMMdd} TO {2,date,yyyyMMdd}]", MessageFields.DATE, fromDate, toDate));
        }

        if (isRoot) {
            queryStr.append(format(" +{0}:1", MessageFields.IS_ROOT));
        }

        if (inReg) {
            queryStr.append(format(" +{0}:1", MessageFields.REG));
        }

        if (inHasUrl) {
            queryStr.append(format(" +{0}:1", MessageFields.HAS_URL));
        }

        if (inHasImg) {
            queryStr.append(format(" +{0}:1", MessageFields.HAS_IMG));
        }

        return queryStr.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
    }

    @Override
    public SearchResult search(SearchRequest req, int limit) throws SearchException {
        final SearchResult searchResult = search(
                req.getForumId(),

                formQueryString(
                        req.getText(), req.isRoot(), req.isInTitle(), req.isInBody(),
                        req.getTopicCode(), req.getNick(), req.getHost(),
                        req.getFromDate(), req.getToDate(),
                        req.isInReg(), req.isInHasUrl(), req.isInHasImg()),

                req.isSearchAll(),
                req.getSortDirection(),
                limit);
        searchResult.setLastSearch(req);
        return searchResult;
    }

    @Override
    public Sort getDateSort() {
        // sort causes slow first search & lot memory used!
        return config.isSearchPerformSort()
                ? new Sort(new SortField(MessageFields.DATE, SortField.STRING, true))
                : null;
    }

    private SearchResult search(String forumId, String queryStr, boolean searchAll, SortBy sortDirection, int limit) throws SearchException {
        Assert.notNull(forumId, "site can't be null!");

        return searchIndex(forumId, queryStr, searchAll, sortDirection, limit);
    }

    private SearchResult searchIndex(String forumId, String queryStr, boolean searchAll, SortBy sortDirection, int limit) throws SearchException {
        SearchResult result;
        try {
            QueryParser parser = getQueryParser();
            parser.setDefaultOperator(searchAll ? QueryParser.AND_OPERATOR : QueryParser.OR_OPERATOR);

            Query query = parser.parse(queryStr);

            log.info("query: " + query);

            IndexManager indexManager = IndexManager.get(forumId);

            result = new SearchResult(forumId, query, indexManager.search(query, sortDirection, limit));
        } catch (ParseException e) {
            throw new SearchException(queryStr, e);
        } catch (IOException e) {
            log.error(e);
            throw new SearchException(queryStr, e);
        }
        return result;
    }

    private QueryParser getQueryParser() {
        return new QueryParser(LuceneVersion.VERSION, MessageFields.BODY, config.getMessageAnalyzer());
    }

    @Override
    public void optimizeIndex(String forumId) {
        // TODO: implement?
        throw new UnsupportedOperationException("optimizeIndex");
    }

    @Override
    public void dropIndex(String forumId) throws IOException {
        final IndexManager indexManager = IndexManager.get(forumId);
        indexManager.drop();
//        indexManager.close();
    }

    @Override
    public int[] search(String forumId, String searchString, int skip, int limit) throws SearchException {
        final QueryParser queryParser = getQueryParser();

        final Query query;
        try {
            query = queryParser.parse(searchString);
        } catch (ParseException e) {
            throw new SearchException("search: Wrong searchString", e);
        }

        int realLimit = skip + fixLimit(limit);

        final IndexManager indexManager = IndexManager.get(forumId);

        final int[] realLimitIds;
        try {
            realLimitIds = search(indexManager.getSearcher(), query, realLimit);
        } catch (IOException e) {
            throw new SearchException("search: I/O exception", e);
        }

        return skip(realLimitIds, skip);

/*        try {
    final int[] ids = search(searcher, query, realLimit);

    if (ids.length == realLimit) {
        return skip(ids, skip);
    }

    realLimit -= ids.length;

    if (realLimit <= 0) {
        throw new IllegalStateException();
    }

    final IndexSearcher bigSearcher = new IndexSearcher(indexManager.getBigReader());

    final int[] idsBig = search(bigSearcher, query, realLimit);


    final int[] idsAll = ArrayUtils.addAll(ids, idsBig);
    return skip(idsAll, skip); // TODO: optimize

} catch (IOException e) {
    throw new SearchException("search: I/O exception", e);
}*/
    }

/*    private int[] last(int[] inp, int takeLast) {
        return ArrayUtils.subarray(inp, inp.length - takeLast, inp.length);
    }*/

    private int[] skip(int[] inp, int skip) {
        return ArrayUtils.subarray(inp, skip, inp.length);
    }

    private int fixLimit(int limit) {
        // TODO?
        if (limit > MAX_LIMIT || limit <= 0) {
            limit = MAX_LIMIT;
        } /*else if (limit == -1) {
            limit = Integer.MAX_VALUE;
        }*/
        return limit;
    }

    private int[] search(IndexSearcher searcher, Query query, int limit) throws IOException {
        final TopDocs topDocs = searcher.search(query, null, limit, REVERSED_INDEX_ORDER_SORT);

        int[] ids = new int[topDocs.scoreDocs.length];
        for (int i = 0; i < ids.length; i++) {
            final ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            ids[i] = Integer.parseInt(searcher.doc(scoreDoc.doc).get(MessageFields.URL_NUM));
        }
        return ids;
    }
}
