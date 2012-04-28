package info.xonix.zlo.search.test.junit.daemon;

import info.xonix.zlo.search.daemon.DaemonManager;
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

        TestDaemon testDaemon1 = new TestDaemon(
                100, 5000, 1000,
                "test-1", 0, 1020);

        TestDaemon testDaemon2 = new TestDaemon(
                100, 5020, 1000,
                "test-2", 1070, 2000);

        daemonManager.startDaemon(testDaemon1);
        daemonManager.startDaemon(testDaemon2);

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
