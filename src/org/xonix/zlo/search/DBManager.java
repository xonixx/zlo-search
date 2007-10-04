package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DBManager {
    private static String SQL_INSERT_MSG = "INSERT INTO messages(" +
        "num, " +
        "host, " +
        "topic, " +
        "title, " +
        "nick, "  +
        "date, "  +
        "reg, "  +
        "body) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static String SQL_UPDATE_MSG = "UPDATE messages " +
        "SET num=?, " +
        "host=?, " +
        "topic=?, " +
        "title=?, " +
        "nick=?, "  +
        "date=?, "  +
        "reg=?, "  +
        "body=? WHERE num=?";

    private static String SQL_DELETE_MSG = "DELETE FROM messages WHERE num=?";
    private static String SQL_SELECT_MSG_BY_ID = "SELECT * FROM messages WHERE num=?";
    private static String SQL_SELECT_MSG_IN_RANGE = "SELECT * FROM messages WHERE num>? AND num<?";
    private static String SQL_SELECT_LAST_MSG = "SELECT MAX(num) FROM messages";

    private static void fillPreparedStatement(PreparedStatement pstmt, ZloMessage zloMessage) throws DBException {
        try {
            if (zloMessage != null) {
                pstmt.setInt(1, zloMessage.getNum());
                pstmt.setString(2, zloMessage.getHost());
                pstmt.setString(3, zloMessage.getTopic());
                pstmt.setString(4, zloMessage.getTitle());
                pstmt.setString(5, zloMessage.getNick());
                pstmt.setTimestamp(6, new Timestamp(zloMessage.getDate().getTime()));
                pstmt.setBoolean(7, zloMessage.isReg());
                pstmt.setString(8, zloMessage.getBody());
           }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
    
    public static void saveMessages(List<ZloMessage> msgs, boolean update) throws DBException {
        try {
            PreparedStatement chkstmt = Config.DB_CONNECTION.prepareStatement(SQL_SELECT_MSG_BY_ID);
            PreparedStatement insertPstmt = Config.DB_CONNECTION.prepareStatement(SQL_INSERT_MSG);
            PreparedStatement updatePstmt = Config.DB_CONNECTION.prepareStatement(SQL_UPDATE_MSG);

            for (ZloMessage zloMessage : msgs) {
                chkstmt.setInt(1, zloMessage.getNum());
                ResultSet rs = chkstmt.executeQuery();
                if (!rs.next()) {
                    fillPreparedStatement(insertPstmt, zloMessage);
                    insertPstmt.executeUpdate();
                } else if (update){
                    fillPreparedStatement(updatePstmt, zloMessage);
                    updatePstmt.setInt(9, zloMessage.getNum());
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
            if (!rs.wasNull()) {
                return new ZloMessage(
                        rs.getString(ZloMessage.NICK),
                        rs.getString(ZloMessage.HOST),
                        rs.getString(ZloMessage.TOPIC),
                        rs.getString(ZloMessage.TITLE),
                        rs.getString(ZloMessage.BODY),
                        rs.getTimestamp(ZloMessage.DATE),
                        rs.getBoolean(ZloMessage.REG),
                        rs.getInt(ZloMessage.URL_NUM),
                        null,
                        null,
                        null); // todo: need to fix!
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
                                rs.getString(ZloMessage.NICK),
                                rs.getString(ZloMessage.HOST),
                                rs.getString(ZloMessage.TOPIC),
                                rs.getString(ZloMessage.TITLE),
                                rs.getString(ZloMessage.BODY),
                                rs.getTimestamp(ZloMessage.DATE),
                                rs.getBoolean(ZloMessage.REG),
                                rs.getInt(ZloMessage.URL_NUM),
                                null,
                                null,
                                null)); // todo: need to fix!
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
