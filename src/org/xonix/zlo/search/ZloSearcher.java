package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.utils.TimeUtils;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.text.MessageFormat;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher {
    private static final Logger logger = Logger.getLogger(ZloSearcher.class);

    public static final int PERIOD_RECREATE_INDEXER = TimeUtils.parseToMilliSeconds(Config.getProp("searcher.period.recreate.indexer"));

    private static long lastCreateTime = -1;
    private static boolean isReopening = false;
    private static IndexReader indexReader;

    public static IndexReader getIndexReader() {
        if (indexReader == null) {
            try {
                indexReader = IndexReader.open(Config.INDEX_DIR);
            } catch (IOException e) {
                logger.error("Error while creating index reader: " + e);
            }
        } else {
            try {
                if (needToRecreateReader()) {
                    synchronized(ZloSearcher.class) {
                        if (needToRecreateReader()) {
                            startReopeningThread();
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("IndexReader error", e);
            }
        }
        return indexReader;
    }

    private static boolean needToRecreateReader() throws IOException {
        return !indexReader.isCurrent()
                        && System.currentTimeMillis() - lastCreateTime > PERIOD_RECREATE_INDEXER
                        && !isReopening;
    }

    private static void startReopeningThread() {
        Thread t = new Thread(new Runnable() {

            public void run() {
                isReopening = true;

                logger.info("Start recreating indexReader in separate thread...");
                IndexReader _indexReader = null,
                            oldIndexReader;

                try {
                    _indexReader = IndexReader.open(Config.INDEX_DIR);
                } catch (IOException e) {
                    logger.error("Error while recreating index reader: " + e);
                }

                // search to form memory caches
                search(_indexReader, " +nick:абырвалг", null);
                oldIndexReader = indexReader;

                indexReader = _indexReader;
                lastCreateTime = System.currentTimeMillis();

                clean(oldIndexReader);
                logger.info("Successfuly recreated.");

                isReopening = false;
            }
        });

        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public static void clean(IndexReader ir) {
        try {
            if (ir != null)
                ir.close();
        } catch (IOException e) {
            logger.error("Error while closing index reader: " + e.getClass());
        }
    }

    public static void clean() {
        clean(indexReader);
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
        return search(null, queryString, null);
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

        return search(null, ZloMessage.formQueryString(text, inTitle, inBody, topicCode, nick, host, fromDate, toDate, inReg, inHasUrl, inHasImg), null);
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

    public static SearchResult search(IndexReader indexReader, String queryStr, Sort sort) {
        if (sort == null)
            sort = new Sort(new SortField(ZloMessage.FIELDS.DATE, SortField.STRING, true));

        if (indexReader == null)
            indexReader = getIndexReader();

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

            Hits hits = searcher.search(query, sort);
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
        return search(MessageFormat.format(" +num:[{0} TO {1}]", urlFrom, urlTo));
    }

    public static int getLastIndexedNumber() {
        String searchStr = MessageFormat.format(" +num:[{0} TO {1}]",
                ZloMessage.URL_NUM_FORMAT.format(0),
                ZloMessage.URL_NUM_FORMAT.format(999999999));
        try {
            return Integer.parseInt(
                    ZloSearcher.search(null, searchStr,
                            new Sort(new SortField(ZloMessage.FIELDS.URL_NUM, SortField.STRING, true))).getHits().doc(0).get(ZloMessage.FIELDS.URL_NUM));
        } catch (IOException e) {
            logger.error(e);
            return -1;
        }
    }
}
