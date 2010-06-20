package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 16.06.2008
 * Time: 2:16:36
 */
public class SetCorrectLastIndexed extends App {
    public static void main(String[] args) throws IOException {
        String siteName = getSiteName();

        if ("e".equals(siteName))
            return;

        Site s = Site.forName(siteName);

        DoubleIndexSearcher dis = new DoubleIndexSearcher(s, null);
        int lastIndexedNum = Integer.parseInt(dis.search(new MatchAllDocsQuery()).doc(0).get("num"));

        System.out.println(lastIndexedNum);

        s.getDbManager().setLastIndexedNumber(lastIndexedNum);

    }

}
