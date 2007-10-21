package org.xonix.zlo.search;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DBManager {
    private static final Logger logger = Logger.getLogger(DBManager.class);

    public static final String MSG_NICK = ZloMessage.NICK;
    public static final String MSG_HOST = ZloMessage.HOST;
    public static final String MSG_TOPIC = ZloMessage.TOPIC;
    public static final String MSG_TITLE = "title"; // with html
    public static final String MSG_BODY = "body"; // with html
    public static final String MSG_DATE = "msgDate";
    public static final String MSG_REG = ZloMessage.REG;
    public static final String MSG_URL_NUM = ZloMessage.URL_NUM;
    public static final String MSG_STATUS = ZloMessage.STATUS;

    private static final String SQL_INSERT_MSG = "INSERT INTO messages(" +
            "num," +
            "host," +
            "topic," +
            "title," +
            "nick," +
            "msgDate," +
            "reg," +
            "body," +
            "status)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_MSG = "UPDATE messages " +
            "SET num=?," +
            "host=?," +
            "topic=?," +
            "title=?," +
            "nick=?," +
            "msgDate=?," +
            "reg=?," +
            "body=?" +
            "status=? WHERE num=?";

    private static final String SQL_DELETE_MSG = "DELETE FROM messages WHERE num=?";
    private static final String SQL_SELECT_MSG_BY_ID = "SELECT * FROM messages WHERE num=?";
    private static final String SQL_SELECT_MSG_IN_RANGE = "SELECT * FROM messages WHERE num>=? AND num<?";
    private static final String SQL_SELECT_LAST_MSG = "SELECT MAX(num) FROM messages";

    private static final String SQL_SELECT_SET = "SELECT * FROM messages WHERE num in (%1s) ORDER BY msgDate DESC;";
    private static final String SQL_SELECT_CHECK_ALIVE = "SELECT 1;";

    private static void fillPreparedStatement(PreparedStatement pstmt, ZloMessage msg) throws DBException {
        try {
            pstmt.setInt(1, msg.getNum());
            pstmt.setString(2, msg.getHost());
            pstmt.setInt(3, ZloMessage.TOPIC_CODES.get(msg.getTopic()));
            pstmt.setString(4, msg.getTitle());
            pstmt.setString(5, msg.getNick());
            pstmt.setTimestamp(6, msg.getDate() == null
                    ? null
                    : new Timestamp(msg.getDate().getTime()));
            pstmt.setBoolean(7, msg.isReg());
            pstmt.setString(8, msg.getBody());
            pstmt.setInt(9, msg.getStatus().getInt());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
    
    public static void saveMessages(List<ZloMessage> msgs, boolean update) throws DBException {
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
            throw new DBException(e);
        } finally {
            close(chkPstmt, insertPstmt, updatePstmt, rs);
        }
    }

    public static void saveMessagesFast(List<ZloMessage> msgs) throws DBException {
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
                throw new DBException(e1);
            }
            throw new DBException(e);
        } finally {
            close(insertPstmt, rs);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DBException {
        saveMessages(msgs, false);
    }

    public static ZloMessage getMessageByNumber(int num) throws DBException {
        return getMessageByNumber(num, false);
    }

    private static ZloMessage getMessageByNumber(int num, boolean afterReconnect) throws DBException {
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
                throw new DBException(e);
            } else {
                reopenConnectionIfNeeded();
                return getMessageByNumber(num, true);
            }
        } finally {
            close(st, rs);
        }
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws DBException {
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
            throw new DBException(e);
        } finally {
            close(st, rs);
        }
    }

    public static List<ZloMessage> getMessages(int[] nums, int fromIndex) throws DBException {
        return getMessages(nums, fromIndex, false);
    }

    private static List<ZloMessage> getMessages(int[] nums, int fromIndex, boolean afterReconnect) throws DBException {
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
                throw new DBException(e);
            } else {
                reopenConnectionIfNeeded();
                return getMessages(nums, fromIndex, true);
            }
        } finally {
            close(st, rs);
        }

    }

    public static int getLastRootMessageNumber() throws DBException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
           st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_LAST_MSG);
           rs = st.executeQuery();
           if (rs.next()) {
               return rs.getInt(1);
           }
        } catch (SQLException e) {
           throw new DBException(e);
        } finally {
            close(st, rs);
        }
        return -1;
    }

    private static ZloMessage getMessage(ResultSet rs) throws SQLException {
        return new ZloMessage(
                rs.getString(MSG_NICK)
                , rs.getString(MSG_HOST)
                , rs.getInt(MSG_TOPIC)
                , rs.getString(MSG_TITLE)
                , rs.getString(MSG_BODY)
                , rs.getTimestamp(MSG_DATE)
                , rs.getBoolean(MSG_REG)
                , rs.getInt(MSG_URL_NUM)
                , null
                , null
                , ZloMessage.Status.fromInt(rs.getInt(MSG_STATUS)));
    }

    public static void reopenConnectionIfNeeded() throws DBException {
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
            throw new DBException(e);
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
