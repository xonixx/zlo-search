package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

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

    public static final String MSG_NICK = ZloMessage.FIELDS.NICK;
    public static final String MSG_ALT_NAME = "altName";
    public static final String MSG_HOST = ZloMessage.FIELDS.HOST;
    public static final String MSG_TOPIC = "topic";
    public static final String MSG_TOPIC_CODE = "topicCode";
    public static final String MSG_TITLE = "title"; // with html
    public static final String MSG_BODY = "body"; // with html
    public static final String MSG_DATE = "msgDate";
    public static final String MSG_REG = ZloMessage.FIELDS.REG;
    public static final String MSG_URL_NUM = ZloMessage.FIELDS.URL_NUM;
    public static final String MSG_PARENT_NUM = "parentNum";
    public static final String MSG_STATUS = ZloMessage.STATUS;

    private static final String SQL_INSERT_MSG =            props.getProperty("sql.insert.msg");
    private static final String SQL_UPDATE_MSG =            props.getProperty("sql.update.msg");
    private static final String SQL_DELETE_MSG =            props.getProperty("sql.delete.msg");
    private static final String SQL_SELECT_MSG_BY_ID =      props.getProperty("sql.select.msg.by.id");
    private static final String SQL_SELECT_MSG_IN_RANGE =   props.getProperty("sql.select.msg.in.range");
    private static final String SQL_SELECT_LAST_MSG_NUM =   props.getProperty("sql.select.last.msg.num");
    private static final String SQL_SELECT_SET =            props.getProperty("sql.select.set");
    private static final String SQL_SELECT_TOPICS =         props.getProperty("sql.select.topics");

    private static final String SQL_LOG_REQUEST =           props.getProperty("sql.log.request");

    private static final String SQL_MARK_AS_INDEXED =       props.getProperty("sql.indexer.mark.as.indexed");
    private static final String SQL_MARK_AS_INDEXED_RANGE = props.getProperty("sql.indexer.mark.as.indexed.range");
    private static final String SQL_SELECT_LAST_INDEXED =   props.getProperty("sql.indexer.select.last.indexed.num");

    private static final String SQL_SELECT_LAST_MSGS =      props.getProperty("sql.web.select.last.msgs");

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
            Connection conn = DbUtils.getConnection();
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
            DbUtils.close(chkPstmt, insertPstmt, updatePstmt, rs);
        }
    }

    public static void saveMessagesFast(List<ZloMessage> msgs) throws DbException {
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DbUtils.getConnection();
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
            DbUtils.close(insertPstmt, rs);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DbException {
        saveMessages(msgs, false);
    }

    public static ZloMessage getMessageByNumber(int num) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = DbUtils.getConnection().prepareStatement(SQL_SELECT_MSG_BY_ID);
            st.setInt(1, num);
            rs = st.executeQuery();
            if (rs.next())
                return getMessage(rs);
            else
                return null;
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            DbUtils.close(st, rs);
        }
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = DbUtils.getConnection().prepareStatement(SQL_SELECT_MSG_IN_RANGE);
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
            DbUtils.close(st, rs);
        }
    }

    public static List<ZloMessage> getMessages(int[] nums, int fromIndex) throws DbException {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for(int i=1; i<nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(SQL_SELECT_SET, sbNums.toString());

        DbUtils.Result res = DbUtils.executeSelect(sql);
        ResultSet rs = res.getResultSet();
        try {
            List<ZloMessage> msgs = new ArrayList<ZloMessage>();

            while (rs.next()) {
                ZloMessage msg = getMessage(rs);
                msg.setHitId(fromIndex++);
                msgs.add(msg);
            }

            return msgs;
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            res.close();
        }
    }

    public static int getLastMessageNumber() throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_LAST_MSG_NUM);
        try {
            return res.getInt1();
        } finally {
            res.close();
        }
    }

    public static String[] getTopics() throws DbException {
        if (topics == null) {
            DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_TOPICS);
            try {
                Map<Integer, String> topicsMap = new HashMap<Integer, String>();
                ResultSet rs = res.getResultSet();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    topicsMap.put(id, name);
                }

                topics = new String[topicsMap.size()];
                for(int i=0; i<topicsMap.size(); i++) {
                    topics[i] = topicsMap.get(i);
                }
            } catch (SQLException e) {
                logger.fatal("Can't load topics", e);
            } finally {
                res.close();
            }
        }
        return topics;
    }

    public static void logRequest(String host, String userAgent, String reqText, String reqQuery, String referer) throws DbException {
        DbUtils.executeUpdate(
                SQL_LOG_REQUEST
                , new Object[] {
                        StringUtils.substring(host, 0, 100),
                        StringUtils.substring(userAgent, 0, 100),
                        StringUtils.substring(reqText, 0, 200),
                        StringUtils.substring(reqQuery, 0, 200),
                        StringUtils.substring(referer, 0, 100)}
                , new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR}
                , 1);
    }

    public static void markAsIndexed(int num) throws DbException {
        DbUtils.executeUpdate(
                SQL_MARK_AS_INDEXED
                , new Object[] {num}
                , new int[] {Types.INTEGER}
                , 1);
    }

    public static void markRangeAsIndexed(int from, int to) throws DbException {
        DbUtils.executeUpdate(
                SQL_MARK_AS_INDEXED_RANGE
                , new Object[] {from, to}
                , new int[] {Types.INTEGER, Types.INTEGER});
    }

    public static int getLastIndexedNumber() throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_LAST_INDEXED);
        try {
            return res.getInt1();
        } finally {
            res.close();
        }
    }

    /*
    returns 2 int vals:
    1. last saved message num or 0 if absent
    2. last indexed msg num or 0 if absent
     */
    public static int[] getLastMessageNums() throws DbException {
        int[] result = new int[2];
        DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_LAST_MSGS);
        try {
            ResultSet rs = res.getResultSet();
            rs.next();
            result[0] = rs.getInt(1);
            rs.next();
            result[1] = rs.getInt(1);
        } catch(SQLException e) {
            throw new DbException(e);
        }
        return result;
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
}
