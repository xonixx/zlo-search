package info.xonix.zlo.search.test;

import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 14:37:56
 */
public class Main2 {
    public static final String TEST_INDEX = "test_index";
    public static final Analyzer ANALYZER = new RussianAnalyzer();

    public static void main(String[] args) {
        final String[][] DATA = {
                {"10", "hello world", "Hello world!! 123 one two three George Bush"},
                {"27", "google", "Google da best!"},
                {"254", "firefox", "Firefox rules!!! IE must die!"},
                {"177", "привет", "Иванов Иван Иванович"},
                {"178", "превед", "Ктулху Фхтагн мальчика"},
                {"179", "превед медвед", "Йа Креведко!"},
                //{"Развлечения", "Развлечения", "Абракадабра абырвалг Даздраперма"},
                //{"123123123", "Развлечения", "Абракадабра абырвалг Даздраперма"},
        };
        try {
            // indexing
            IndexWriter wr = new IndexWriter(TEST_INDEX, ANALYZER, true);

/*            for (String[] line : DATA) {
                Document d = new Document();
                d.add(new Field("num", line[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
                d.add(new Field("title", line[1], Field.Store.YES, Field.Index.TOKENIZED));
                d.add(new Field("body", line[2], Field.Store.COMPRESS, Field.Index.TOKENIZED));

                wr.addDocument(d);
            }
            */
            Site site = Site.forName("zlo");
            AppLogic appLogic = AppSpringContext.get(AppLogic.class);
            wr.addDocument(appLogic.getMessageByNumber(site, 3001403).getDocument());


            wr.optimize();
            wr.close();

            //searching
            IndexReader reader = IndexReader.open(TEST_INDEX);
            IndexSearcher searcher = new IndexSearcher(reader);

/*            QueryParser parser = new QueryParser("body", ANALYZER);

            String queryStr = "\"Иван\"";
            Query query = parser.parse(queryStr);
            System.out.println(query.toString());
            Hits hits = searcher.search(query);
            for (Iterator it = hits.iterator(); it.hasNext();) {
                Document d = ((Hit)it.next()).getDocument();
                System.out.println("Found: {"+d.get("num")+", "+d.get("title")+", "+d.get("body")+"}");
            }*/
//            System.out.println(ZloSearcher.searchIndexReader(reader, "topicCode:4", null).getHits().length());
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (ParseException e) {
            e.printStackTrace();
        }*/
    }
}
