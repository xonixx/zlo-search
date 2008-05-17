package info.xonix.zlo.search.test;

import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.RussianWithNumbersAndSpecialStopWordsAnalyzer;
import info.xonix.zlo.search.ZloIndexer;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.dao.Site;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;

import java.io.StringReader;
import java.io.IOException;
import java.io.File;

/**
 * Author: Vovan
 * Date: 04.05.2008
 * Time: 0:24:44
 */
public class Test5 {
    public static void main(String[] args) {
        m3();
    }

    public static void m3() {
        // test index
        Site zlo = Site.forName("zlo");
        zlo.setDB_VIA_CONTAINER(false);
        ZloIndexer zi = new ZloIndexer(zlo);

        zi.setAnalyzer(new RussianWithNumbersAndSpecialStopWordsAnalyzer(new String[0])); // nos stopWords=empty index with all words
        zi.setIndexPerTime(10000);
        zi.setIndexDir(new File("D:\\TEST\\JAVA\\ZloSearcher\\indexes\\__test\\no_stop_words"));

        try {
            zi.indexRange(0, zlo.getDbManager().getLastMessageNumber());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        String s = "в чем смысл жизни?";

//        Analyzer analyzer = ZloMessage.constructAnalyzer();
        Analyzer analyzer = new RussianWithNumbersAndSpecialStopWordsAnalyzer(new String[0]);

        showTokens(s, analyzer);

    }

    private static void showTokens(String s, Analyzer analyzer) {
        Token t;
        TokenStream ts = analyzer.tokenStream("body", new StringReader(s));
        try {
            while((t = ts.next()) != null) {
                System.out.println(t.termText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void m1() {
        Analyzer analyzer = ZloMessage.constructAnalyzer();
        QueryParser qp = new QueryParser("body", analyzer);
        try {
            System.out.println(qp.parse("nick:\"\\\\/\\\\/0\\\\/\\\\/KA\""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
