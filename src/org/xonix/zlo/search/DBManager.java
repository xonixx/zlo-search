package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.AbstractList;
import java.util.Vector;
import java.util.Date;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DBManager {
    private static String insertPreparedStatement = "INSERT INTO messages(" +
        "num, " +
        "host, " +
        "topic, " +
        "title, " +
        "nick, "  +
        "date, "  +
        "reg, "  +
        "body) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static String deletePreparedStatement = "DELETE FROM messages " +
        "WHERE num=?";

    private static String selectMessageById = "SELECT * FROM messages WHERE " +
       "num=?";

    private static String selectMessagesInRange = "SELECT * FROM messages WHERE " +
        "num>? AND " + "num<?";

    public static void saveMessages(List<ZloMessage> listZloMessages, boolean update) throws SQLException, IOException {
        PreparedStatement chkstmt = Config.DBCONNECTION.prepareStatement(selectMessageById);
        PreparedStatement pstmt = Config.DBCONNECTION.prepareStatement(insertPreparedStatement);
        int numOfDeleted = 0;

        for (ZloMessage zloMessage : listZloMessages) {
            if (zloMessage != null){
                chkstmt.setInt(1, zloMessage.getNum());
                ResultSet rs = chkstmt.executeQuery();
                if (rs.wasNull() || (update && !rs.wasNull())) {
                    if (!rs.wasNull()){
                        PreparedStatement deletestmt = Config.DBCONNECTION.prepareStatement(deletePreparedStatement);
                        deletestmt.setInt(1, zloMessage.getNum());
                        numOfDeleted = deletestmt.executeUpdate();
                    }
                    if (rs.wasNull() || numOfDeleted>0){
                        pstmt.setInt(1, zloMessage.getNum());
                        pstmt.setString(2, zloMessage.getHost());
                        pstmt.setString(3, zloMessage.getTopic());
                        pstmt.setString(4, zloMessage.getTitle());
                        pstmt.setString(5, zloMessage.getNick());
                        pstmt.setTimestamp(6, new Timestamp(zloMessage.getDate().getTime()));
                        pstmt.setBoolean(7, zloMessage.isReg());
                        pstmt.setString(8, zloMessage.getBody());

                        pstmt.executeUpdate();
                    } else {
                        throw new SQLException("comodified DataBase");
                    }
                }
            }
        }
    }

    public static ZloMessage getMessageByNumber(int num) throws SQLException, UnsupportedEncodingException {
        PreparedStatement st = Config.DBCONNECTION.prepareStatement(selectMessageById);
        st.setInt(1, num);
        ResultSet rs = st.executeQuery();
        if (!rs.wasNull()){
            // ZloMessage(String userName, String hostName, String msgTopic, String msgTitle, String msgBody,
            // Date msgDate,
            // boolean reg, int urlNum)
            return new ZloMessage(
            rs.getString(ZloMessage.NICK),
            rs.getString(ZloMessage.HOST),
            rs.getString(ZloMessage.TOPIC),
            rs.getString(ZloMessage.TITLE),
            rs.getString(ZloMessage.BODY),
            rs.getTimestamp(ZloMessage.DATE),
            rs.getBoolean(ZloMessage.REG),
            rs.getInt(ZloMessage.URL_NUM));
        }
        else return null;
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws SQLException, UnsupportedEncodingException {
        PreparedStatement st = Config.DBCONNECTION.prepareStatement(selectMessagesInRange);
        st.setInt(1, start);
        st.setInt(2, end);
        ResultSet rs = st.executeQuery();
        List<ZloMessage> lmsg = new Vector<ZloMessage>();
        while (rs.next()){
            lmsg.add(
            new ZloMessage(
            rs.getString(ZloMessage.NICK),
            rs.getString(ZloMessage.HOST),
            rs.getString(ZloMessage.TOPIC),
            rs.getString(ZloMessage.TITLE),
            rs.getString(ZloMessage.BODY),
            rs.getTimestamp(ZloMessage.DATE),
            rs.getBoolean(ZloMessage.REG),
            rs.getInt(ZloMessage.URL_NUM))
            );
        }
        return lmsg;
    }
}
