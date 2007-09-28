package org.xonix.zlo.search.daemon;

import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.DBException;
import org.xonix.zlo.search.config.Config;

import java.io.IOException;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class Crawler {
    private static int numOfContinuousScan = Config.NUM_OF_CONTINIOUSSCAN;
    private static int numOfWaitSeconds = Config.NUM_OF_WAIT_SECONDS;

    private static void processInBackground() throws InterruptedException, IOException, DAO.Exception, DBException {
        int start;
        int end;
        while (true){
            start = DAO.DB._getLastMessageNumber();
            end = DAO.Site._getLastMessageNumber();
            if ( (start + numOfContinuousScan) < end ){
                end = start + numOfContinuousScan;
            }
            if (start != end ){
                DAO.DB.saveMessages(DAO.Site._getMessages(start, end));
            }
            Thread.sleep(1000 * numOfWaitSeconds);
        }
    }

    public static void main(String[] args) throws DAO.Exception, IOException, InterruptedException, DBException {
        DAO.DB.saveMessages(DAO.Site._getMessages(3999990, 3999999));
        DAO.DB.saveMessages(DAO.Site._getMessages(4000000, 4000004));
        Crawler.processInBackground();
    }
}