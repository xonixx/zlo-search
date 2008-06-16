package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexSearcher;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 16:20:13
 */
public class DropIndex extends App {
    public static void main(String[] args) {
        System.out.print("Are you sure you want to drop index ? (y/n): ");
        byte[] reply = new byte[1];
        try {
            System.in.read(reply);

            if (reply[0] == 'y') {
                System.in.read(reply);
                String siteName = getSiteName();
                System.out.println("Deleting...");
                if (Config.USE_DOUBLE_INDEX) {
                    Site site = Site.forName(siteName);
                    site.setDB_VIA_CONTAINER(false);
                    DoubleIndexSearcher dis = site.getZloSearcher().getDoubleIndexSearcher();
                    dis.drop();
                    dis.close();
                    site.getDbManager().setLastIndexedNumber(-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
