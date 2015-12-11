package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * User: xonix
 * Date: 11.12.15
 * Time: 22:49
 */
public class LuceneTests {
    private static Analyzer analyzer;

    @BeforeClass
    public static void setUp() {
        Config config = AppSpringContext.getApplicationContextForTesting().getBean(Config.class);
        analyzer = config.getMessageAnalyzer();
        System.out.println("Using analyzer: " + analyzer.getClass());
    }

    @Test
    public void testQP1() throws ParseException {
        QueryParser parser = newQueryParser(QueryParser.Operator.AND);

//        Query query = parser.parse("http://aaa/");
//        Query query = parser.parse("http://zlo.rt.mipt.ru/?read=9236097");
//        Query query = parser.parse("http.//zlo.rt.mipt.ru/.read=9236097");
//        Query query = parser.parse("http...zlo.rt.mipt.ru..read=9236097");
        Query query = parser.parse("/aaa/");
//        Query query = parser.parse(" +(title:(http.//zlo.rt.mipt.ru/.read=9236097) OR body:(http.//zlo.rt.mipt.ru/.read=9236097))");

        System.out.println(query);
    }

    private QueryParser newQueryParser(QueryParser.Operator defaultOperator) {
        QueryParser queryParser = new QueryParser(LuceneVersion.VERSION, MessageFields.BODY, analyzer);
        queryParser.setAutoGeneratePhraseQueries(true); // rt.mipt.ru -> "rt mipt ru"
        queryParser.setDefaultOperator(defaultOperator);
        return queryParser;
    }
}
