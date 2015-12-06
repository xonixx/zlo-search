package info.xonix.zlo.search.test.experiments;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Author: Vovan
 * Date: 04.05.2008
 * Time: 0:24:44
 */
public class Test5 {
    private static final Config config = AppSpringContext.get(Config.class);

/*    public static void main(String[] args) {
        m4();
    }*/

    @Test
    public void m2() {
        String s = "в чем смысл жизни?";

        Analyzer analyzer = config.getMessageAnalyzer();

        showTokens(s, analyzer);

    }

    private static void showTokens(String s, Analyzer analyzer) {
        Token t;
        try {
            TokenStream ts = analyzer.tokenStream("body", new StringReader(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
/*        try {
            while ((t = ts.next()) != null) {
                System.out.println(t.termText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void m1() {
        Analyzer analyzer = config.getMessageAnalyzer();
        QueryParser qp = new QueryParser(LuceneVersion.VERSION, "body", analyzer);
        try {
            System.out.println(qp.parse("nick:\"\\\\/\\\\/0\\\\/\\\\/KA\""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
