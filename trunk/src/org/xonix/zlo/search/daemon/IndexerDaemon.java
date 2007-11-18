package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.TimeUtils;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;

import java.util.List;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 19:25:04
 */
public class IndexerDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(IndexerDaemon.class);

    public static final int SCAN_PER_TIME = Integer.parseInt(Config.getProp("indexer.daemon.index.per.time"));
    public static final int SCAN_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("indexer.daemon.period.to.index"));
    public static final int RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("indexer.daemon.period.to.reconnect"));

    private class IndexingProcess extends Process {
        private int indexFrom = -1;
        private int indexTo = -1;
        
        protected void doOneIteration() {
/*
            try {
                if (indexFrom == -1)
                    indexFrom = DbManager. + 1;

                if (endSource == -1)
                    endSource = source.getLastMessageNumber();

                int end;

                if (startDb + SCAN_PER_TIME < endSource) {
                    end = startDb + SCAN_PER_TIME;
                } else {
                    endSource = source.getLastMessageNumber();
                    if (startDb + SCAN_PER_TIME < endSource)
                        end = startDb + SCAN_PER_TIME;
                    else
                        end = endSource;
                }

                if (startDb < end) {
                    List<ZloMessage> msgs = source.getMessages(startDb, end);
                    DAO.DB.saveMessages(msgs);
                }

                startDb = end;

                while (startDb == endSource) {
                    sleep0(SCAN_PERIOD);
                    endSource = source.getLastMessageNumber();
                }
            } catch (DAO.DAOException e) {
                if (e.getSource() instanceof DAO.DB) {
                    logger.warn("Problem with db: " + e);
                } else if (e.getSource() instanceof DAO.Site) {
                    logger.warn("Problem with site: " + e);
                }
                e.printStackTrace();
                sleep0(RECONNECT_PERIOD);
            }
*/
        }
    }

    protected Process createProcess() {
        return new IndexingProcess();
    }

    public static void main(String[] args) {
        new IndexerDaemon().start();
    }
}
