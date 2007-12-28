package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.IndexingSource;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.utils.TimeUtils;

import java.util.List;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class);

    public static final int SCAN_PER_TIME = Integer.parseInt(Config.getProp("db.daemon.scan.per.time"));
    public static final int SCAN_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("db.daemon.period.to.scan"));
    public static final int RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("db.daemon.period.to.reconnect"));

    private IndexingSource source;

    private class MainProcess extends Process {
        private int endSource = -1;
        private int startDb = -1;

        protected void doOneIteration() {
            try {
                if (startDb == -1)
                    startDb = DAO.DB._getLastMessageNumber() + 1;

                if (endSource == -1)
                    endSource = source.getLastMessageNumber();

                int end;

                if (startDb + SCAN_PER_TIME - 1 < endSource) {
                    end = startDb + SCAN_PER_TIME - 1;
                } else {
                    endSource = source.getLastMessageNumber();
                    if (startDb + SCAN_PER_TIME - 1 < endSource)
                        end = startDb + SCAN_PER_TIME - 1;
                    else
                        end = endSource;
                }

                if (startDb <= end) {
                    List<ZloMessage> msgs = source.getMessages(startDb, end + 1);
                    DAO.DB.saveMessages(msgs);
                }

                startDb = end + 1;

                while (startDb > endSource) {
                    logger.debug("Sleeping " + SCAN_PERIOD / 1000 / 60f + " min...");
                    sleepSafe(SCAN_PERIOD);
                    endSource = source.getLastMessageNumber();
                }
            } catch (DAO.DAOException e) {
                if (e.getSource() instanceof DAO.DB) {
                    logger.warn("Problem with db: " + e);
                } else if (e.getSource() instanceof DAO.Site) {
                    logger.warn("Problem with site: " + e);
                }
                e.printStackTrace();
                sleepSafe(RECONNECT_PERIOD);
            }
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon(IndexingSource source) {
        super();
        this.source = source;
    }

    protected Process createProcess() {
        return new MainProcess();
    }

    public static void main(String[] args) {
//        DAO.DB.saveMessages(DAO.Site._getMessages(3999990, 3999999));
//        DAO.DB.saveMessages(DAO.Site._getMessages(4000000, 4000010));

        new DbDaemon(DAO.Site.getSite("zlo")).start();
//        new DbDaemon(new ZloStorage()).start();
    }
}