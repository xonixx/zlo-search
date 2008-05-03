package info.xonix.zlo.search.test;

import info.xonix.zlo.search.model.ZloMessage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;

/**
 * Author: Vovan
 * Date: 04.05.2008
 * Time: 0:24:44
 */
public class Test5 {
    public static void main(String[] args) {
        Analyzer analyzer = ZloMessage.constructAnalyzer();
        QueryParser qp = new QueryParser("body", analyzer);
        try {
            System.out.println(qp.parse("nick:\"\\\\/\\\\/0\\\\/\\\\/KA\""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
