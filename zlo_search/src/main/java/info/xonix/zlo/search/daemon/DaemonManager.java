package info.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: gubarkov
 * Date: 27.04.12
 * Time: 22:21
 * <p/>
 * contains a list of daemons &amp; restarts them in case of unexpected finish
 */
public class DaemonManager {
    private final static Logger log = Logger.getLogger(DaemonManager.class);

    private List<Daemon> daemons = new LinkedList<Daemon>();

    public static long DEFAULT_CHECK_PERIOD = 1000;// 1 sec

    private long checkPeriod;

    public DaemonManager() {
        this(DEFAULT_CHECK_PERIOD);
    }

    public DaemonManager(long checkPeriod) {
        this.checkPeriod = checkPeriod;

        startCheckingTimer();

        registerShutdownHook();
    }

    private void startCheckingTimer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                final List<Daemon> toRestart = new LinkedList<Daemon>();

                final Iterator<Daemon> iterator = daemons.iterator();

                while (iterator.hasNext()) {
                    final Daemon daemon = iterator.next();
                    if (daemon.finishedAbnormally()) {
                        iterator.remove();
                        daemon.reset();
                        toRestart.add(daemon);
                    }
                }

                for (Daemon daemon : toRestart) {
                    log.warn("Daemon " + daemon.describe() + " finished unexpectedly, restarting",
                            daemon.getLastException());

                    startDaemon(daemon);
                }
            }
        }, 0, checkPeriod, TimeUnit.MILLISECONDS);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown(true);
            }
        }));
    }

    public DaemonManager startDaemon(Daemon daemon) {
        log.info("Starting daemon: " + daemon.describe());

        daemon.doOnStart();

        Daemon.Process t = daemon.getProcess();
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY); // so daemons not slowing search
        t.start();

        daemons.add(daemon);

        return this;
    }

    public void shutdown(boolean waitAllExit) {
        log.info("Shutting down " + daemons.size() + " threads...");

        for (Daemon d : daemons) {
            d.setExiting();

            if (d.getDaemonState() == DaemonState.SLEEPING) {
                d.getProcess().interrupt();
            }
        }

        if (waitAllExit) {
            try {
                Thread.sleep(10000);//test
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
