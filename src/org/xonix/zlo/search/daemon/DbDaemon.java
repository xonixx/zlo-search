package org.xonix.zlo.search.daemon;

import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.IndexingSource;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.logging.Logger;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class.getName());

    public static final int SCAN_PER_TIME = Integer.parseInt(Config.getProp("db.daemon.scan.per.time"));
    public static final int SCAN_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("db.daemon.period.to.scan"));
    public static final int RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(Config.getProp("db.daemon.period.to.reconnect"));

    private static boolean exiting;

    private IndexingSource source;

    private class MainProcess extends Thread {
        private int endSource = -1;
        private int startDb = -1;

        public MainProcess() {
            super();
        }

        public void run() {
            while (true) {
                doOneIteration();

                if (exiting)
                    break;
            }
        }

        private void doOneIteration() {
            try {
                if (startDb == -1)
                    startDb = DAO.DB._getLastMessageNumber() + 1;

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
                    logger.warning("Problem with db: " + e);
                } else if (e.getSource() instanceof DAO.Site) {
                    logger.warning("Problem with site: " + e);
                }
                e.printStackTrace();
                sleep0(RECONNECT_PERIOD);
            }
        }

        private void sleep0(long millis) {
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                logger.info("MainProcess interrupted???");
            }
        }
    }

    public DbDaemon(IndexingSource source) {
        this.source = source;
        registerExitHandlers();
    }

    private void start() {
        while (true) {
            MainProcess t = new MainProcess();
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // if we are here => thread finished
            // if !exiting => thread was finished by mysterious reason
            if (!exiting) {
                logger.warning("MainProcess unexpectedly finished, restarting...");
            } else {
                logger.info("Gracefully exiting...");
                break;
            }
        }
    }

    public void registerExitHandlers() {
        exiting = false;

        SignalHandler exitHandler = new SignalHandler() {
            public void handle(Signal signal) {
                exiting = true;
            }
        };

        Signal.handle(new Signal("INT"), exitHandler);
        Signal.handle(new Signal("TERM"), exitHandler);
    }

    public static void main(String[] args) {
//        DAO.DB.saveMessages(DAO.Site._getMessages(3999990, 3999999));
//        DAO.DB.saveMessages(DAO.Site._getMessages(4000000, 4000010));

        new DbDaemon(DAO.Site.SOURCE).start();
//        new DbDaemon(new ZloStorage()).start();
    }
}