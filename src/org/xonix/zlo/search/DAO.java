package org.xonix.zlo.search;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Author: gubarkov
 * Date: 13.09.2007
 * Time: 16:40:04
 */

// data access object
public class DAO {
    private static Logger logger = Logger.getLogger(DAO.class.getName());
    public static class Exception extends java.lang.Exception {
        public Exception(Throwable cause) {
            super(cause);
        }

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class Site implements IndexingSource {
        public static Site SOURCE = new Site();
        private Site() {} // not to create

        public ZloMessage getMessageByNumber(int num) throws Exception {
            logger.info("Receiving from site: " + num);
            try {
                return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
            } catch (IOException e) {
                throw new Exception(e);
            }
        }

        private static class MessageRetriever extends Thread {
            private static int from = -1;
            private static int to = -1;
            private static int currentNum = -1;

            private static int threadNum = 0;

            private List<ZloMessage> msgs;

            public MessageRetriever(int from, int to, List<ZloMessage> msgs) {
                super("MessageRetriever #" + threadNum);
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
//                System.out.println("Run " + currentNum + ", " + from + ", " + to);
                try {
                    while (hasMoreToDownload()) {
                        try {
                            logger.info("Downloading " + currentNum);
                            msgs.add(SOURCE.getMessageByNumber(getNextNum()));
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

        public List<ZloMessage> getMessages(final int from, final int to, int threadsNum) throws Exception {
            final List<ZloMessage> msgs;
            if (threadsNum == 1) {
                msgs = new ArrayList<ZloMessage>();

                for (int i = from; i <= to; i++) {
                    msgs.add(getMessageByNumber(i));
                }
            } else {
                msgs = new Vector<ZloMessage>();
                List<Thread> threads = new ArrayList<Thread>();

                // starting all threads
                for (int i = 0; i < threadsNum; i++) {
                    Thread t = new MessageRetriever(from, to, msgs);
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
            return msgs;
        }

        public List<ZloMessage> getMessages(int from, int to) throws Exception {
            return getMessages(from, to, Config.THREADS_NUMBER);
        }

        public static int getLastRootMessageNumber() throws Exception {
            try {
                return PageRetriever.getLastRootMessageNumber();
            } catch (IOException e) {
                throw new Exception(e);
            }
        }
    }

    public static class DB implements IndexingSource {
        public static DB SOURCE = new DB();

        private DB(){}

        public static void saveMessages(List<ZloMessage> listZloMessages) throws Exception {
            try {
                DBManager.saveMessages(listZloMessages);
            } catch (DBException e) {
                throw new Exception(e);
            }
        }

        public ZloMessage getMessageByNumber(int num) throws Exception {
            try {
                return DBManager.getMessageByNumber(num);
            } catch (DBException e) {
                throw new Exception(e);
            }
        }

        public List<ZloMessage> getMessages(int start, int end) throws Exception {
            try {
                return DBManager.getMessagesByRange(start, end);
            } catch (DBException e) {
                throw new Exception(e);
            }
        }

    }
}
