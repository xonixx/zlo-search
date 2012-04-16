package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.ZloObservable;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.exceptions.ExceptionsLogger;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 20:00:32
 * todo: create class DaemonGroup
 */
public abstract class Daemon {
    private static final Logger log = Logger.getLogger(Daemon.class);

    protected ExceptionsLogger exceptionsLogger = AppSpringContext.get(ExceptionsLogger.class);

    private boolean exiting;
    private boolean isSleeping = false;
    private Process process;

    private int doPerTime;
    private long sleepPeriod;
    private long retryPeriod;

    private Exception lastException;
    private Date lastExceptionTime;

    private DaemonState daemonState;

    private static Observable observable = new ZloObservable();

    private String forumId;

    public static void on(Observer o) {
        observable.addObserver(o);
    }

    public static void un(Observer o) {
        observable.deleteObserver(o);
    }

    public void setDoPerTime(int doPerTime) {
        this.doPerTime = doPerTime;
    }

    public void setSleepPeriod(long sleepPeriod) {
        this.sleepPeriod = sleepPeriod;
    }

    public void setRetryPeriod(long retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    protected abstract Logger getLogger();

    // clean up
    private static Vector<Daemon> daemons = new Vector<Daemon>();

    public static Vector<Daemon> getDaemons() {
        return daemons;
    }

    /**
     * register for cleaning up
     *
     * @param d Daemon
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

    private static void processExited(Daemon daemon) {
        daemons.remove(daemon);
        if (daemons.isEmpty()) {
            log.info("All daemons have exited.");
            observable.notifyObservers("exited");
        }
    }
    // end clean up

    protected void setExiting(boolean exiting) {
        this.exiting = exiting;
        if (exiting)
            daemonState = DaemonState.EXITING;
    }

    protected boolean isExiting() {
        return exiting;
    }

    private void saveLastException(Exception e) {
        lastException = e;
        lastExceptionTime = new Date();
    }

    public Exception getLastException() {
        return lastException;
    }

    public Date getLastExceptionTime() {
        return lastExceptionTime;
    }

    public DaemonState getDaemonState() {
        return daemonState;
    }

    protected abstract Process createProcess();

    private Process getProcess() {
        if (process == null) {
            process = createProcess();
        }
        return process;
    }

    public String describe() {
        return getClass().getSimpleName() + "-" + forumId;
    }

    protected abstract class Process extends Thread {

        public Process() {
            super(describe());
        }

        public void run() {
            while (true) {
                try {
                    doOneIteration();
                } catch (InterruptedException e) {
                    getLogger().info(forumId + " - Process interrupted.");
                }

                if (isExiting()) {
                    getLogger().info(forumId + " - Performing cleanup...");
                    cleanUp();
                    processExited(Daemon.this);
                    break;
                }
            }
        }

        protected abstract int getFromIndex() throws Exception;

        protected abstract int getEndIndex() throws Exception;

        protected abstract void perform(int from, int to) throws Exception;

        protected abstract boolean processException(Exception ex);

        private int indexFrom = -1;
        private int end = -1;

        private void doOneIteration() throws InterruptedException {
            final Logger logger = getLogger();
            try {
                if (indexFrom == -1) {
                    indexFrom = getFromIndex() + 1;
                }

                if (end == -1)
                    end = getEndIndex();

                int indexTo = indexFrom + doPerTime - 1;

                if (indexTo > end) {
                    logger.warn("indexTo=" + indexTo + " > " + "end=" + end + ". Calling getEndIndex()...");

                    end = getEndIndex();

                    if (indexTo > end) {
                        indexTo = end;
                    }
                }

                if (indexFrom <= indexTo) {
                    doPerform(indexFrom, indexTo);
                    indexFrom = indexTo + 1;
                }

                while (indexFrom > end) {
                    logger.info(forumId + " - Sleeping " + TimeUtils.toMinutesSeconds(sleepPeriod) + "...");
                    doSleep(sleepPeriod);
                    end = getEndIndex();
                }
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                saveLastException(e);

                if (!processException(e)) {
                    logger.error("(" + forumId + ") Unknown exception", e);
                }

                logger.info(forumId + " - Retry in " + TimeUtils.toMinutesSeconds(retryPeriod));
                doSleep(retryPeriod);
            }
        }

        protected abstract void cleanUp();

        protected void doSleep(long millis) throws InterruptedException {
            daemonState = DaemonState.SLEEPING;
            isSleeping = true;
            sleep(millis);
            isSleeping = false;
        }

        private void doPerform(int from, int to) throws Exception {
            daemonState = DaemonState.PERFORMING;
            perform(from, to);
        }

        protected void reset() {
            indexFrom = -1;
            end = -1;
        }
    }

    protected Daemon() {
        this(Config.getSiteEnvName());
    }

    protected Daemon(String forumId) {
//        super(forumId);
        this.forumId = forumId;
        registerExitHandlers();
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public void registerExitHandlers() {
        registerForCleanUp(this);

        // TODO: VVV-- this is incorrect: we should not call overridable method from constructor
        getLogger().info(getForumId() + " - Registering exit handlers...");
        setExiting(false);

        final SignalHandler exitHandler = new SignalHandler() {
            public void handle(Signal signal) {
                // getLogger().info(...) gaves NPE at rt
                log.info(getForumId() + " - Exit handler for " + signal.getName() + "...");
                setExitingAll();
            }
        };

        Signal.handle(new Signal("INT"), exitHandler);
        Signal.handle(new Signal("TERM"), exitHandler);
    }

    protected void start() {
        getLogger().info("Starting " + this.getClass() + " daemon for site: " + getForumId());
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
                getLogger().warn(getForumId() + " - MainProcess unexpectedly finished, restarting...");
            } else {
                getLogger().info(getForumId() + " - Gracefully exiting...");
                break;
            }
        }
    }
}
