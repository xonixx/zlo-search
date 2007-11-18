package org.xonix.zlo.search.daemon;

import org.apache.log4j.Logger;
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

    protected void setExiting(boolean exiting) {
        this.exiting = exiting;
    }

    protected boolean isExiting() {
        return exiting;
    }

    protected abstract Process createProcess();

    protected abstract class Process extends Thread {
        public Process() {
            super();
        }

        public void run() {
            while (true) {
                doOneIteration();

                if (isExiting())
                    break;
            }
        }

        protected abstract void doOneIteration();

        protected void sleep0(long millis) {
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                logger.info("MainProcess interrupted???");
            }
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

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                logger.debug("Exiting...");
                setExiting(true);
            }
        });
    }

    protected void start() {
        while (true) {
            Process t = createProcess();
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
}
