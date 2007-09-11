package org.xonix.zlo.search.test;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import java.text.DateFormat;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 06.09.2007
 * Time: 0:48:24
 */
public class Test1 {
    public static void main(String[] args) {
        m4();
    }

    public static void m1(){
        QueryParser qp = new QueryParser("field1", new SimpleAnalyzer());
        try {
            Query q = qp.parse("[1.1.04 TO 5.30.05]");
            System.out.println(q.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        System.out.println(df.format(new Date()));
    }

    public static void m3() {
        //new StandardAnalyzer().
        System.out.println(new Date().hashCode());
    }

    public static void m4() {
            
    }
}
