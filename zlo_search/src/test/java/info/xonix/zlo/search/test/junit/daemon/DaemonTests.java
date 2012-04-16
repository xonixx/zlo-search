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
        final int stop = 1000;

        TestDaemon testDaemon = new TestDaemon("test", start, stop);

        testDaemon.start();
    }
}
