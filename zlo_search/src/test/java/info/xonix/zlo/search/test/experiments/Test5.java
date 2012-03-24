package info.xonix.zlo.search.test.experiments;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.junit.Test;

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
        TokenStream ts = analyzer.tokenStream("body", new StringReader(s));
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

    public static void m4() {
//        SearchLogicImpl zs = new SearchLogicImpl(Site.forName("zlo"));
//        System.out.println(zs.search(-1, "тест", true, true, false, false, false, null, null).getHits().length());
        String forumId = "zlo";
        DoubleIndexManager dis = DoubleIndexManager.create(forumId, null);
        IndexSearcher is = new IndexSearcher(dis.getBigReader());
/*        try {
            Hits hits = is.search(new QueryParser("body", config.getMessageAnalyzer()).parse("body:тест title:тест"), Sort.INDEXORDER);

            for (int i = 1; i < 10; i++) {
                System.out.println(hits.doc(hits.length() - i).get("num"));
            }

            System.out.println(hits.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }
}
