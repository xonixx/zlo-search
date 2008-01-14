package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.db.DbManager;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 16:20:13
 */
public class DropIndex {
    public static void main(String[] args) {
        System.out.print("Are you shure you want to drop index ? (y/n): ");
        byte[] reply = new byte[1];
        try {
            System.in.read(reply);

            if (reply[0] == 'y') {
                System.out.println("Deleting...");
                if (Config.USE_DOUBLE_INDEX) {
                    DoubleIndexSearcher dis = ZloSearcher.forSite(new Site("zlo")).getDoubleIndexSearcher();
                    dis.drop();
                    dis.close();
                    DbManager.forSite("zlo").setLastIndexedNumber(-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
