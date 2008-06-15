package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;

import java.io.IOException;
import java.util.Scanner;

/**
 * Author: Vovan
 * Date: 16.06.2008
 * Time: 2:16:36
 */
public class SetCorrectLastIndexed {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter site to set correct lastIndexed or e to exit");
        Scanner scanner = new Scanner(System.in);
        String siteName = scanner.nextLine();

        if ("e".equals(siteName))
            return;
        
        Site s = Site.forName(siteName);

        DoubleIndexSearcher dis = new DoubleIndexSearcher(s, null);
        int lastIndexedNum = Integer.parseInt(dis.search(new MatchAllDocsQuery()).doc(0).get("num"));

        System.out.println(lastIndexedNum);

        s.setDB_VIA_CONTAINER(false);
        s.getDbManager().setLastIndexedNumber(lastIndexedNum);

    }
}
