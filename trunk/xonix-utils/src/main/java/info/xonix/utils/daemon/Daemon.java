package info.xonix.utils.daemon;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 20:00:32
 */
public abstract class Daemon {
    private static final Logger log = Logger.getLogger(Daemon.class);

    private Process process;

    private Exception lastException;
    private Date lastExceptionTime;

    private DaemonState daemonState;

    private String forumId;

    protected Daemon(String forumId) {
        this.forumId = forumId;
    }

    // TODO: this is NOT good!
    protected abstract Logger getLogger();

    protected void setExiting() {
        daemonState = DaemonState.EXITING;
    }

    protected boolean isExiting() {
        return daemonState == DaemonState.EXITING;
    }

    void saveLastException(Exception e) {
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

    synchronized Process getProcess() {
        if (process == null) {
            process = createProcess();
        }
        return process;
    }

    public String describe() {
        return getClass().getSimpleName() + "-" + forumId;
    }

    public void reset() {
        try {
            process.cleanUp();
        } catch (Exception e) {
            log.error("Error cleanUp", e);
        }

        process = null;
    }

    protected abstract class Process extends Thread {
        public Process() {
            super(describe());
        }

        protected abstract boolean processException(Exception ex);

        protected void stopIfExiting() throws InterruptedException {
            if (isExiting()) {
                throw new InterruptedException("exit");
            }
        }

        protected abstract void cleanUp();

        void doSleep(long millis) throws InterruptedException {
            DaemonState prevState = daemonState;

            setStateIfNotExiting(DaemonState.SLEEPING);

            sleep(millis);

            setStateIfNotExiting(prevState);
        }

        void setStateIfNotExiting(DaemonState newDaemonState) {
            if (!isExiting()) {
                daemonState = newDaemonState;
            }
        }
    }

    public String getForumId() {
        return forumId;
    }

    public boolean finishedAbnormally() {
        return getProcess().getState() == Thread.State.TERMINATED
                && !isExiting();
    }

    protected void doOnStart() {
    }

    void start() {
        doOnStart();

        Process t = getProcess();
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY); // so daemons not slowing search
        t.start();
    }
}
