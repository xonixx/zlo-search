package org.xonix.zlo.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher {
    private final static ZloSearcher ZLO_SEARCHER_INSTANCE = new ZloSearcher();

    public static ZloSearchResult search(String queryString) {
        return ZLO_SEARCHER_INSTANCE.search0(queryString);
    }

    private ZloSearchResult search0(String queryStr) {
        List<ZloMessage> msgs = new ArrayList<ZloMessage>();
        ZloSearchResult result = new ZloSearchResult(msgs);
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

            Hits hits = searcher.search(query);

            for (Iterator it = hits.iterator(); it.hasNext();) {
                msgs.add(ZloMessage.fromDocument(((Hit)it.next()).getDocument()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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
        for (ZloMessage msg: ZloSearcher.search(query).getMsgs()) {
            System.out.println(msg);
        }
    }
}
