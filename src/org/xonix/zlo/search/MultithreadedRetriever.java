package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;

import java.util.*;
import java.util.logging.Logger;

/**
 * Author: gubarkov
 * Date: 01.10.2007
 * Time: 21:18:54
 */
public class MultithreadedRetriever {

    private static Logger logger = Logger.getLogger(MultithreadedRetriever.class.getName());

    private static class MessageRetriever extends Thread {
        private static int from = -1;
        private static int to = -1;
        private static int currentNum = -1;

        private static int threadNum = 0;

        private List<ZloMessage> msgs;
        private IndexingSource source;

        public MessageRetriever(IndexingSource source, int from, int to, List<ZloMessage> msgs) {
            super("MessageRetriever #" + threadNum);

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

        private static boolean hasMoreToDownload() {
            return currentNum <= to;
        }

        private int getNextNum() {
            return currentNum++;
        }

        public void run() {
            try {
                while (hasMoreToDownload()) {
                    try {
                        logger.info("Downloading :" + currentNum + " by " + getName());
                        msgs.add(source.getMessageByNumber(getNextNum()));
                    } catch (Exception e) {
                        e.printStackTrace(); // todo: need to decide what to do here
                    }
                }
            } finally {
                logger.info(getName()+" finished.");
                threadNum--;

                if (threadNum == 0) {
                    // no more to download - making MessageRetriever ready for next requests
                    from = to = currentNum = -1;
                }
            }
        }
    }

    public static List<ZloMessage> getMessages(IndexingSource source, final int from, final int to, int threadsNum) throws DAO.Exception {
        final List<ZloMessage> msgs;
        if (threadsNum == 1) {
            msgs = new ArrayList<ZloMessage>();

            for (int i = from; i <= to; i++) {
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
        }

        Collections.sort(msgs, new Comparator<ZloMessage>() {
            public int compare(ZloMessage m1, ZloMessage m2) {
                return m1.getNum() > m2.getNum() ? 1 : m1.getNum() < m2.getNum() ? -1 : 0;
            }
        });
        
        return msgs;
    }
}
