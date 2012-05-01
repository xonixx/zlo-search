package info.xonix.utils.daemon;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * User: gubarkov
 * Date: 16.04.12
 * Time: 15:43
 */
public class DaemonTests {
    private final static Logger log = Logger.getLogger(DaemonTests.class);

    @Test
    public void test1() {
        DaemonManager daemonManager = new DaemonManager();

        daemonManager.startDaemon(new TestIteratingDaemon(
                100, 5000, 1000,
                "test-1", 0, 1020, 3000, 4000));

        daemonManager.startDaemon(new TestIteratingDaemon(
                100, 5020, 1000,
                "test-2", 1070, 2000, 6666, 2000));

        waitForever();
    }

    @Test
    public void test2() {
        DaemonManager daemonManager = new DaemonManager();

        daemonManager.startDaemon(new RandomlyFailingDaemon("d01", .01f));
        daemonManager.startDaemon(new RandomlyFailingDaemon("d1", .1f));
        daemonManager.startDaemon(new RandomlyFailingDaemon("d2", .2f));

        waitForever();
    }

    private static void waitForever() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
