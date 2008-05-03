package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.ZloIndexer;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.site.SiteSource;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
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

    private int doPerTime;
    private int sleepPeriod;
    private int retryPeriod;

    public void setDoPerTime(int doPerTime) {
        this.doPerTime = doPerTime;
    }

    public void setSleepPeriod(int sleepPeriod) {
        this.sleepPeriod = sleepPeriod;
    }

    public void setRetryPeriod(int retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    protected abstract Logger getLogger();

    // clean up
    private static Vector<Daemon> daemons = new Vector<Daemon>();

    /**
     * register for cleaning up
     *
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

        public Process() {
            super();
        }


        private ZloIndexer zloIndexer;

        protected ZloIndexer getIndexer() {
            if (zloIndexer == null) {
                zloIndexer = new ZloIndexer(getSite());
            }
            return zloIndexer;
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
            boolean wasException = false;
            try {
                if (indexFrom == -1) {
                    indexFrom = getFromIndex() + 1;
                }

                if (end == -1)
                    end = getEndIndex();

                int indexTo = indexFrom + doPerTime - 1;

                if (indexTo >= end) {
                    end = getEndIndex();
                    if (indexTo >= end) {
                        indexTo = end;
                    }
                }

                if (indexFrom <= indexTo) {
                    perform(indexFrom, indexTo);
                }

                indexFrom = indexTo + 1;

                while (indexFrom > end) {
                    getLogger().info(getSiteName() + " - Sleeping " + TimeUtils.toMinutesSeconds(sleepPeriod) + "...");
                    doSleep(sleepPeriod);
                    end = getEndIndex();
                }
            } catch (DbException e) {
                getLogger().warn(getSiteName() + " - Problem with db: " + e.getClass());
                wasException = true;
            } catch (DAOException e) {
                if (e.getCause() instanceof ConnectException) {
                    getLogger().error(getSiteName() + " - Problem with site... " + e.getCause().getClass().getName());
                } else {
                    getLogger().error(getSiteName() + " - ", e);
                }
                wasException = true;
            } catch (IOException e) {
                getLogger().error(getSiteName() + " - IOException while indexing, probably something with index...", e);
                wasException = true;
            }

            if (wasException) {
                getLogger().info(getSiteName() + " - Reconnect in " + TimeUtils.toMinutesSeconds(retryPeriod));
                doSleep(retryPeriod);
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
        site.setDB_VIA_CONTAINER(false);
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
