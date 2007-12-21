package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.sql.*;
import java.util.*;

import static org.xonix.zlo.search.db.VarType.*;

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

    private static final String DB_DICT_LAST_INDEXED =          "lastIndexed";
    private static final String DB_DICT_LAST_INDEXED_DOUBLE =   "lastIndexedDouble";
    private static final String TABLES_KEY_PREFIX = "db.tables.";

    private static List<String> tables = null;
    private static List<String> getTableNames() {
        if (tables == null) {
            tables = new ArrayList<String>();
            for (Object key : Config.getAppProperties().keySet()) {
                String k = (String) key;
                if (k.startsWith(TABLES_KEY_PREFIX)) {
                    tables.add(k.replaceFirst(TABLES_KEY_PREFIX, ""));
                }
            }
        }
        return tables;
    }

    private static String prepareSql(String sqlName) {
        String sql = props.getProperty(sqlName);
        for (String tab : getTableNames()) {
            sql = sql.replaceAll("\\{" + tab + "\\}", Config.getProp(TABLES_KEY_PREFIX + tab));
        }
        return sql;
    }

    private static final String SQL_INSERT_MSG =            prepareSql("sql.insert.msg");
    private static final String SQL_INSERT_UPDATE_MSG =     prepareSql("sql.insert.update.msg");
    private static final String SQL_UPDATE_MSG =            prepareSql("sql.update.msg");
    private static final String SQL_DELETE_MSG =            prepareSql("sql.delete.msg");
    private static final String SQL_SELECT_MSG_BY_ID =      prepareSql("sql.select.msg.by.id");
    private static final String SQL_SELECT_MSG_IN_RANGE =   prepareSql("sql.select.msg.in.range");
    private static final String SQL_SELECT_LAST_MSG_NUM =   prepareSql("sql.select.last.msg.num");
    private static final String SQL_SELECT_SET =            prepareSql("sql.select.set");
    private static final String SQL_SELECT_ALL_TOPICS =     prepareSql("sql.select.all.topics");
    private static final String SQL_SELECT_NEW_TOPICS =     prepareSql("sql.select.new.topics");

    private static final String SQL_LOG_REQUEST =           prepareSql("sql.log.request");

    private static HashMap<String, Integer> topicsHashMap;

    private static void fillPreparedStatement(PreparedStatement pstmt, ZloMessage msg) throws DbException {
        DbUtils.setParams(pstmt,
                new Object[] {msg.getNum(), msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(),
                                msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt()},
                new VarType[] {INTEGER,     INTEGER,            STRING,         INTEGER,            STRING,         STRING,
                                STRING,             DATE,               BOOLEAN,   STRING,          INTEGER});
    }

    public static void saveMessages(List<ZloMessage> msgs, boolean update) throws DbException {
        PreparedStatement chkPstmt = null;
        PreparedStatement insertPstmt = null;
        PreparedStatement updatePstmt = null;
        ResultSet rs = null;
        try {
            Connection conn = ConnectionUtils.getConnection();
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
            CloseUtils.close(chkPstmt, insertPstmt, updatePstmt, rs);
        }
    }

    public static void saveMessagesFast(List<ZloMessage> msgs) throws DbException {
        saveMessagesFast(msgs, false);
    }

    public static void saveMessagesFast(List<ZloMessage> msgs, boolean updateIfExists) throws DbException {
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            conn.setAutoCommit(false);
            insertPstmt = conn.prepareStatement(updateIfExists ? SQL_INSERT_UPDATE_MSG : SQL_INSERT_MSG);

            for (ZloMessage msg : msgs) {
                logger.debug("Adding msg: " + msg.getNum() + " to batch... ");

                if (!updateIfExists)
                    fillPreparedStatement(insertPstmt, msg);
                else {
                    DbUtils.setParams(insertPstmt,
                            new Object[] {msg.getNum(), msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(), msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt(),
                                                        msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(), msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt() },
                            new VarType[] {INTEGER,     INTEGER,            STRING,         INTEGER,            STRING,         STRING,         STRING,         DATE,               BOOLEAN,     STRING,        INTEGER,
                                                        INTEGER,            STRING,         INTEGER,            STRING,         STRING,         STRING,         DATE,               BOOLEAN,     STRING,        INTEGER});
                }

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
            CloseUtils.close(insertPstmt, rs);
        }
    }

    public static void saveMessages(List<ZloMessage> msgs) throws DbException {
        saveMessages(msgs, false);
    }

    // todo: test
    public static ZloMessage getMessageByNumber(int num) throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(
                SQL_SELECT_MSG_BY_ID,
                new Object[] {num},
                new VarType[] {INTEGER});

        ZloMessage zm = null;
        if (res.next())
            zm = getMessage(res);

        res.close();
        return zm;
    }

    public static List<ZloMessage> getMessagesByRange(int start, int end) throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(
                SQL_SELECT_MSG_IN_RANGE,
                new Object[] {start, end},
                new VarType[] {INTEGER, INTEGER});
        try {
            List<ZloMessage> msgs = new ArrayList<ZloMessage>();

            while (res.next())
                msgs.add(getMessage(res));

            return msgs;
        } finally {
            res.close();
        }
    }

    public static List<ZloMessage> getMessages(int[] nums, int fromIndex) throws DbException {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for(int i=1; i<nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(SQL_SELECT_SET, sbNums.toString());

        DbUtils.Result res = DbUtils.executeSelect(sql);

        List<ZloMessage> msgs = new ArrayList<ZloMessage>();

        while (res.next()) {
            ZloMessage msg = getMessage(res);
            msg.setHitId(fromIndex++);
            msgs.add(msg);
        }

        res.close();
        return msgs;
    }

    public static int getLastMessageNumber() throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_LAST_MSG_NUM);
        try {
            return res.getOneInt();
        } finally {
            res.close();
        }
    }

    // returns <topic name, topic code> where "topic name"s also includes old codes
    public static HashMap<String, Integer> getTopicsHashMap() throws DbException {
        if (topicsHashMap == null) {
            DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_ALL_TOPICS);
            try {
                topicsHashMap = new HashMap<String, Integer>();
                while (res.next()) {
                    topicsHashMap.put(res.getString(2), res.getInt(1));
                }
            } finally {
                res.close();
            }
        }
        return topicsHashMap;
    }

    // returns only "new" topics - current posible topics on site
    private static String[] topics = null;
    public static String[] getTopics() throws DbException {
        if (topics == null) {
            Map<Integer, String> topicsMap = new HashMap<Integer, String>();
            DbUtils.Result res = DbUtils.executeSelect(SQL_SELECT_NEW_TOPICS);
            while (res.next()) {
                topicsMap.put(res.getInt(1), res.getString(2));
            }
            topics = new String[topicsMap.size()];
            for (int id : topicsMap.keySet()) {
                topics[id] = topicsMap.get(id);
            }
            res.close();
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
                , new VarType[] {STRING, STRING, STRING, STRING, STRING}
                , 1);
    }

    public static void setLastIndexedNumber(int num) throws DbException {
        if (Config.USE_DOUBLE_INDEX)
            DbDict.setInt(DB_DICT_LAST_INDEXED_DOUBLE, num);
        else
            DbDict.setInt(DB_DICT_LAST_INDEXED, num);
    }

    public static int getLastIndexedNumber() throws DbException {
        Integer lastIndexed = DbDict.getInt(Config.USE_DOUBLE_INDEX ? DB_DICT_LAST_INDEXED_DOUBLE : DB_DICT_LAST_INDEXED);
        return lastIndexed == null ? 0 : lastIndexed;
    }

    private static ZloMessage getMessage(DbUtils.Result rs) throws DbException {
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
