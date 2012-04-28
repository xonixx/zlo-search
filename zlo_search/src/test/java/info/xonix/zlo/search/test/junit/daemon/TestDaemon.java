package info.xonix.zlo.search.test.junit.daemon;

import info.xonix.zlo.search.daemon.Daemon;
import org.apache.log4j.Logger;

/**
 * User: gubarkov
 * Date: 16.04.12
 * Time: 15:53
 */
public class TestDaemon extends Daemon {
    private final Logger log = getLogger();
    private int start;
    private int stop;

    public TestDaemon(
            int doPerTime, long sleepPeriod, long retryPeriod,
            String forumId, int start, int stop) {
        super(forumId,
                doPerTime, sleepPeriod, retryPeriod);

        this.start = start;
        this.stop = stop;
    }

    @Override
    protected Logger getLogger() {
        return Logger.getLogger("testDaemon");
    }

    @Override
    protected Daemon.Process createProcess() {
        return new Daemon.Process() {
            @Override
            protected int getFromIndex() throws Exception {
                log.info("getFromIndex, start=" + start);
                return start;
            }

            @Override
            protected int getEndIndex() throws Exception {
                int _stop = stop;
                log.info("getEndIndex, stop=" + _stop);
                stop += 50;
                return _stop;
            }

            @Override
            protected void perform(int from, int to) throws Exception {
                log.info("perform from=" + from + " to=" + to);
            }

            @Override
            protected boolean processException(Exception ex) {
                log.error("processException", ex);
                return false;
            }

            @Override
            protected void cleanUp() {
                log.info("cleanUp");
            }
        };
    }
}