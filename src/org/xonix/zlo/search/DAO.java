package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.config.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;

/**
 * Author: gubarkov
 * Date: 13.09.2007
 * Time: 16:40:04
 */

// data access object
public class DAO {
    public static class Site {
        public static ZloMessage getMessageByNumber(int num) throws IOException {
            return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
        }

        public static List<ZloMessage> getMessages(int from, int to, int threads) throws IOException {
            List<ZloMessage> msgs;
            if (threads == 1) {
                msgs = new ArrayList<ZloMessage>();

                for (int i = from; i <= to; i++) {
                    msgs.add(getMessageByNumber(i));
                }
            } else {
                msgs = new Vector<ZloMessage>();
                // todo: implement multithreaded
            }
            return msgs;
        }

        public static List<ZloMessage> getMessages(int from, int to) throws IOException {
            return getMessages(from, to, 1);
        }

        public static int getLastRootMessageNumber() throws IOException {
            return PageRetriever.getLastRootMessageNumber();
        }
    }

    public static class DB {
        private static String insertPreparedStatement = "INSERT INTO messages(id, topic, title, nick, mdate, body) VALUES (?, ?, ?, ?, ?, ?)";

        private static String selectMessageById = "SELECT * FROM messages WHERE id=?";

        public void saveMsgs(int startNum, int endNum) throws SQLException, IOException {
        PreparedStatement pstmt = Config.DBCONNECTION.prepareStatement(insertPreparedStatement);

        for (int i=startNum; i<=endNum; i++) {
            ZloMessage msg = PageParser.parseMessage(PageRetriever.getPageContentByNumber(i), i);
            if (msg != null) {
                pstmt.setInt(1, msg.getNum());
                pstmt.setString(2, msg.getTopic());
                pstmt.setString(3, msg.getTitle());
                pstmt.setString(4, msg.getNick());
                pstmt.setTimestamp(5, new Timestamp(msg.getDate().getTime()));
                pstmt.setString(6, msg.getBody());

                pstmt.executeUpdate();
            }
        }
    }

    public ZloMessage getMessageByNumber(int count) throws SQLException, UnsupportedEncodingException {
        PreparedStatement st = Config.DBCONNECTION.prepareStatement(selectMessageById);
        st.setInt(1, count);
        ResultSet rs = st.executeQuery();
        ZloMessage msg = new ZloMessage();
        while (rs.next()){
            msg.setNum(rs.getInt("id"));
            msg.setTopic(rs.getString("topic"));
            msg.setNick(rs.getString("nick"));
            msg.setTitle(rs.getString("title"));
            msg.setTopic(rs.getString("topic"));
            msg.setDate(rs.getTimestamp("mdate"));
            msg.setBody(rs.getString("body"));
        }
        return msg;
    }

    }
}
