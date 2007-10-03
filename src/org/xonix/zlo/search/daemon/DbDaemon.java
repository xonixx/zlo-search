package org.xonix.zlo.search.daemon;

import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.DBException;
import org.xonix.zlo.search.config.Config;

import java.io.IOException;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon {
    public static final int NUM_OF_CONTINIOUS_SCAN = Integer.parseInt(Config.getProp("db.daemon.scan.per.time"));
    public static final int NUM_OF_WAIT_SECONDS = Integer.parseInt(Config.getProp("db.daemon.period.to.scan"));

    private static void processInBackground() throws InterruptedException, IOException, DAO.Exception, DBException {
        int start;
        int end;
        while (true){
            start = DAO.DB._getLastMessageNumber();
            end = DAO.Site._getLastMessageNumber();
            if ( (start + NUM_OF_CONTINIOUS_SCAN) < end ){
                end = start + NUM_OF_CONTINIOUS_SCAN;
            }
            if (start != end ){
                DAO.DB.saveMessages(DAO.Site._getMessages(start, end));
            }
            Thread.sleep(1000 * NUM_OF_WAIT_SECONDS);
        }
    }

    public static void main(String[] args) throws DAO.Exception, IOException, InterruptedException, DBException {
        DAO.DB.saveMessages(DAO.Site._getMessages(3999990, 3999999));
        DAO.DB.saveMessages(DAO.Site._getMessages(4000000, 4000004));
        processInBackground();
    }
}