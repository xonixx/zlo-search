package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.MultithreadedRetriever;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;

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

    public static void main(String[] args) throws DbException, DAO.DAOException {
        new Config();
        int n=110000;

        int addedEmpty = 0;
        while (n <= MAX_ALEXZAM + N) {
            List<ZloMessage> msgs_n_N = DbManager.getMessagesByRange(n, n+N);

            Set<Integer> newNums = new HashSet<Integer>();
            for (ZloMessage m : msgs_n_N) {
                if (m.getStatus() == ZloMessage.Status.UNKNOWN) {
                    newNums.add(m.getNum());
                }
            }

            System.out.println(MessageFormat.format("Need to rescan {0} unknown messages from {1} to {2} ", newNums.size(), n, n+N ));
            addedEmpty = newNums.size();

            if (newNums.size() > 0) {
                System.out.print("Getting from site... ");
                List<ZloMessage> newMsgs = MultithreadedRetriever.getMessages(DAO.Site.SOURCE, newNums);

                System.out.print("Saving... ");
                DbManager.saveMessagesFast(newMsgs, true);

                System.out.println("Done.");
            }
            newNums.clear();

            n += N;
        }
        System.out.println("Added empty: " + addedEmpty);
    }
}
