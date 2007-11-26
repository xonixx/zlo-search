package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
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

    public final static SimpleDateFormat QUERY_DATEFORMAT = new SimpleDateFormat("yyyyMMdd"); // because of locale
    private static IndexReader indexReader;

    public static IndexReader getIndexReader() {
        if (indexReader == null) {
            try {
                indexReader = IndexReader.open(Config.INDEX_DIR);
            } catch (IOException e) {
                logger.error("Error while creating index reader: " + e);
            }
        } else try {
            if (!indexReader.isCurrent()) {
                clean();
                logger.info("Reopen index reader...");
                return getIndexReader();
            }
        } catch (IOException e) {
            logger.error("IndexReader error", e);
        }
        return indexReader;
    }

    public static void clean() {
        try {
            if (indexReader != null)
                indexReader.close();
        } catch (IOException e) {
            logger.error("Error while closing index reader: " + e.getClass());
        }
        indexReader = null;
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

    public static SearchResult search(String queryString) {
        return search(getIndexReader(), queryString);
    }

    public static SearchResult search(int topicCode,
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

        return search(getIndexReader(), ZloMessage.formQueryString(text, inTitle, inBody, topicCode, nick, host, fromDate, toDate, inReg, inHasUrl, inHasImg));
    }

    public static SearchResult search(SearchRequest searchRequest) {
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

    public static SearchResult search(int topicCode,
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

    public static SearchResult search(IndexReader indexReader, String queryStr) {
        SearchResult result = new SearchResult();
        IndexSearcher searcher = null;
        try {
            searcher = new IndexSearcher(indexReader);
            Analyzer analyzer = ZloMessage.constructAnalyzer();
            QueryParser parser = new QueryParser(ZloMessage.FIELDS.BODY, analyzer);
            Query query = parser.parse(queryStr);

            result.setSearcher(searcher);
            result.setAnalyzer(analyzer);
            result.setQueryParser(parser);
            result.setQuery(query);

            Hits hits = searcher.search(query, new Sort(new SortField(ZloMessage.FIELDS.DATE, SortField.STRING, true)));
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

    public static SearchResult searchInNumRange(int urlFrom, int urlTo) {
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
