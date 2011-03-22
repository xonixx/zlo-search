package info.xonix.zlo.search.test;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleHits;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.logic.SearchLogic;
import info.xonix.zlo.search.logic.SearchLogicImpl;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 16.12.2007
 * Time: 1:14:23
 */
public class DoubleIndexTest {
    private static final Config config = AppSpringContext.get(Config.class);
    private static final SearchLogic SEARCH_LOGIC = AppSpringContext.get(SearchLogicImpl.class);

    public static void main(String[] args) {
        m2();
        System.exit(0);
    }

    private static void m3() {
        DoubleIndexManager dis = getDIS();
        System.out.println(dis.getBigReader().isOptimized());
/*        try {
            System.out.println(new IndexWriter("D:\\TEST\\JAVA\\ZloSearcher\\__test\\1", config.getMessageAnalyzer()).getUseCompoundFile());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static DoubleIndexManager getDIS() {
        return new DoubleIndexManager(Site.forName("test"), SEARCH_LOGIC.getDateSort());
    }

    public static void m1() {
        DoubleIndexManager dis = getDIS();
        try {
            DoubleHits dh = dis.search(new MatchAllDocsQuery(), -1);
            System.out.println(dh.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        DoubleIndexManager dis = getDIS();
        try {
            //dis.moveSmallToBig();
            dis.optimize();
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
