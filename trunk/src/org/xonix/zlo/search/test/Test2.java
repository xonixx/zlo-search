package org.xonix.zlo.search.test;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.commons.lang.time.DateFormatUtils;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.ZloSearchResult;
import org.xonix.zlo.search.ZloSearcher;

import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Author: gubarkov
 * Date: 06.09.2007
 * Time: 20:18:16
 */
public class Test2 {
    public static void main(String[] args) {
        try {
            IndexWriter iw = new IndexWriter("__test_index_1", new KeywordAnalyzer());
            iw.addDocument(getDoc1(0, 0));
            iw.addDocument(getDoc1(1, 5));
            iw.addDocument(getDoc1(-3, 7));
            iw.addDocument(getDoc1(4, -2));
            iw.optimize();
            iw.close();
            IndexReader ir = IndexReader.open("__test_index_1");
            IndexSearcher is = new IndexSearcher(ir);

            try {
                Query q = new QueryParser("body", new StandardAnalyzer()).parse("date:[0.0.05 TO 2.25.08]");
                System.out.println("Query: "+q);
                Hits hh = is.search(q);
                System.out.println("Found#: "+hh.length());
                Hit h;
                for (Iterator it = hh.iterator(); it.hasNext(); ) {
                    h = (Hit) it.next();
                    System.out.println("Found: " + h.get("date"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Document getDoc1(int yearsDelta, int monthDelta){
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, yearsDelta);
        cal.add(Calendar.MONTH, monthDelta);
        System.out.println("Indexing: " + ZloSearcher.QUERY_DATEFORMAT.format(cal.getTime()));
        Document d = new Document();
//        d.add(new Field("date1", DateTools.dateToString(cal.getTime(), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.UN_TOKENIZED));
        d.add(new Field("date1", DateFormatUtils.format(cal.getTime(), "yyyyMMdd"), Field.Store.YES, Field.Index.UN_TOKENIZED));
        return d;
    }
}
