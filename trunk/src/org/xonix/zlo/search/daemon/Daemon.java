package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.dao.DB;
import org.xonix.zlo.search.db.DbManagerSource;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.ZloIndexer;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 20:00:32
 */
public abstract class Daemon {
    private static final Logger logger = Logger.getLogger(Daemon.class);
    private boolean exiting;
    private boolean isSleeping = false;
    private Process process;

    protected void setExiting(boolean exiting) {
        if (isSleeping) {
            logger.info("Exiting...");
            getProcess(getSiteName()).cleanUp();
            System.exit(0);
        } else {
            this.exiting = exiting;
        }
    }

    protected boolean isExiting() {
        return exiting;
    }

    protected abstract Process createProcess(String siteName);

    private Process getProcess(String siteName) {
        if (process == null) {
            process = createProcess(siteName);
        }
        return process;
    }

    protected abstract class Process extends Thread {
        private DbManagerSource dbms;

        public Process(String siteName) {
            super(siteName);
            Site site = new Site(siteName);
            site.DB_VIA_CONTAINER = false;
            dbms = new DbManagerSource(site);
        }

        protected DbManager getDbManager() {
            return dbms.getDbManager();
        }

        private ZloIndexer zloIndexer;
        protected ZloIndexer getIndexer() {
            if (zloIndexer == null) {
                zloIndexer = new ZloIndexer(getDB());
            }
            return zloIndexer;
        }

        protected DB getDB() {
            return dbms.getDB();
        }

        public void run() {
            while (true) {
                doOneIteration();

                if (isExiting()) {
                    cleanUp();
                    break;
                }
            }
        }

        protected abstract void doOneIteration();
        protected abstract void cleanUp();

        protected void sleepSafe(long millis) {
            isSleeping = true;
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                logger.info("MainProcess interrupted???");
            }
            isSleeping = false;
        }
    }

    protected Daemon() {
        registerExitHandlers();
    }

    public void registerExitHandlers() {
        logger.info("Registering exit handlers...");
        setExiting(false);

        SignalHandler exitHandler = new SignalHandler() {
            public void handle(Signal signal) {
                logger.info("Exit handler for " + signal.getName() + "...");
                setExiting(true);
            }
        };

        Signal.handle(new Signal("INT"), exitHandler);
        Signal.handle(new Signal("TERM"), exitHandler);
    }

    protected void start() {
        while (true) {
            Process t = getProcess(getSiteName());
            t.setPriority(Thread.MIN_PRIORITY); // so daemons not slowing search 
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // if we are here => thread finished
            // if !exiting => thread was finished by mysterious reason
            if (!isExiting()) {
                logger.warn("MainProcess unexpectedly finished, restarting...");
            } else {
                logger.info("Gracefully exiting...");
                break;
            }
        }
    }

    private String getSiteName() {
        String sn = System.getenv("SITE_NAME");
        if (sn == null) {
            logger.error("Must set SITE_NAME environment variable!");
            System.exit(-1);
            return null;
        } else {
            return sn;
        }
    }
}
