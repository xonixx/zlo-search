package org.xonix.zlo.search;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DBManager {
    private static Logger log = Logger.getLogger(DBManager.class.getName());

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
    private static String SQL_SELECT_MSG_IN_RANGE = "SELECT * FROM messages WHERE num>? AND num<?";
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
        try {
            PreparedStatement chkPstmt = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            PreparedStatement insertPstmt = Config.DB_CONNECTION.prepareStatement(SQL_INSERT_MSG);
            PreparedStatement updatePstmt = Config.DB_CONNECTION.prepareStatement(SQL_UPDATE_MSG);

            for (ZloMessage msg : msgs) {
                chkPstmt.setInt(1, msg.getNum());
                ResultSet rs = chkPstmt.executeQuery();
                if (!rs.next()) {
                    log.info("Inserting msg: " + msg.getNum());
                    fillPreparedStatement(insertPstmt, msg);
                    insertPstmt.executeUpdate();
                } else if (update) {
                    log.info("Updating msg: "+msg.getNum());
                    fillPreparedStatement(updatePstmt, msg);
                    updatePstmt.setInt(9, msg.getNum());
                    if (updatePstmt.executeUpdate() == 0)
                        throw new SQLException("Failed to update record");
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DBException {
        saveMessages(msgs, false);
    }

    public static ZloMessage getMessageByNumber(int num) throws DBException {
        try {
            PreparedStatement st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            st.setInt(1, num);
            ResultSet rs = st.executeQuery();
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
                        ZloMessage.Status.fromInt(rs.getInt(MSG_STATUS))); // todo: need to fix!
            } else
                return null;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws DBException {
        try {
            PreparedStatement st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_IN_RANGE);
            st.setInt(1, start);
            st.setInt(2, end);
            ResultSet rs = st.executeQuery();
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
                                ZloMessage.Status.fromInt(rs.getInt(MSG_STATUS)))); // todo: need to fix!
            }
            return msgs;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public static int getLastRootMessageNumber() throws DBException {
           try {
               PreparedStatement st = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_LAST_MSG);
               ResultSet rs = st.executeQuery();
               if (rs.next()) {
                   return rs.getInt(1);
               }
           } catch (SQLException e) {
               throw new DBException(e);
           }
           return -1;
    }

}
