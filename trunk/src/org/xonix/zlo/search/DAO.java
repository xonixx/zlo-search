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

    public static class DAOException extends java.lang.Exception {
        private IndexingSource source;

        public DAOException(IndexingSource source, Throwable cause) {
            super(cause);
            this.source = source;
        }

        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }

        public IndexingSource getSource() {
            return source;
        }
    }

    public static class Site implements IndexingSource {
        public static Site SOURCE = new Site();
        private Site() {} // not to create

        public ZloMessage getMessageByNumber(int num) throws DAOException {
            logger.info("Receiving from site: " + num);
            try {
                return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
            } catch (IOException e) {
                throw new DAOException(SOURCE, e);
            }
        }

        public List<ZloMessage> getMessages(int from, int to) throws DAOException {
            return MultithreadedRetriever.getMessages(SOURCE, from, to, Config.THREADS_NUMBER);
        }

        public int getLastMessageNumber() throws DAOException {
            try {
                return PageRetriever.getLastRootMessageNumber();
            } catch (IOException e) {
                throw new DAOException(SOURCE, e);
            }
        }
        
        public static ZloMessage _getMessageByNumber(int num) throws DAOException {
            return SOURCE.getMessageByNumber(num);
        }

        public static List<ZloMessage> _getMessages(int start, int end) throws DAOException {
            return SOURCE.getMessages(start, end);
        }

        public static int _getLastMessageNumber() throws DAOException {
            return SOURCE.getLastMessageNumber();
        }
    }

    public static class DB implements IndexingSource {
        public static DB SOURCE = new DB();

        private DB() {}

        public static void saveMessages(List<ZloMessage> listZloMessages) throws DAOException {
            try {
                DBManager.saveMessages(listZloMessages);
            } catch (DBException e) {
                throw new DAOException(SOURCE, e);
            }
        }

        public ZloMessage getMessageByNumber(int num) throws DAOException {
            try {
                return DBManager.getMessageByNumber(num);
            } catch (DBException e) {
                throw new DAOException(SOURCE, e);
            }
        }

        public List<ZloMessage> getMessages(int start, int end) throws DAOException {
            try {
                return DBManager.getMessagesByRange(start, end);
            } catch (DBException e) {
                throw new DAOException(SOURCE, e);
            }
        }

        public int getLastMessageNumber() throws DAOException {
            try {
                return DBManager.getLastRootMessageNumber();
            } catch (DBException e) {
                throw new DAOException(SOURCE, e);
            }
        }

        public static ZloMessage _getMessageByNumber(int num) throws DAOException {
            return SOURCE.getMessageByNumber(num); 
        }

        public static List<ZloMessage> _getMessages(int start, int end) throws DAOException {
            return SOURCE.getMessages(start, end);
        }

        public static int _getLastMessageNumber() throws DAOException {
            return SOURCE.getLastMessageNumber();
        }
    }
}
