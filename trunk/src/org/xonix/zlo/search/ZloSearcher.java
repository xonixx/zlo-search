package org.xonix.zlo.search;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

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
    private static final Logger logger = Logger.getLogger(ZloSearcher.class);

    private final static ZloSearcher ZLO_SEARCHER_INSTANCE = new ZloSearcher();
    public final static SimpleDateFormat QUERY_DATEFORMAT = new SimpleDateFormat("yyyyMMdd"); // because of locale
    private final static IndexReader INDEX_READER;

    static {
        IndexReader _ir;
        try {
            _ir = IndexReader.open(Config.INDEX_DIR);
        } catch (IOException e) {
            _ir = null;
            logger.error("Error while creating index reader: " + e);
        }
        INDEX_READER = _ir;
    }

    public static class ParseException extends RuntimeException {
        private String query;

        public ParseException(String query, Throwable cause) {
            super(cause);
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

    public static ZloSearchResult search(String queryString) {
        return ZLO_SEARCHER_INSTANCE.search0(queryString);
    }

    public static ZloSearchResult search(int topicCode,
                                         String text,
                                         boolean inTitle,
                                         boolean inBody,
                                         boolean inReg,
                                         boolean inHasUrl,
                                         boolean inHasImg,
                                         String nick,
                                         String host,
                                         Date fromDate,
                                         Date toDate) {
        StringBuilder queryStr = new StringBuilder();

        if (StringUtils.isNotEmpty(text)) {
            if (inTitle && !inBody)
                queryStr.append(" +title:(").append(text).append(")");

            else if (!inTitle && inBody)
                queryStr.append(" +body:(").append(text).append(")");

            else if (inTitle && inBody)
                queryStr.append(" +(body:(").append(text).append(") OR (title:(").append(text).append(")))");

            else // !inTitle && !inBody
                queryStr.append(" +title:(").append(text).append(")")
                        .append(" +body:(").append(text).append(")");
        }

        if (-1 != topicCode) {
            queryStr.append(" +topic:").append(topicCode);
        }

        if (StringUtils.isNotEmpty(nick))
            queryStr.append(" +nick:\"").append(nick).append("\"");

        if (StringUtils.isNotEmpty(host))
            queryStr.append(" +host:").append(host);

        if (fromDate != null && toDate != null)
            queryStr.append(" +date:[").append(QUERY_DATEFORMAT.format(fromDate)).append(" TO ").append(QUERY_DATEFORMAT.format(toDate)).append("]");

        if (inReg)
            queryStr.append(" +reg:1");

        if (inHasUrl)
            queryStr.append(" +url:1");

        if (inHasImg)
            queryStr.append(" +img:1");

        return ZLO_SEARCHER_INSTANCE.search0(queryStr.toString());
    }

    public static ZloSearchResult search(SearchRequest searchRequest) {
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
                searchRequest.getToDate()
        );
    }

    public static ZloSearchResult search(int topicCode,
                                         String text,
                                         boolean inTitle,
                                         boolean inBody,
                                         boolean inReg,
                                         boolean inHasUrl,
                                         boolean inHasImg,
                                         String nick,
                                         String host) {
        return search(topicCode, text, inTitle, inBody,
                inReg, inHasUrl, inHasImg, nick, host, null, null);
    }

    private ZloSearchResult search0(String queryStr) {
        ZloSearchResult result = new ZloSearchResult();
        IndexSearcher searcher = null;
        try {
            searcher = new IndexSearcher(INDEX_READER);
            Analyzer analyzer = ZloMessage.constructAnalyzer();
            QueryParser parser = new QueryParser(ZloMessage.BODY, analyzer);
            Query query = parser.parse(queryStr);

            result.setSearcher(searcher);
            result.setAnalyzer(analyzer);
            result.setQueryParser(parser);
            result.setQuery(query);

            Hits hits = searcher.search(query, new Sort(new SortField(ZloMessage.DATE, SortField.STRING, true)));
            result.setHits(hits);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.lucene.queryParser.ParseException e) {
            throw new ParseException(queryStr, e);
        } finally {
            try {
                if (searcher != null)
                    searcher.close();
            } catch (IOException e) {
                logger.warn("Error while closing searcher: " + e);
            }
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
