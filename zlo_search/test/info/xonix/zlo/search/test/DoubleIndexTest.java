package info.xonix.zlo.search.test;

import info.xonix.zlo.search.ZloSearcher;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.model.ZloMessage;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 16.12.2007
 * Time: 1:14:23
 */
public class DoubleIndexTest {
    public static void main(String[] args) {
        m2();
        System.exit(0);
    }

    private static void m3() {
        DoubleIndexSearcher dis = getDIS();
        System.out.println(dis.getBigReader().isOptimized());
        try {
            System.out.println(new IndexWriter("D:\\TEST\\JAVA\\ZloSearcher\\__test\\1", ZloMessage.constructAnalyzer()).getUseCompoundFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DoubleIndexSearcher getDIS() {
        return new DoubleIndexSearcher(Site.forName("test"), ZloSearcher.getDateSort());
    }

    public static void m1() {
        DoubleIndexSearcher dis = getDIS();
        try {
            DoubleHits dh = dis.search(new MatchAllDocsQuery());
            System.out.println(dh.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        DoubleIndexSearcher dis = getDIS();
        try {
            //dis.moveSmallToBig();
            dis.optimize();
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
