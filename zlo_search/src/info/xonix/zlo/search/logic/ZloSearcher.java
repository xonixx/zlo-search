package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.SearchRequest;
import info.xonix.zlo.search.model.SearchResult;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.io.IOException;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher /*extends SiteSource*/ {
    private static final Logger logger = Logger.getLogger(ZloSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

    private Site site;

    public ZloSearcher(Site site) {
//        super(site);
        this.site = site;
    }

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            logger.error("Error while closing index reader: " + e.getClass());
        }
    }

    public SearchResult search(int topicCode,
                               String text,
                               boolean inTitle,
                               boolean inBody,
                               boolean inReg,
                               boolean inHasUrl,
                               boolean inHasImg,
                               String nick,
                               String host,
                               Date fromDate,
                               Date toDate,
                               boolean searchAll) {

        return search(ZloMessage.formQueryString(text, inTitle, inBody, topicCode, nick, host, fromDate, toDate, inReg, inHasUrl, inHasImg), searchAll);
    }

    public SearchResult search(SearchRequest searchRequest) {
        return search(
                searchRequest.getTopicCode(),
                searchRequest.getText(),
                searchRequest.isInTitle(),
                searchRequest.isInBody(),
                searchRequest.isInReg(),
                searchRequest.isInHasUrl(),
                searchRequest.isInHasImg(),
                searchRequest.getNick(),
                searchRequest.getHost(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                searchRequest.isSearchAll()
        );
    }

    public SearchResult search(int topicCode,
                               String text,
                               boolean inTitle,
                               boolean inBody,
                               boolean inReg,
                               boolean inHasUrl,
                               boolean inHasImg,
                               String nick,
                               String host) {
        return search(topicCode, text, inTitle, inBody,
                inReg, inHasUrl, inHasImg, nick, host, null, null, false);
    }

    public static Sort getDateSort() {
        // sort causes slow first search & lot memory used!
        return Config.SEARCH_PERFORM_SORT
                ? new Sort(new SortField(ZloMessage.FIELDS.DATE, SortField.STRING, true))
                : null;
    }

    private SearchResult search(String queryStr, boolean searchAll) {
        if (!Config.USE_DOUBLE_INDEX) {
            throw new RuntimeException("Old!!!");
        } else {
            return searchDoubleIndex(queryStr, null, searchAll);
        }
    }

    private SearchResult searchDoubleIndex(String queryStr, Sort sort, boolean searchAll) {
        if (sort == null)
            sort = getDateSort();

        SearchResult result = new SearchResult();
        try {
            Analyzer analyzer = ZloMessage.constructAnalyzer();

            QueryParser parser = new QueryParser(ZloMessage.FIELDS.BODY, analyzer);
            parser.setDefaultOperator(searchAll ? QueryParser.AND_OPERATOR : QueryParser.OR_OPERATOR);

            Query query = parser.parse(queryStr);

            logger.info("query: " + query);

            DoubleIndexSearcher dis = getDoubleIndexSearcher();

            result.setAnalyzer(analyzer);
            result.setQueryParser(parser);
            result.setQuery(query);
            result.setDoubleIndexSearcher(dis);
            result.setHits(dis.search(query));
            result.setSearchDateNow();
        } catch (ParseException e) {
            throw new SearchException(queryStr, e);
        } catch (IOException e) {
            logger.error(e);
        }
        return result;
    }

    private DoubleIndexSearcher doubleIndexSearcher;

    public DoubleIndexSearcher getDoubleIndexSearcher() {
        if (doubleIndexSearcher == null) {
            doubleIndexSearcher = new DoubleIndexSearcher(site, getDateSort());
        }
        return doubleIndexSearcher;
    }
}
