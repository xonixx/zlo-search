package org.xonix.zlo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.Iterator;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:59:08
 */
public class ZloSearchResult implements Iterable {

    private Hits hits;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private QueryParser parser;
    private Query query;

    // search is performed for these criteria:
    private String topicCode;
    private String title;
    private String body;
    private String nick;
    private String host;
    private Date fromDate;
    private Date toDate;

    public ZloSearchResult() {
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    public void setSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void setQueryParser(QueryParser parser) {
        this.parser = parser;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public IndexSearcher getSearcher() {
        return searcher;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public QueryParser getParser() {
        return parser;
    }

    public Query getQuery() {
        return query;
    }

    private class MsgsIterator implements Iterator<ZloMessage> {
        Iterator hitsIterator = hits.iterator();

        public boolean hasNext() {
            return hitsIterator.hasNext();
        }

        public ZloMessage next() {
            try {
                return ZloMessage.fromDocument(((Hit) hitsIterator.next()).getDocument());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void remove() {
        }
    }

    public Iterator<ZloMessage> iterator() {
        return new MsgsIterator();
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isTheSameSearch(String topicCode,
                                     String title,
                                     String body,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return StringUtils.equals(this.topicCode, topicCode) &&
                StringUtils.equals(this.title, title) &&
                StringUtils.equals(this.body, body) &&
                StringUtils.equals(this.nick, nick) &&
                StringUtils.equals(this.host, host) &&
                (this.fromDate == fromDate || this.fromDate.equals(fromDate)) &&
                (this.toDate == toDate || this.toDate.equals(toDate));
    }

    public boolean isNotTheSameSearch(String topicCode,
                                     String title,
                                     String body,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return !isTheSameSearch(topicCode, title, body, nick, host, fromDate, toDate);
    }
}
