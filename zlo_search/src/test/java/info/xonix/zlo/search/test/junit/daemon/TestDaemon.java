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
    private long cleanUpTime;

    public TestDaemon(
            int doPerTime, long sleepPeriod, long retryPeriod,
            String forumId, int start, int stop, long cleanUpTime) {
        super(forumId,
                doPerTime, sleepPeriod, retryPeriod);

        this.start = start;
        this.stop = stop;
        this.cleanUpTime = cleanUpTime;
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
                log.info(describe() + " - getFromIndex, start=" + start);
                return start;
            }

            @Override
            protected int getEndIndex() throws Exception {
                int _stop = stop;
                log.info(describe() + " - getEndIndex, stop=" + _stop);
                stop += 50;
                return _stop;
            }

            @Override
            protected void perform(int from, int to) throws Exception {
                log.info(describe() + " - perform from=" + from + " to=" + to);
            }

            @Override
            protected boolean processException(Exception ex) {
                log.error(describe() + " - processException", ex);
                return false;
            }

            @Override
            protected void cleanUp() {
                log.info(describe() + " - cleanUp will take " + cleanUpTime);
                try {
                    Thread.sleep(cleanUpTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(describe() + " - cleanUp done.");
            }
        };
    }
}