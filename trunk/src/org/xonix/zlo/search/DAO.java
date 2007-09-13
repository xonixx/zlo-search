package org.xonix.zlo.search;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Author: gubarkov
 * Date: 13.09.2007
 * Time: 16:40:04
 */

// data access object
public class DAO {
    public static class Site {
        private Site() {} // not to create

        public static ZloMessage getMessageByNumber(int num) throws IOException {
            return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
        }

        private static class MessageRetriever implements Runnable {
            private static int from = -1;
            private static int to = -1;
            private static int i = -1;

            private List<ZloMessage> msgs;


            public MessageRetriever(int from, int to, List<ZloMessage> msgs) {
                if (MessageRetriever.from == -1)
                    MessageRetriever.from = from;
                if (MessageRetriever.to == -1)
                    MessageRetriever.to = to;
                if (i == -1)
                    i = from;
                this.msgs = msgs;
                System.out.println("Born "+i);
            }

            private static boolean hasMoreToDownload() {
                return i <= to;
            }

            private int getNextNum() {
                return i++;
            }

            public void run() {
                System.out.println("Run " + i + ", " + from + ", " + to);
                while (hasMoreToDownload()) {
                    try {
                        System.out.println("Downloading " + i);
                        msgs.add(getMessageByNumber(getNextNum()));
                    } catch (IOException e) {
                        e.printStackTrace(); // todo: need to decide what to do here
                    }
                }
            }
        }

        public static List<ZloMessage> getMessages(final int from, final int to, int threadsNum) throws IOException {
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
                    Thread t = new Thread(new MessageRetriever(from, to, msgs));
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

        public static List<ZloMessage> getMessages(int from, int to) throws IOException {
            return getMessages(from, to, Config.THREADS_NUMBER);
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
