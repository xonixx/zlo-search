package info.xonix.zlo.search.progs;


import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.SearchLogic;
import info.xonix.zlo.search.logic.SearchLogicImpl;
import info.xonix.zlo.search.spring.AppSpringContext;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 16:20:13
 */
public class DropIndex extends App {
    public static void main(String[] args) {
        AppLogic appLogic = AppSpringContext.get(AppLogic.class);
        SearchLogic searchLogic = AppSpringContext.get(SearchLogicImpl.class);

        System.out.print("Are you sure you want to drop index ? (y/n): ");

        byte[] reply = new byte[1];
        try {
            System.in.read(reply);

            if (reply[0] == 'y') {
                System.in.read(reply);
                String siteName = getSiteName();
                System.out.println("Deleting...");
                String forumId = siteName;

                searchLogic.dropIndex(forumId);

                appLogic.setLastIndexedNumber(forumId, -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
