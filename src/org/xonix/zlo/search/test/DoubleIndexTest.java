package org.xonix.zlo.search.test;

import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.DoubleHits;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.model.ZloMessage;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.index.IndexWriter;

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
            DoubleHits dh = dis.search(new MatchAllDocsQuery(), new Sort());
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
