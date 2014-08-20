package info.xonix.utils.daemon;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 20:00:32
 */
public abstract class DaemonBase implements Daemon {
    private static final Logger log = Logger.getLogger(DaemonBase.class);

    private Process process;

    private Exception lastException;
    private Date lastExceptionTime;

    private DaemonState daemonState;

    private String id;

    protected DaemonBase(String id) {
        this.id = id;
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

    synchronized Process getProcess() {
        if (process == null) {
            process = new Process();
        }
        return process;
    }

    public String describe() {
        return getClass().getSimpleName() + "-" + id;
    }

    public void reset() {
        try {
            cleanUp();
        } catch (Exception e) {
            log.error("Error cleanUp", e);
        }

        process = null;
    }

    class Process extends Thread {
        public Process() {
            super(describe());
        }

        @Override
        public void run() {
            perform();
        }

        protected void stopIfExiting() throws InterruptedException {
            if (isExiting()) {
                throw new InterruptedException("exit");
            }
        }
    }

    void doSleep(long millis) throws InterruptedException {
        DaemonState prevState = daemonState;

        setStateIfNotExiting(DaemonState.SLEEPING);

        Thread.sleep(millis);

        setStateIfNotExiting(prevState);
    }

    void setStateIfNotExiting(DaemonState newDaemonState) {
        if (!isExiting()) {
            daemonState = newDaemonState;
        }
    }

    public String getId() {
        return id;
    }

    public boolean finishedAbnormally() {
        return getProcess().getState() == Thread.State.TERMINATED
                && !isExiting();
    }

    protected void doOnStart() {
    }

    protected void stopIfExiting() throws InterruptedException {
        getProcess().stopIfExiting();
    }

    @Override
    public boolean processException(Exception ex) {
        return false;
    }

    @Override
    public void cleanUp() {
        // do nothing by default
    }

    void start() {
        doOnStart();

        Process t = getProcess();
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY); // so daemons not slowing search
        t.start();
    }
}
