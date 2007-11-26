package org.xonix.zlo.search.test;

import org.xonix.zlo.search.daemon.Daemon;
import org.xonix.zlo.search.config.Config;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 21:23:57
 */
public class TestDaemon extends Daemon {
    private static final Logger logger = Logger.getLogger(TestDaemon.class);
    private class MyProcess extends Process {

        private int i=0;
        protected void doOneIteration() {
            System.out.println("Iteration: " + i);
            sleepSafe(5000);
            i++;
        }
    }

    protected Process createProcess() {
        return new MyProcess();
    }

    public static void main(String[] args) {

        new Config();
        new TestDaemon().start();
/*                        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                //logger.debug("Exiting...");
                System.out.println("Exiting...");
                //setExiting(true);
            }
        });*/
    }
}
