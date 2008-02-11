package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.dao.DB;
import org.xonix.zlo.search.dao.DAOException;
import org.xonix.zlo.search.db.DbManagerSource;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.site.SiteSource;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 20:00:32
 */
public abstract class Daemon extends SiteSource {
//    private static final Logger logger = Logger.getLogger("Daemon");
    private boolean exiting;
    private boolean isSleeping = false;
    private Process process;

    protected int DO_PER_TIME;
    protected int SLEEP_PERIOD;
    protected int RETRY_PERIOD;

    protected abstract Logger getLogger();

    protected void setExiting(boolean exiting) {
        if (isSleeping) {
            getLogger().info(getSiteName() + " - Exiting...");
            getProcess().cleanUp();
            System.exit(0);
        } else {
            this.exiting = exiting;
        }
    }

    protected boolean isExiting() {
        return exiting;
    }

    protected abstract Process createProcess();

    private Process getProcess() {
        if (process == null) {
            process = createProcess();
        }
        return process;
    }

    protected abstract class Process extends Thread {
        private DbManagerSource dbms;

        public Process() {
            super(getSiteName());
            Site site = getSite();
            site.setDB_VIA_CONTAINER(false);
            dbms = new DbManagerSource(site);
        }

        protected DbManager getDbManager() {
            return dbms.getDbManager();
        }

        private ZloIndexer zloIndexer;
        protected ZloIndexer getIndexer() {
            if (zloIndexer == null) {
                zloIndexer = new ZloIndexer(getSite());
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

        protected abstract int getFromIndex() throws DAOException;
        protected abstract int getEndIndex() throws DAOException;
        protected abstract void perform(int from, int to) throws DAOException;

        private int indexFrom = -1;
        private int end = -1;

        private void doOneIteration() {
            try {
                if (indexFrom == -1) {
                    indexFrom = getFromIndex() + 1;
                }

                if (end == -1)
                    end = getEndIndex();

                int indexTo;

                if (indexFrom + DO_PER_TIME - 1 < end) {
                    indexTo = indexFrom + DO_PER_TIME - 1;
                } else {
                    end = getEndIndex();
                    if (indexFrom + DO_PER_TIME - 1 < end)
                        indexTo = indexFrom + DO_PER_TIME - 1;
                    else
                        indexTo = end;
                }

                if (indexFrom <= indexTo) {
                    perform(indexFrom, indexTo);
                }

                indexFrom = indexTo + 1;

                while (indexFrom > end) {
                    getLogger().info(getSiteName() + " - Sleeping " + SLEEP_PERIOD / 1000 / 60f + " min...");
                    sleepSafe(SLEEP_PERIOD);
                    end = getEndIndex();
                }
            } catch (DbException e) {
                getLogger().warn(getSiteName() + " - Problem with db: " + e.getClass());
                sleepSafe(RETRY_PERIOD);
            } catch (IOException e) {
                getLogger().error(getSiteName() + " - IOException while indexing, probably something with index...", e);
                sleepSafe(RETRY_PERIOD);
            }
        }
        protected abstract void cleanUp();

        protected void sleepSafe(long millis) {
            isSleeping = true;
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                getLogger().info(getSiteName() + " - MainProcess interrupted???");
            }
            isSleeping = false;
        }
    }

    protected Daemon() {
        this(Site.forName(Config.getSiteEnvName()));
    }

    protected Daemon(Site site) {
        super(site);
        registerExitHandlers();
    }

    public void registerExitHandlers() {
        getLogger().info(getSiteName() + " - Registering exit handlers...");
        setExiting(false);

        SignalHandler exitHandler = new SignalHandler() {
            public void handle(Signal signal) {
                getLogger().info(getSiteName() + " - Exit handler for " + signal.getName() + "...");
                setExiting(true);
            }
        };

        Signal.handle(new Signal("INT"), exitHandler);
        Signal.handle(new Signal("TERM"), exitHandler);
    }

    protected void start() {
        getLogger().info("Starting " + this.getClass() + " daemon for site: " + getSiteName());
        while (true) {
            Process t = getProcess();
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
                getLogger().warn(getSiteName() + " - MainProcess unexpectedly finished, restarting...");
            } else {
                getLogger().info(getSiteName() + " - Gracefully exiting...");
                break;
            }
        }
    }
}
