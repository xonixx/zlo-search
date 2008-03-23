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
import java.net.ConnectException;
import java.util.Vector;

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

    // clean up
    private static Vector<Daemon> daemons = new Vector<Daemon>();
    /**
     * register for cleaning up
     * @param d
     */
    public static void registerForCleanUp(Daemon d) {
        daemons.add(d);
    }

    public static void setExitingAll() {
        for (Daemon d : daemons) {
            d.setExiting(true);

            if (d.isSleeping) {
                d.getProcess().interrupt();
            }
        }
    }
    // end clean up

    protected void setExiting(boolean exiting) {
        this.exiting = exiting;
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
            super();
            setName(getSiteName());
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
                try {
                    doOneIteration();
                } catch (InterruptedException e) {
                    getLogger().info(getSiteName() + " - Process interrupted.");
                }

                if (isExiting()) {
                    getLogger().info(getSiteName() + " - Performing cleanup...");
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

        private void doOneIteration() throws InterruptedException {
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
                    doSleep(SLEEP_PERIOD);
                    end = getEndIndex();
                }
            } catch (DbException e) {
                getLogger().warn(getSiteName() + " - Problem with db: " + e.getClass());
                doSleep(RETRY_PERIOD);
            } catch (DAOException e) {
                if (e.getCause() instanceof ConnectException) {
                    getLogger().error(getSiteName() + " - Problem with site...", e);
                } else {
                    getLogger().error(getSiteName() + " - ", e);
                }
                doSleep(RETRY_PERIOD);
            } catch (IOException e) {
                getLogger().error(getSiteName() + " - IOException while indexing, probably something with index...", e);
                doSleep(RETRY_PERIOD);
            }
        }
        protected abstract void cleanUp();

        protected void doSleep(long millis) throws InterruptedException {
            isSleeping = true;
            sleep(millis);
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
        registerForCleanUp(this);

        getLogger().info(getSiteName() + " - Registering exit handlers...");
        setExiting(false);

        SignalHandler exitHandler = new SignalHandler() {
            public void handle(Signal signal) {
                getLogger().info(getSiteName() + " - Exit handler for " + signal.getName() + "...");
                setExitingAll();
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
