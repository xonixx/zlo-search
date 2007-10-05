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
    public static final int SCAN_PER_TIME = Integer.parseInt(Config.getProp("db.daemon.scan.per.time"));
    public static final int SCAN_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("db.daemon.period.to.scan"));

    private static void processInBackground() throws InterruptedException, IOException, DAO.Exception, DBException {
        int start;
        int end;
        while (true){
            start = DAO.DB._getLastMessageNumber();
            end = DAO.Site._getLastMessageNumber();
            if ( (start + SCAN_PER_TIME) < end ){
                end = start + SCAN_PER_TIME;
            }
            if (start != end ){
                DAO.DB.saveMessages(DAO.Site._getMessages(start, end));
            }
            Thread.sleep(SCAN_PERIOD);
        }
    }

    public static void main(String[] args) throws DAO.Exception, IOException, InterruptedException, DBException {
        DAO.DB.saveMessages(DAO.Site._getMessages(3999990, 3999999));
        DAO.DB.saveMessages(DAO.Site._getMessages(4000000, 4000010));
        //processInBackground();
    }
}