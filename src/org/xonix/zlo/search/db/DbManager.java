package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DbManager {
    private static final Logger logger = Logger.getLogger(DbManager.class);

    private static Properties props = Config.loadProperties("org/xonix/zlo/search/db/sql.properties");

    public static final String MSG_NICK = ZloMessage.NICK;
    public static final String MSG_ALT_NAME = "altName";
    public static final String MSG_HOST = ZloMessage.HOST;
    public static final String MSG_TOPIC = "topic";
    public static final String MSG_TOPIC_CODE = "topicCode";
    public static final String MSG_TITLE = "title"; // with html
    public static final String MSG_BODY = "body"; // with html
    public static final String MSG_DATE = "msgDate";
    public static final String MSG_REG = ZloMessage.REG;
    public static final String MSG_URL_NUM = ZloMessage.URL_NUM;
    public static final String MSG_PARENT_NUM = "parentNum";
    public static final String MSG_STATUS = ZloMessage.STATUS;

    private static final String SQL_INSERT_MSG =            props.getProperty("sql.insert.msg");
    private static final String SQL_UPDATE_MSG =            props.getProperty("sql.update.msg");
    private static final String SQL_DELETE_MSG =            props.getProperty("sql.delete.msg");
    private static final String SQL_SELECT_MSG_BY_ID =      props.getProperty("sql.select.msg.by.id");
    private static final String SQL_SELECT_MSG_IN_RANGE =   props.getProperty("sql.select.msg.in.range");
    private static final String SQL_SELECT_LAST_MSG_NUM =   props.getProperty("sql.select.last.msg.num");
    private static final String SQL_SELECT_SET =            props.getProperty("sql.select.set");
    private static final String SQL_SELECT_CHECK_ALIVE =    props.getProperty("sql.select.check.alive");
    private static final String SQL_SELECT_TOPICS =         props.getProperty("sql.select.topics");

    private static String[] topics;

    private static void fillPreparedStatement(PreparedStatement pstmt, ZloMessage msg) throws DbException {
        try {
            int i=0;
            pstmt.setInt(++i, msg.getNum());
            pstmt.setInt(++i, msg.getParentNum());
            pstmt.setString(++i, msg.getHost());
            pstmt.setInt(++i, msg.getTopicCode());
            pstmt.setString(++i, msg.getTitle());
            pstmt.setString(++i, msg.getNick());
            pstmt.setString(++i, msg.getAltName());
            pstmt.setTimestamp(++i, msg.getDate() == null
                    ? null
                    : new Timestamp(msg.getDate().getTime()));
            pstmt.setBoolean(++i, msg.isReg());
            pstmt.setString(++i, msg.getBody());
            pstmt.setInt(++i, msg.getStatus().getInt());
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
    
    public static void saveMessages(List<ZloMessage> msgs, boolean update) throws DbException {
        PreparedStatement chkPstmt = null;
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        ResultSet rs = null;
        try {
            Connection conn = Config.DB_CONNECTION;
            chkPstmt = conn.prepareStatement(SQL_SELECT_MSG_BY_ID);
            insertPstmt = conn.prepareStatement(SQL_INSERT_MSG);
            updatePstmt = conn.prepareStatement(SQL_UPDATE_MSG);

            for (ZloMessage msg : msgs) {
                String loggerMsg = "Inserting msg: " + msg.getNum() + "... ";

                chkPstmt.setInt(1, msg.getNum());
                rs = chkPstmt.executeQuery();

                if (!rs.next()) {
                    fillPreparedStatement(insertPstmt, msg);
                    insertPstmt.executeUpdate();
                    loggerMsg += "inserted.";
                } else if (update) {
                    fillPreparedStatement(updatePstmt, msg);
                    updatePstmt.setInt(9, msg.getNum());
                    if (updatePstmt.executeUpdate() == 0)
                        throw new SQLException("Failed to update record");
                    loggerMsg += "updated.";
                } else {
                    loggerMsg += "already exists.";
                }

                logger.debug(loggerMsg);
                rs.close();
            }
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            close(chkPstmt, insertPstmt, updatePstmt, rs);
        }
    }

    public static void saveMessagesFast(List<ZloMessage> msgs) throws DbException {
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        Connection conn = Config.DB_CONNECTION;
        try {
            conn.setAutoCommit(false);
            insertPstmt = conn.prepareStatement(SQL_INSERT_MSG);

            for (ZloMessage msg : msgs) {
                logger.debug("Adding msg: " + msg.getNum() + " to batch... ");
                fillPreparedStatement(insertPstmt, msg);
                insertPstmt.addBatch();
            }

            insertPstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                throw new DbException(e1);
            }
            throw new DbException(e);
        } finally {
            close(insertPstmt, rs);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DbException {
        saveMessages(msgs, false);
    }

    public static ZloMessage getMessageByNumber(int num) throws DbException {
        return getMessageByNumber(num, false);
    }

    private static ZloMessage getMessageByNumber(int num, boolean afterReconnect) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;

        if (Config.DB_CONNECTION == null)
            reopenConnectionIfNeeded();

        try {
            st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            st.setInt(1, num);
            rs = st.executeQuery();
            if (rs.next())
                return getMessage(rs);
            else
                return null;
        } catch (SQLException e) {
            if (afterReconnect) {
                throw new DbException(e);
            } else {
                reopenConnectionIfNeeded();
                return getMessageByNumber(num, true);
            }
        } finally {
            close(st, rs);
        }
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_IN_RANGE);
            st.setInt(1, start);
            st.setInt(2, end);
            rs = st.executeQuery();
            List<ZloMessage> msgs = new ArrayList<ZloMessage>();

            while (rs.next())
                msgs.add(getMessage(rs));

            return msgs;
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            close(st, rs);
        }
    }

    public static List<ZloMessage> getMessages(int[] nums, int fromIndex) throws DbException {
        return getMessages(nums, fromIndex, false);
    }

    private static List<ZloMessage> getMessages(int[] nums, int fromIndex, boolean afterReconnect) throws DbException {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for(int i=1; i<nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(SQL_SELECT_SET, sbNums.toString());

        ResultSet rs = null;
        Statement st = null;
        try {
            st = Config.DB_CONNECTION.createStatement();
            rs = st.executeQuery(sql);
            List<ZloMessage> msgs = new ArrayList<ZloMessage>();

            while (rs.next()) {
                ZloMessage msg = getMessage(rs);
                msg.setHitId(fromIndex++);
                msgs.add(msg);
            }

            return msgs;
        } catch (SQLException e) {
            if (afterReconnect) {
                throw new DbException(e);
            } else {
                reopenConnectionIfNeeded();
                return getMessages(nums, fromIndex, true);
            }
        } finally {
            close(st, rs);
        }

    }

    public static int getLastRootMessageNumber() throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
           st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_LAST_MSG_NUM);
           rs = st.executeQuery();
           if (rs.next()) {
               return rs.getInt(1);
           }
        } catch (SQLException e) {
           throw new DbException(e);
        } finally {
            close(st, rs);
        }
        return -1;
    }

    public static String[] getTopics() {
        if (topics == null) {
            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_TOPICS);
                rs = st.executeQuery();
                Map<Integer, String> topicsMap = new HashMap<Integer, String>();

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String  name = rs.getString(2);
                    topicsMap.put(id, name);
                }

                topics = new String[topicsMap.size()];
                for(int i=0; i<topicsMap.size(); i++) {
                    topics[i] = topicsMap.get(i);
                }
            } catch (SQLException e) {
                logger.fatal("Can't load topics", e);
            } finally {
                close(st, rs);
            }
        }
        return topics;
    }

    private static ZloMessage getMessage(ResultSet rs) throws SQLException {
        return new ZloMessage(
                rs.getString(MSG_NICK)
                , rs.getString(MSG_ALT_NAME)
                , rs.getString(MSG_HOST)
                , rs.getString(MSG_TOPIC)
                , rs.getInt(MSG_TOPIC_CODE)
                , rs.getString(MSG_TITLE)
                , rs.getString(MSG_BODY)
                , rs.getTimestamp(MSG_DATE)
                , rs.getBoolean(MSG_REG)
                , rs.getInt(MSG_URL_NUM)
                , rs.getInt(MSG_PARENT_NUM)
                , rs.getInt(MSG_STATUS));
    }

    public static void reopenConnectionIfNeeded() throws DbException {
        Statement checkStmt = null;
        try {
            boolean closed = Config.DB_CONNECTION == null || Config.DB_CONNECTION.isClosed();

            if (!closed) {
                checkStmt = Config.DB_CONNECTION.createStatement();

                try {
                    checkStmt.executeQuery(SQL_SELECT_CHECK_ALIVE);
                } catch(SQLException e) {
                    closed = true;
                }
            }

            if (closed) {
                close(Config.DB_CONNECTION);
                logger.info("Db connection closed, recreating...");
                Config.createConnection();

                if (Config.DB_CONNECTION == null)
                    throw new SQLException("Can't open connection");
            }
        } catch (SQLException e) {
            logger.error("Problem with recreating connection: " + e);
            throw new DbException(e);
        } finally {
            close(checkStmt);
        }
    }

    private static void close(Object obj) {
        if (obj == null)
            return;

        try {
            if (obj instanceof Statement) {
                ((Statement) obj).close();
            } else if (obj instanceof ResultSet) {
                ((ResultSet) obj).close();
            } else if (obj instanceof Closeable) {
                ((Closeable) obj).close();
            } else if (obj instanceof Connection) {
                ((Connection) obj).close();
            }
        } catch (SQLException e) {
            ;
        } catch(IOException e) {
            ;
        }
    }

    private static void close(Object... all) {
        for(Object obj : all) {
            close(obj);
        }
    }
}
