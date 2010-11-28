package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.SearchRequest;
import info.xonix.zlo.search.domainobj.SearchResult;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageFields;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.factory.SiteFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher implements InitializingBean {
    private static final Logger log = Logger.getLogger(ZloSearcher.class);

//    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));
    private Config config;

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

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            log.error("Error while closing index reader: " + e.getClass());
        }
    }

    public SearchResult search(SearchRequest req) {
        return search(
                req.getSite(),
                Message.formQueryString(
                        req.getText(), req.isInTitle(), req.isInBody(),
                        req.getTopicCode(), req.getNick(), req.getHost(),
                        req.getFromDate(), req.getToDate(),
                        req.isInReg(), req.isInHasUrl(), req.isInHasImg()),
                req.isSearchAll());
    }

    public Sort getDateSort() {
        // sort causes slow first search & lot memory used!
        return config.isSearchPerformSort()
                ? new Sort(new SortField(MessageFields.DATE, SortField.STRING, true))
                : null;
    }

    private SearchResult search(Site site, String queryStr, boolean searchAll) {
        Assert.notNull(site, "site can't be null!");

//        if (!Config.USE_DOUBLE_INDEX) {
//            throw new RuntimeException("Old!!!");
//        } else {
        return searchDoubleIndex(site, queryStr, null, searchAll);
//        }
    }

    private SearchResult searchDoubleIndex(Site site, String queryStr, Sort sort, boolean searchAll) {
        // TODO: do we need sorting here???
        if (sort == null)
            sort = getDateSort();

        SearchResult result;
        try {
            Analyzer analyzer = config.getMessageAnalyzer();

            QueryParser parser = new QueryParser(MessageFields.BODY, analyzer);
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

    public void dropIndex(Site site) throws IOException {
        final DoubleIndexManager dis = getDoubleIndexManager(site);
        dis.drop();
        dis.close();
    }

    /**
     * TODO: make private!
     *
     * @param site site
     * @return dis
     */
    public DoubleIndexManager getDoubleIndexManager(Site site) {
        return doubleIndexManagerFactory.get(site);
    }
}
