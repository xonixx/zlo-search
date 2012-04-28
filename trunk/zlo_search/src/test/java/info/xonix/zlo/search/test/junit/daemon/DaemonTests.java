package info.xonix.zlo.search.test.junit.daemon;

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
        final int start = 0;
        final int stop = 1020;

        TestDaemon testDaemon = new TestDaemon(
                100, 5000, 1000,
                "test", start, stop);

        testDaemon.start();
    }
}
