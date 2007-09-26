package org.xonix.zlo.search;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher {
    private final static ZloSearcher ZLO_SEARCHER_INSTANCE = new ZloSearcher();
    public final static SimpleDateFormat QUERY_DATEFORMAT = new SimpleDateFormat("yyyyMMdd"); // because of locale

    public static ZloSearchResult search(String queryString) {
        return ZLO_SEARCHER_INSTANCE.search0(queryString);
    }

    public static ZloSearchResult search(String topicCode,
                                         String title,
                                         String body,
                                         String nick,
                                         String host,
                                         Date fromDate,
                                         Date toDate) {
        StringBuilder res = new StringBuilder();

        if (StringUtils.isNotEmpty(body))
            res.append("+body:(").append(body).append(")");

        if (StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode)) {
            res.append(" +topic:").append(topicCode);
        }

        if (StringUtils.isNotEmpty(title))
            res.append(" +title:(").append(title).append(")");

        if (StringUtils.isNotEmpty(nick))
            res.append(" +nick:\"").append(nick).append("\"");

        if (StringUtils.isNotEmpty(host))
            res.append(" +host:").append(host);

        if (fromDate != null && toDate != null)
            res.append(" +date:[").append(QUERY_DATEFORMAT.format(fromDate)).append(" TO ").append(QUERY_DATEFORMAT.format(toDate)).append("]");

        ZloSearchResult result = ZLO_SEARCHER_INSTANCE.search0(res.toString());
        result.setTopicCode(topicCode);
        result.setTitle(title);
        result.setBody(body);
        result.setNick(nick);
        result.setHost(host);
        result.setFromDate(fromDate);
        result.setToDate(toDate);
        return result;
    }

    public static ZloSearchResult search(String topicCode,
                                         String title,
                                         String body,
                                         String nick,
                                         String host) {
        return search(topicCode, title, body, nick, host, null, null);
    }

    private ZloSearchResult search0(String queryStr) {
        ZloSearchResult result = new ZloSearchResult();
        try {
            IndexReader reader = IndexReader.open(Config.INDEX_DIR);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = ZloMessage.constructAnalyzer();
            QueryParser parser = new QueryParser(ZloMessage.BODY, analyzer);
            Query query = parser.parse(queryStr);

            result.setSearcher(searcher);
            result.setAnalyzer(analyzer);
            result.setQueryParser(parser);
            result.setQuery(query);

            Hits hits = searcher.search(query, new Sort(ZloMessage.DATE, false));
            result.setHits(hits);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ZloMessage searchMsgByNum(int urlNum) {
        try {
            return search("+num:" + ZloMessage.URL_NUM_FORMAT.format(urlNum)).iterator().next().getMessage(); // returns 1 result
        } catch (NoSuchElementException e) { // 0 results found => msg with such num not indexed
            return null;
        }
    }

    public static ZloSearchResult searchInNumRange(int urlFrom, int urlTo) {
        return search("+num:[" + ZloMessage.URL_NUM_FORMAT.format(urlFrom) 
                + " TO " + ZloMessage.URL_NUM_FORMAT.format(urlTo) + "]");
    }

    public static void main(String[] args) {
        String query = "title:днем OR title:день AND nick:jaga";
//        query = "num:3675166";
//        query = "title:\"анонсы\"";
//        query = "nick:голодный OR title:батьку";
//        query = "nick:\"downtube 4130 chromoly\"";
//        query = "num:3765293";
//        query = "1946";
        query="topic:–азвлечени€";
        System.out.println(query);
        ZloSearcher searcher = new ZloSearcher();
/*        for (ZloMessage msg: ZloSearcher.search(query).iterator()) {
            System.out.println(msg);
        }*/
    }
}
