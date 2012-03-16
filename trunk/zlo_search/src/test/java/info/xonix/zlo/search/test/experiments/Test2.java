package info.xonix.zlo.search.test.experiments;

/*import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;*/

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.RAMDirectory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;*/

/**
 * Author: gubarkov
 * Date: 06.09.2007
 * Time: 20:18:16
 */
public class Test2 {
    public final static SimpleDateFormat QUERY_DATEFORMAT = new SimpleDateFormat("yyyyMMdd"); // because of locale


    public static void main(String[] args) {
        RAMDirectory rd = new RAMDirectory();
/*        try {

            IndexWriter iw = new IndexWriter(rd, new KeywordAnalyzer());
            iw.addDocument(getDoc1(0, 0));
            iw.addDocument(getDoc1(1, 5));
            iw.addDocument(getDoc1(-3, 7));
            iw.addDocument(getDoc1(4, -2));
            iw.optimize();
            iw.close();
            IndexReader ir = IndexReader.open(rd);
            IndexSearcher is = new IndexSearcher(ir);

            try {
                Query q = new QueryParser("body", new StandardAnalyzer()).parse("date1:[20050000 TO 20080225]");
//                Query q = new QueryParser("", new StandardAnalyzer()).parse("date1:20110709");
                System.out.println("Query: "+q);
                Hits hh = is.search(q);
                System.out.println("Found#: "+hh.length());
                Hit h;
                for (Iterator it = hh.iterator(); it.hasNext(); ) {
                    h = (Hit) it.next();
                    System.out.println("Found: " + h.get("date1"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static Document getDoc1(int yearsDelta, int monthDelta) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, yearsDelta);
        cal.add(Calendar.MONTH, monthDelta);
        System.out.println("Indexing: " + QUERY_DATEFORMAT.format(cal.getTime()));
        Document d = new Document();
        String dateS = DateTools.dateToString(cal.getTime(), DateTools.Resolution.MINUTE);
        System.out.println("> " + dateS);
        d.add(new Field("date1", dateS, Field.Store.YES, Field.Index.NOT_ANALYZED));
//        d.add(new Field("date1", dateS, Field.Store.YES, Field.Index.UN_TOKENIZED));
//        d.add(new Field("date1", DateField.dateToString(cal.getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
        return d;
    }
}
