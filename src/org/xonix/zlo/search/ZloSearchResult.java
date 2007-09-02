package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;

import java.util.List;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:59:08
 */
public class ZloSearchResult {
    List<ZloMessage> msgs;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private QueryParser parser;
    private Query query;

    public ZloSearchResult(List<ZloMessage> msgs) {
        this.msgs = msgs;
    }

    public List<ZloMessage> getMsgs() {
        return msgs;
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
}
