package info.xonix.utils.daemon;

import info.xonix.utils.TimeUtils;
import org.apache.log4j.Logger;

/**
 * User: gubarkov
 * Date: 28.04.12
 * Time: 19:01
 */
public abstract class IteratingDaemon extends Daemon {
    private int doPerTime;
    private long sleepPeriod;
    private long retryPeriod;

    protected IteratingDaemon(String forumId, int doPerTime, long sleepPeriod, long retryPeriod) {
        super(forumId);

        this.doPerTime = doPerTime;
        this.sleepPeriod = sleepPeriod;
        this.retryPeriod = retryPeriod;
    }

    protected abstract int getFromIndex() throws Exception;

    protected abstract int getEndIndex() throws Exception;

    protected abstract void perform(int from, int to) throws Exception;

    protected boolean processException(Exception ex) {
        return false;
    }

    protected void cleanUp() {
        // do nothing by default
    }

    protected void stopIfExiting() throws InterruptedException {
        getProcess().stopIfExiting();
    }

    private class IteratingProcess extends Daemon.Process {
        public void run() {
            while (true) {
                try {
                    doOneIteration();
                } catch (InterruptedException e) {
                    getLogger().info(getForumId() + " - Process interrupted.");
                }

                if (isExiting()) {
                    getLogger().info(getForumId() + " - Performing cleanup...");
                    cleanUp();

                    break;
                }
            }
        }

        private int indexFrom = -1;
        private int end = -1;

        private void doOneIteration() throws InterruptedException {
            final Logger logger = getLogger();
            try {
                if (indexFrom == -1) {
                    indexFrom = getFromIndex() + 1;
                }

                stopIfExiting();

                if (end == -1) {
                    end = getEndIndex();
                }

                stopIfExiting();

                int indexTo = indexFrom + doPerTime - 1;

                if (indexTo > end) {
                    indexTo = end;
                }

                if (indexFrom <= indexTo) {
                    doPerform(indexFrom, indexTo);
                    indexFrom = indexTo + 1;
                }

                stopIfExiting();

                while (indexFrom > end) {
                    logger.info(getForumId() + " - Sleeping " + TimeUtils.toMinutesSeconds(sleepPeriod) + "...");
                    doSleep(sleepPeriod);
                    end = getEndIndex();
                }
            } catch (InterruptedException e) {
                throw e;// exiting
            } catch (Exception e) {
                saveLastException(e);

                if (!processException(e)) {
                    logger.error("(" + getForumId() + ") Unknown exception", e);
                }

                stopIfExiting();

                logger.info(getForumId() + " - Retry in " + TimeUtils.toMinutesSeconds(retryPeriod));
                doSleep(retryPeriod);
            }
        }

        private void doPerform(int from, int to) throws Exception {
            setStateIfNotExiting(DaemonState.PERFORMING);

            perform(from, to);
        }

        @Override
        protected boolean processException(Exception ex) {
            return IteratingDaemon.this.processException(ex);
        }

        @Override
        protected void cleanUp() {
            IteratingDaemon.this.cleanUp();
        }
    }

    @Override
    protected Daemon.Process createProcess() {
        return new IteratingProcess();
    }
}
