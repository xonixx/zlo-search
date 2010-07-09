package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 16.06.2008
 * Time: 2:16:36
 */
public class SetCorrectLastIndexed extends App {
    public static void main(String[] args) throws IOException {
        AppLogic appLogic = AppSpringContext.get(AppLogic.class);

        String siteName = getSiteName();

        if ("e".equals(siteName))
            return;

        Site site = Site.forName(siteName);

        DoubleIndexSearcher dis = new DoubleIndexSearcher(site, null);
        int lastIndexedNum = Integer.parseInt(dis.search(new MatchAllDocsQuery()).doc(0).get("num"));

        System.out.println(lastIndexedNum);

        appLogic.setLastIndexedNumber(site, lastIndexedNum);

    }

}
