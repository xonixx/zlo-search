package info.xonix.zlo.search;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.site.PageRetriever;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Author: gubarkov
 * Date: 01.10.2007
 * Time: 21:18:54
 */
public class MultithreadedRetriever {

    private static Logger logger = Logger.getLogger(MultithreadedRetriever.class);

//    private static final int LIMIT_PER_SECOND = Integer.parseInt(Config.getProp("retriever.limit.per.second"));

    // todo: not tested
    private static class MessageRetriever extends Thread {
        private static int threadNum = 0;

        private List<ZloMessage> msgs;
        private List<ZloMessage> myMsgs = new ArrayList<ZloMessage>();
        private IndexingSource source;

        private static Exception exception = null;

        private static Iterator<Integer> numsIterator = null;

        public MessageRetriever(IndexingSource source, Iterable<Integer> numsToSave, List<ZloMessage> msgs) {
            super("MessageRetriever #" + threadNum);

            if (threadNum == 0 && exception != null) {
                // clean the exception of last range retrieve
                exception = null;
            }

            this.source = source;

            if (numsIterator == null)
                numsIterator = numsToSave.iterator();

            this.msgs = msgs;
            logger.info("Born #" + threadNum);
            threadNum++;
        }

        private synchronized int getNextNum() {
            if (numsIterator.hasNext() && exception == null)
                return numsIterator.next();
            else
                return -1;
        }

        public void run() {
            try {
                int nextNum;
                while ((nextNum = getNextNum()) != -1) {
                    try {
                        logger.debug("Downloading: " + nextNum + " by " + getName());
                        myMsgs.add(source.getMessageByNumber(nextNum));
                    } catch (Exception e) {
                        exception = e;
                        logger.warn(getName() + " exiting because of exception: " + e);
                    }
                }
            } finally {
                msgs.addAll(myMsgs);
                logger.info(getName() + " finished, downloaded: " + myMsgs.size());
                threadNum--;

                if (threadNum == 0) {
                    // no more to download - making MessageRetriever ready for next requests
                    numsIterator = null;
                }
            }
        }

        public static Exception getException() {
            return exception;
        }
    }

    public static List<ZloMessage> getMessages(Site source, int from, int to) throws DAOException {
        Set<Integer> nums = new HashSet<Integer>(to - from);
        for(int i=from; i<to; i++) {
            nums.add(i);
        }
        return getMessages(source, nums);
    }

    public static List<ZloMessage> getMessages(Site source, Iterable<Integer> numsToSave) throws DAOException {
        return getMessages(source, numsToSave, PageRetriever.THREADS_NUMBER);
    }

    public static List<ZloMessage> getMessages(Site source, Iterable<Integer> numsToSave, int threadsNum) throws DAOException {
        final List<ZloMessage> msgs;

        if (threadsNum == 1) {
            msgs = new ArrayList<ZloMessage>();

            for (int i : numsToSave) {
                long t1 = System.currentTimeMillis();

                msgs.add(source.getMessageByNumber(i));

                long delta = System.currentTimeMillis() - t1;
                long toSleep = 1000/source.getIndexerLimitPerSecond() - delta;
                if (toSleep > 0) {
                    try {
                        Thread.sleep(toSleep);
                    } catch (InterruptedException e) {
                        ;
                    }
                }
            }
        } else {
            msgs = new Vector<ZloMessage>();
            List<Thread> threads = new ArrayList<Thread>();

            // starting all threads
            for (int i = 0; i < threadsNum; i++) {
                Thread t = new MessageRetriever(source, numsToSave, msgs);
                t.start();
                threads.add(t);
            }

            // joining them - main thread waits for all
            try {
                for(Thread t: threads)
                    t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (MessageRetriever.getException() != null)
                throw new DAOException(source, MessageRetriever.getException());
        }

        Collections.sort(msgs, ZloMessage.NUM_COMPARATOR);
        
        return msgs;
    }
}
