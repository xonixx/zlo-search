package org.xonix.zlo.search;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.List;
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
        private static Site SOURCE = new Site();
        private Site() {} // not to create

        public ZloMessage getMessageByNumber(int num) throws Exception {
            logger.info("Receiving from site: " + num);
            try {
                return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
            } catch (IOException e) {
                throw new Exception(e);
            }
        }

        public List<ZloMessage> getMessages(int from, int to) throws Exception {
            return MultithreadedRetriever.getMessages(SOURCE, from, to, Config.THREADS_NUMBER);
        }

        public int getLastMessageNumber() throws Exception {
            try {
                return PageRetriever.getLastRootMessageNumber();
            } catch (IOException e) {
                throw new Exception(e);
            }
        }
        
        public static ZloMessage _getMessageByNumber(int num) throws Exception {
            return SOURCE.getMessageByNumber(num);
        }

        public static List<ZloMessage> _getMessages(int start, int end) throws Exception {
            return SOURCE.getMessages(start, end);
        }

        public static int _getLastMessageNumber() throws Exception {
            return SOURCE.getLastMessageNumber();
        }
    }

    public static class DB implements IndexingSource {
        private static DB SOURCE = new DB();

        private DB() {}

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

        public int getLastMessageNumber() throws Exception {
            try {
                return DBManager.getLastRootMessageNumber();
            } catch (DBException e) {
                throw new Exception(e);
            }
        }

        public static ZloMessage _getMessageByNumber(int num) throws Exception {
            return SOURCE.getMessageByNumber(num); 
        }

        public static List<ZloMessage> _getMessages(int start, int end) throws Exception {
            return SOURCE.getMessages(start, end);
        }

        public static int _getLastMessageNumber() throws Exception {
            return SOURCE.getLastMessageNumber();
        }
    }
}
