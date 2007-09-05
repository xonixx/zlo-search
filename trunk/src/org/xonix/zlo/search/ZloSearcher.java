package org.xonix.zlo.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 2:24:05
 */
public class ZloSearcher {
    private final static ZloSearcher ZLO_SEARCHER_INSTANCE = new ZloSearcher();
    public final static SimpleDateFormat QUERY_DATEFORMAT = new SimpleDateFormat("M.d.yy"); // because of locale

    public static ZloSearchResult search(String queryString) {
        return ZLO_SEARCHER_INSTANCE.search0(queryString);
    }

    public static ZloSearchResult search(String topicCode,
                                         String title,
                                         String body,
                                         String nick,
                                         String host,
                                         Date fromdDate,
                                         Date toDate) {
        StringBuilder res = new StringBuilder();

        if (StringUtils.isNotEmpty(body))
            res.append("+body:(").append(body).append(")");

        if (!"0".equals(topicCode)) {
            res.append(" +topic:").append(topicCode);
        }

        if (StringUtils.isNotEmpty(title))
            res.append(" +title:(").append(title).append(")");

        if (StringUtils.isNotEmpty(nick))
            res.append(" +nick:").append(nick);

        if (StringUtils.isNotEmpty(host))
            res.append(" +host:").append(host);

        if (fromdDate != null && toDate != null)
            res.append(" +date:[").append(QUERY_DATEFORMAT.format(fromdDate)).append(" TO ").append(QUERY_DATEFORMAT.format(toDate)).append("]");

        return ZLO_SEARCHER_INSTANCE.search0(res.toString());
    }

    public static ZloSearchResult search(String topicCode,
                                         String title,
                                         String body,
                                         String nick,
                                         String host) {
        return search(topicCode, title, body, nick, host, null, null);
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

            Hits hits = searcher.search(query, new Sort(ZloMessage.DATE, false));

            // TODO: this must be lazy!!! ---VVV
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
        String query = "title:���� OR title:���� AND nick:jaga";
//        query = "num:3675166";
//        query = "title:\"������\"";
//        query = "nick:�������� OR title:������";
//        query = "nick:\"downtube 4130 chromoly\"";
//        query = "num:3765293";
//        query = "1946";
        query="topic:�����������";
        System.out.println(query);
        ZloSearcher searcher = new ZloSearcher();
        for (ZloMessage msg: ZloSearcher.search(query).getMsgs()) {
            System.out.println(msg);
        }
    }
}
