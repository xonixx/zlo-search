package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.SearchRequest;
import info.xonix.zlo.search.domainobj.SearchResult;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.model.MessageFields;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class SearchLogicImpl implements SearchLogic, InitializingBean {
    private static final Logger log = Logger.getLogger(SearchLogicImpl.class);

    //    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));
    private Config config;

    public static String formQueryString(String text, boolean inTitle, boolean inBody, int topicCode, String nick, String host, Date fromDate, Date toDate, boolean inReg, boolean inHasUrl, boolean inHasImg) {
        text = FoundTextHighlighter.escapeColon(text);

        StringBuilder queryStr = new StringBuilder();

        nick = StringUtils.lowerCase(nick);
        host = StringUtils.lowerCase(host);

        if (StringUtils.isNotEmpty(text)) {
            if (inTitle && !inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.TITLE, text));

            else if (!inTitle && inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.BODY, text));

            else if (inTitle && inBody)
                queryStr.append(MessageFormat.format(" +({0}:({2}) OR {1}:({2}))", MessageFields.TITLE, MessageFields.BODY, text));

            else // !inTitle && !inBody
                queryStr.append(MessageFormat.format(" +{0}:({2}) +{1}:({2})", MessageFields.TITLE, MessageFields.BODY, text));
        }

        if (-1 != topicCode) {
            queryStr.append(MessageFormat.format(" +{0}:{1}", MessageFields.TOPIC_CODE, topicCode));
        }

        if (StringUtils.isNotEmpty(nick))
            queryStr.append(MessageFormat.format(" +{0}:(\"{1}\")", MessageFields.NICK, nick));

        if (StringUtils.isNotEmpty(host))
            queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.HOST, host));

        if (fromDate != null && toDate != null)
            queryStr.append(MessageFormat.format(" +{0}:[{1,date,yyyyMMdd} TO {2,date,yyyyMMdd}]", MessageFields.DATE, fromDate, toDate));

        if (inReg)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.REG));

        if (inHasUrl)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.HAS_URL));

        if (inHasImg)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.HAS_IMG));

        return queryStr.toString();
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
    }

    // TODO: move this to Site as it belongs to it
    private SiteFactory<DoubleIndexManager> doubleIndexManagerFactory = new SiteFactory<DoubleIndexManager>() {
        @Override
        protected DoubleIndexManager create(Site site) {
            return new DoubleIndexManager(site, getDateSort());
        }
    };

/*    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            log.error("Error while closing index reader: " + e.getClass());
        }
    }*/

    @Override
    public SearchResult search(SearchRequest req) throws SearchException {
        final SearchResult searchResult = search(
                req.getSite(),
                formQueryString(
                        req.getText(), req.isInTitle(), req.isInBody(),
                        req.getTopicCode(), req.getNick(), req.getHost(),
                        req.getFromDate(), req.getToDate(),
                        req.isInReg(), req.isInHasUrl(), req.isInHasImg()),
                req.isSearchAll());
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

    private SearchResult search(Site site, String queryStr, boolean searchAll) throws SearchException {
        Assert.notNull(site, "site can't be null!");

        return searchDoubleIndex(site, queryStr/*, null*/, searchAll);
    }

    private SearchResult searchDoubleIndex(Site site, String queryStr/*, Sort sort*/, boolean searchAll) throws SearchException {
        // TODO: do we need sorting here???
//        if (sort == null)
//            sort = getDateSort();

        SearchResult result;
        try {
            QueryParser parser = getQueryParser();
            parser.setDefaultOperator(searchAll ? QueryParser.AND_OPERATOR : QueryParser.OR_OPERATOR);

            Query query = parser.parse(queryStr);

            log.info("query: " + query);

            DoubleIndexManager dis = getDoubleIndexManager(site);

            result = new SearchResult(site, query, dis, dis.search(query));
        } catch (ParseException e) {
            throw new SearchException(queryStr, e);
        } catch (IOException e) {
            log.error(e);
            throw new SearchException(queryStr, e);
        }
        return result;
    }

    private QueryParser getQueryParser() {
        return new QueryParser(Version.LUCENE_29, MessageFields.BODY, config.getMessageAnalyzer());
    }

    @Override
    public void optimizeIndex(Site site) {
        DoubleIndexManager dis = getDoubleIndexManager(site);

        try {
            dis.moveSmallToBig();
            dis.optimize();
        } catch (IOException e) {
            log.error("Error while optimizingIndex", e);
        }
        // VVV --- won't close - as it closes dis for websearch
//        dis.close();
    }

    @Override
    public void dropIndex(Site site) throws IOException {
        final DoubleIndexManager dis = getDoubleIndexManager(site);
        dis.drop();
        dis.close();
    }

    @Override
    public int[] search(Site site, String searchString, int limit) throws SearchException {
        final QueryParser queryParser = getQueryParser();

        final Query query;
        try {
            query = queryParser.parse(searchString);
        } catch (ParseException e) {
            throw new SearchException("search: Wrong searchString", e);
        }

        if (limit == 0) {
            limit = 1000;
        } else if (limit == -1) {
            limit = Integer.MAX_VALUE;
        }
        final DoubleIndexManager doubleIndexManager = getDoubleIndexManager(site);

        final IndexSearcher smallSearcher = new IndexSearcher(doubleIndexManager.getSmallReader());

        try {
            final int[] ids = search(smallSearcher, query, limit);

            if (ids.length == limit) {
                return ids;
            }

            limit = limit - ids.length;

            if (limit <= 0) {
                throw new IllegalStateException();
            }

            final IndexSearcher bigSearcher = new IndexSearcher(doubleIndexManager.getBigReader());

            final int[] idsBig = search(bigSearcher, query, limit);
            return ArrayUtils.addAll(ids, idsBig);

        } catch (IOException e) {
            throw new SearchException("search: I/O exception", e);
        }
    }

    private int[] search(IndexSearcher smallSearcher, Query query, int limit) throws IOException {
        final TopDocs topDocs = smallSearcher.search(query, null, limit, Sort.INDEXORDER);

        int[] ids = new int[topDocs.scoreDocs.length];
        for (int i = 0; i < ids.length; i++) {
            final ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            ids[i] = Integer.parseInt(smallSearcher.doc(scoreDoc.doc).get(MessageFields.URL_NUM));
        }
        return ids;
    }

    /**
     * TODO: make private!
     *
     * @param site site
     * @return dis
     */
    @Override
    public DoubleIndexManager getDoubleIndexManager(Site site) {
        return doubleIndexManagerFactory.get(site);
    }
}
