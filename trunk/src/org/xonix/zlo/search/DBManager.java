package org.xonix.zlo.search;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Vector;
import java.io.Closeable;
import java.io.IOException;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DBManager {
    private static Logger logger = Logger.getLogger(DBManager.class);

    public static final String MSG_NICK = ZloMessage.NICK;
    public static final String MSG_HOST = ZloMessage.HOST;
    public static final String MSG_TOPIC = ZloMessage.TOPIC;
    public static final String MSG_TITLE = "title"; // with html
    public static final String MSG_BODY = "body"; // with html
    public static final String MSG_DATE = "msgDate";
    public static final String MSG_REG = ZloMessage.REG;
    public static final String MSG_URL_NUM = ZloMessage.URL_NUM;
    public static final String MSG_STATUS = ZloMessage.STATUS;

    private static String SQL_INSERT_MSG = "INSERT INTO messages(" +
            "num, " +
            "host, " +
            "topic, " +
            "title, " +
            "nick, "  +
            "msgDate, "  +
            "reg, "  +
            "body," +
            "status)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static String SQL_UPDATE_MSG = "UPDATE messages " +
            "SET num=?, " +
            "host=?, " +
            "topic=?, " +
            "title=?, " +
            "nick=?, "  +
            "msgDate=?, "  +
            "reg=?, "  +
            "body=?" +
            "status=? WHERE num=?";

    private static String SQL_DELETE_MSG = "DELETE FROM messages WHERE num=?";
    private static String SQL_SELECT_MSG_BY_ID = "SELECT * FROM messages WHERE num=?";
    private static String SQL_SELECT_MSG_IN_RANGE = "SELECT * FROM messages WHERE num>=? AND num<?";
    private static String SQL_SELECT_LAST_MSG = "SELECT MAX(num) FROM messages";

    private static void fillPreparedStatement(PreparedStatement pstmt, ZloMessage msg) throws DBException {
        try {
            pstmt.setInt(1, msg.getNum());
            pstmt.setString(2, msg.getHost());
            pstmt.setString(3, msg.getTopic());
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
            chkPstmt = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            insertPstmt = Config.DB_CONNECTION.prepareStatement(SQL_INSERT_MSG);
            updatePstmt = Config.DB_CONNECTION.prepareStatement(SQL_UPDATE_MSG);

            for (ZloMessage msg : msgs) {
                chkPstmt.setInt(1, msg.getNum());
                rs = chkPstmt.executeQuery();
                if (!rs.next()) {
                    logger.debug("Inserting msg: " + msg.getNum());
                    fillPreparedStatement(insertPstmt, msg);
                    insertPstmt.executeUpdate();
                } else if (update) {
                    logger.debug("Updating msg: "+msg.getNum());
                    fillPreparedStatement(updatePstmt, msg);
                    updatePstmt.setInt(9, msg.getNum());
                    if (updatePstmt.executeUpdate() == 0)
                        throw new SQLException("Failed to update record");
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            close(chkPstmt, insertPstmt, updatePstmt, rs);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DBException {
        saveMessages(msgs, false);
    }

    public static ZloMessage getMessageByNumber(int num) throws DBException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            st.setInt(1, num);
            rs = st.executeQuery();
            if (rs.next()) {
                return new ZloMessage(
                        rs.getString(MSG_NICK),
                        rs.getString(MSG_HOST),
                        rs.getString(MSG_TOPIC),
                        rs.getString(MSG_TITLE),
                        rs.getString(MSG_BODY),
                        rs.getTimestamp(MSG_DATE),
                        rs.getBoolean(MSG_REG),
                        rs.getInt(MSG_URL_NUM),
                        null,
                        null,
                        ZloMessage.Status.fromInt(rs.getInt(MSG_STATUS)));
            } else
                return null;
        } catch (SQLException e) {
            throw new DBException(e);
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
            List<ZloMessage> msgs = new Vector<ZloMessage>();
            while (rs.next()) {
                msgs.add(
                        new ZloMessage(
                                rs.getString(MSG_NICK),
                                rs.getString(MSG_HOST),
                                rs.getString(MSG_TOPIC),
                                rs.getString(MSG_TITLE),
                                rs.getString(MSG_BODY),
                                rs.getTimestamp(MSG_DATE),
                                rs.getBoolean(MSG_REG),
                                rs.getInt(MSG_URL_NUM),
                                null,
                                null,
                                ZloMessage.Status.fromInt(rs.getInt(MSG_STATUS))));
            }
            return msgs;
        } catch (SQLException e) {
            throw new DBException(e);
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
