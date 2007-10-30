package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import static org.xonix.zlo.search.DAO.DAOException;
import org.xonix.zlo.search.model.ZloMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Author: gubarkov
 * Date: 01.10.2007
 * Time: 21:18:54
 */
public class MultithreadedRetriever {

    private static Logger logger = Logger.getLogger(MultithreadedRetriever.class);

    private static class MessageRetriever extends Thread {
        private static int from = -1;
        private static int to = -1;
        private static int currentNum = -1;

        private static int threadNum = 0;

        private List<ZloMessage> msgs;
        private List<ZloMessage> myMsgs = new ArrayList<ZloMessage>();
        private IndexingSource source;

        private static Exception exception = null;

        public MessageRetriever(IndexingSource source, int from, int to, List<ZloMessage> msgs) {
            super("MessageRetriever #" + threadNum);

            if (threadNum == 0 && exception != null) {
                // clean the exception of last range retrieve
                exception = null;
            }

            this.source = source;

            if (MessageRetriever.from == -1)
                MessageRetriever.from = from;
            if (MessageRetriever.to == -1)
                MessageRetriever.to = to;
            if (currentNum == -1)
                currentNum = from;
            this.msgs = msgs;
            logger.info("Born #" + threadNum);
            threadNum++;
        }

        private synchronized boolean hasMoreToDownload() {
            return currentNum < to && exception == null;
        }

        private synchronized int getNextNum() {
            return currentNum++;
        }

        public void run() {
            try {
                while (hasMoreToDownload()) {
                    try {
                        logger.debug("Downloading: " + currentNum + " by " + getName());
                        myMsgs.add(source.getMessageByNumber(getNextNum()));
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
                    from = to = currentNum = -1;
                }
            }
        }

        public static Exception getException() {
            return exception;
        }
    }

    public static List<ZloMessage> getMessages(IndexingSource source, final int from, final int to, int threadsNum) throws DAOException {
        final List<ZloMessage> msgs;
        if (threadsNum == 1) {
            msgs = new ArrayList<ZloMessage>();

            for (int i = from; i < to; i++) {
                msgs.add(source.getMessageByNumber(i));
            }
        } else {
            msgs = new Vector<ZloMessage>();
            List<Thread> threads = new ArrayList<Thread>();

            // starting all threads
            for (int i = 0; i < threadsNum; i++) {
                Thread t = new MessageRetriever(source, from, to, msgs);
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
