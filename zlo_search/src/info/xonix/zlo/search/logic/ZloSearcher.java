package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.*;
import info.xonix.zlo.search.utils.TimeUtils;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher /*extends SiteSource*/ {
    private static final Logger log = Logger.getLogger(ZloSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

/*    private Site site;

    public ZloSearcher(Site site) {
//        super(site);
        this.site = site;
    }*/
    private SiteFactory<DoubleIndexSearcher> doubleIndexSearcherFactory = new SiteFactory<DoubleIndexSearcher>() {
        @Override
        protected DoubleIndexSearcher create(Site site) {
            return new DoubleIndexSearcher(site, getDateSort());
        }
    };

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            log.error("Error while closing index reader: " + e.getClass());
        }
    }

/*    public SearchResult search(int topicCode,
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

        return search(Message.formQueryString(text, inTitle, inBody, topicCode, nick, host, fromDate, toDate, inReg, inHasUrl, inHasImg), searchAll);
    }*/

    public SearchResult search(SearchRequest req) {
        /*return search(
                req.getTopicCode(),
                req.getText(),
                req.isInTitle(),
                req.isInBody(),
                req.isInReg(),
                req.isInHasUrl(),
                req.isInHasImg(),
                req.getNick(),
                req.getHost(),
                req.getFromDate(),
                req.getToDate(),
                req.isSearchAll()
        );*/
        return search(
                req.getSite(),
                Message.formQueryString(
                        req.getText(), req.isInTitle(), req.isInBody(),
                        req.getTopicCode(), req.getNick(), req.getHost(),
                        req.getFromDate(), req.getToDate(),
                        req.isInReg(), req.isInHasUrl(), req.isInHasImg()),
                req.isSearchAll());
    }

/*    public SearchResult search(int topicCode,
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
    }*/

    public static Sort getDateSort() {
        // sort causes slow first search & lot memory used!
        return Config.SEARCH_PERFORM_SORT
                ? new Sort(new SortField(MessageFields.DATE, SortField.STRING, true))
                : null;
    }

    private SearchResult search(Site site, String queryStr, boolean searchAll) {
        Assert.notNull(site, "site can't be null!");

        if (!Config.USE_DOUBLE_INDEX) {
            throw new RuntimeException("Old!!!");
        } else {
            return searchDoubleIndex(site, queryStr, null, searchAll);
        }
    }

    private SearchResult searchDoubleIndex(Site site, String queryStr, Sort sort, boolean searchAll) {
        if (sort == null)
            sort = getDateSort();

        SearchResult result = new SearchResult();
        try {
            Analyzer analyzer = Message.constructAnalyzer();

            QueryParser parser = new QueryParser(MessageFields.BODY, analyzer);
            parser.setDefaultOperator(searchAll ? QueryParser.AND_OPERATOR : QueryParser.OR_OPERATOR);

            Query query = parser.parse(queryStr);

            log.info("query: " + query);

            DoubleIndexSearcher dis = getDoubleIndexSearcher(site);

            result.setAnalyzer(analyzer);
            result.setQueryParser(parser);
            result.setQuery(query);
            result.setDoubleIndexSearcher(dis);
            result.setHits(dis.search(query));
            result.setSearchDateNow();
        } catch (ParseException e) {
            throw new SearchException(queryStr, e);
        } catch (IOException e) {
            log.error(e);
        }
        return result;
    }

/*    private DoubleIndexSearcher doubleIndexSearcher;

    public DoubleIndexSearcher getDoubleIndexSearcher() {
        if (doubleIndexSearcher == null) {
            doubleIndexSearcher = new DoubleIndexSearcher(site, getDateSort());
        }
        return doubleIndexSearcher;
    }*/

    public void optimizeIndex(Site site) {
        DoubleIndexSearcher dis = getDoubleIndexSearcher(site);
/*                int lastIndexedInDb = DbManager.getLastIndexedNumber();
                int lastIndexedInIndex = ZloSearcher.getLastIndexedNumber();
                if (lastIndexedInIndex != lastIndexedInDb) {
                    logger.warn(MessageFormat.format("Last indexed nums not equal! db={0}, index={1}", lastIndexedInDb, lastIndexedInIndex));
                    DbManager.setLastIndexedNumber(lastIndexedInIndex);
                }*/
        try {
            dis.moveSmallToBig();
            dis.optimize();
        } catch (IOException e) {
            log.error("Error while optimizingIndex", e);
        }
        // VVV --- won't close - as it closes dis for websearch
//        dis.close();
    }

    public void dropIndex(Site site) throws IOException {
        DoubleIndexSearcher dis = getDoubleIndexSearcher(site);
        dis.drop();
        dis.close();
    }

    /**
     * TODO: make private!
     *
     * @param site
     * @return
     */
    public DoubleIndexSearcher getDoubleIndexSearcher(Site site) {
        return doubleIndexSearcherFactory.get(site);
    }
}
