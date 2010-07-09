package info.xonix.zlo.search.test;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.daemon.Daemon;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 17.11.2007
 * Time: 21:23:57
 */
public class TestDaemon extends Daemon {
    private static final Logger logger = Logger.getLogger(TestDaemon.class);

    protected Logger getLogger() {
        return null;
    }

    protected Process createProcess() {
        return null;
    }

    private class MyProcess extends Process {

        private int i = 0;

        public MyProcess() {
            super();
        }

        protected int getFromIndex() {
            return 0;
        }

        protected int getEndIndex() {
            return 0;
        }

        protected void perform(int from, int to) {
        }

        protected boolean processException(Exception ex) {
            return false;
        }

        protected void cleanUp() {
        }
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
