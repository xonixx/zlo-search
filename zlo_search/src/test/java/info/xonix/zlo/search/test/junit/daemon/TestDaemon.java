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
    private long workTime;

    public TestDaemon(
            int doPerTime, long sleepPeriod, long retryPeriod,
            String forumId,
            int start, int stop, long cleanUpTime,
            long workTime) {

        super(forumId,
                doPerTime, sleepPeriod, retryPeriod);

        this.start = start;
        this.stop = stop;
        this.cleanUpTime = cleanUpTime;
        this.workTime = workTime;
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

                work(workTime);

                return start;
            }

            @Override
            protected int getEndIndex() throws Exception {
                int _stop = stop;
                log.info(describe() + " - getEndIndex, stop=" + _stop);

                work(workTime);

                stop += 50;
                return _stop;
            }

            @Override
            protected void perform(int from, int to) throws Exception {
                log.info(describe() + " - perform from=" + from + " to=" + to);

                work(workTime);
            }

            @Override
            protected boolean processException(Exception ex) {
                log.error(describe() + " - processException", ex);
                return false;
            }

            @Override
            protected void cleanUp() {
                log.info(describe() + " - cleanUp will take " + cleanUpTime);
                work(cleanUpTime);
                log.info(describe() + " - cleanUp done.");
            }
        };
    }

    private void work(final long interval) {
        log.info(describe() + " - working " + interval + "...");
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}