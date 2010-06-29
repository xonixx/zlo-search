package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DbManagerImpl;
import info.xonix.zlo.search.logic.site.MessageRetriever;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.model.Site;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Vovan
 * Date: 17.12.2007
 * Time: 23:52:24
 */
public class RescanUndefStatusMsgs {
    private static int MAX_ALEXZAM = 4030808; // 4 030 808 - max alexzam db row
    private static int N = 10000;

    public static void main(String[] args) {
        new Config();

        DbManagerImpl dbm = Site.forName("zlo").getDbManager();

        int n = 420000;

        int addedEmpty = 0;
        while (n <= MAX_ALEXZAM + N) {
            List<Message> msgs_n_N = dbm.getMessagesByRange(n, n + N);

            Set<Integer> newNums = new HashSet<Integer>();
            for (Message m : msgs_n_N) {
                if (m.getStatus() == MessageStatus.UNKNOWN) {
                    newNums.add(m.getNum());
                }
            }

            System.out.println(MessageFormat.format("Need to rescan {0} unknown messages from {1} to {2} ", newNums.size(), n, n + N));
            addedEmpty = newNums.size();

            if (newNums.size() > 0) {
                System.out.print("Getting from site... ");
                List<Message> newMsgs = MessageRetriever.getMessages(Site.forName("zlo"), newNums);

                System.out.print("Saving... ");
                dbm.saveMessagesFast(newMsgs, true);

                System.out.println("Done.");
            }
            newNums.clear();

            n += N;
        }
        System.out.println("Added empty: " + addedEmpty);
    }
}
