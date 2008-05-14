package info.xonix.zlo.search.db;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.Site;
import static info.xonix.zlo.search.db.VarType.*;
import info.xonix.zlo.search.model.ZloMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DbManager {
    private static final Logger logger = Logger.getLogger(DbManager.class);

    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/sql.properties");

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
    private static final String DB_DICT_LAST_INDEXED_DATE =          "lastIndexed_date";
    private static final String DB_DICT_LAST_INDEXED_DOUBLE =   "lastIndexedDouble";
    private static final String DB_DICT_LAST_INDEXED_DOUBLE_DATE =   "lastIndexedDouble_date";
    private static final String DB_DICT_LAST_SAVED_DATE =   "lastSavedDate";

    private DbAccessor dbAccessor;

    public DbManager(DbAccessor dbAcessor) {
        this.dbAccessor = dbAcessor;
    }

    private static final String SQL_INSERT_MSG =            props.getProperty("sql.insert.msg");
    private static final String SQL_INSERT_UPDATE_MSG =     props.getProperty("sql.insert.update.msg");
    private static final String SQL_UPDATE_MSG =            props.getProperty("sql.update.msg");
    private static final String SQL_DELETE_MSG =            props.getProperty("sql.delete.msg");
    private static final String SQL_SELECT_MSG_BY_ID =      props.getProperty("sql.select.msg.by.id");
    private static final String SQL_SELECT_MSG_IN_RANGE =   props.getProperty("sql.select.msg.in.range");
    private static final String SQL_SELECT_LAST_MSG_NUM =   props.getProperty("sql.select.last.msg.num");
    private static final String SQL_SELECT_SET =            props.getProperty("sql.select.set");
    private static final String SQL_SELECT_ALL_TOPICS =     props.getProperty("sql.select.all.topics");
    private static final String SQL_SELECT_NEW_TOPICS =     props.getProperty("sql.select.new.topics");

    private static final String SQL_LOG_REQUEST =           props.getProperty("sql.log.request");

    private void fillPreparedStatement(PreparedStatement pstmt, ZloMessage msg) throws DbException {
        DbUtils.setParams(pstmt,
                new Object[] {msg.getNum(), msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(),
                                msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt()},
                new VarType[] {INTEGER,     INTEGER,            STRING,         INTEGER,            STRING,         STRING,
                                STRING,             DATE,               BOOLEAN,   STRING,          INTEGER});
    }

/*    public void saveMessages(List<ZloMessage> msgs, boolean update) throws DbException {
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
    }*/

    public void saveMessagesFast(List<ZloMessage> msgs) throws DbException {
        saveMessagesFast(msgs, false);
    }

    public void saveMessagesFast(List<ZloMessage> msgs, boolean updateIfExists) throws DbException {
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection(dbAccessor.getDataSource());
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

/*    public void saveMessages(List<ZloMessage> msgs) throws DbException {
        saveMessages(msgs, false);
    }*/

    // todo: test
    public ZloMessage getMessageByNumber(int num) throws DbException {
        DbResult res = DbUtils.executeSelect(
                dbAccessor,
                SQL_SELECT_MSG_BY_ID,
                new Object[] {num},
                new VarType[] {INTEGER});

        ZloMessage zm = null;
        if (res.next())
            zm = getMessage(res);

        res.close();
        return zm;
    }

    public List<ZloMessage> getMessagesByRange(int start, int end) throws DbException {
        DbResult res = DbUtils.executeSelect(
                dbAccessor,
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

    public List<ZloMessage> getMessages(int[] nums, int fromIndex) throws DbException {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for(int i=1; i<nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(SQL_SELECT_SET, sbNums.toString());

        DbResult res = DbUtils.executeSelect(dbAccessor, sql);

        List<ZloMessage> msgs = new ArrayList<ZloMessage>();

        while (res.next()) {
            ZloMessage msg = getMessage(res);
            msg.setHitId(fromIndex++);
            msg.setSite((Site) dbAccessor);
            msgs.add(msg);
        }

        res.close();
        return msgs;
    }

    public int getLastMessageNumber() throws DbException {
        DbResult res = DbUtils.executeSelect(dbAccessor, SQL_SELECT_LAST_MSG_NUM);
        try {
            return res.getOneInt();
        } finally {
            res.close();
        }
    }

    private HashMap<String, Integer> topicsHashMap;
    // returns <topic name, topic code> where "topic name"s also include old codes
    public HashMap<String, Integer> getTopicsHashMap() throws DbException {
        if (topicsHashMap == null) {
            DbResult res = DbUtils.executeSelect(dbAccessor, SQL_SELECT_ALL_TOPICS);
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
    private String[] topics = null;
    public String[] getTopics() throws DbException {
        if (topics == null) {
            Map<Integer, String> topicsMap = new HashMap<Integer, String>();
            DbResult res = DbUtils.executeSelect(dbAccessor, SQL_SELECT_NEW_TOPICS);
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

    public void logRequest(int siteNum, String host, String userAgent,
                           String reqText, String reqNick, String reqHost,
                           String reqQuery, String reqQueryString, String referer) throws DbException {
        DbUtils.executeUpdate(
                dbAccessor,
                SQL_LOG_REQUEST
                , new Object[] {
                        siteNum,
                        StringUtils.substring(host, 0, 100),
                        StringUtils.substring(userAgent, 0, 200),

                        StringUtils.substring(reqText, 0, 200),
                        StringUtils.substring(reqNick, 0, 100),
                        StringUtils.substring(reqHost, 0, 100),

                        StringUtils.substring(reqQuery, 0, 200),
                        StringUtils.substring(reqQueryString, 0, 400),
                        StringUtils.substring(referer, 0, 100)}
                , new VarType[] {INTEGER, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING}
                , 1);
    }

    public void setLastIndexedNumber(int num) throws DbException {
        DbDict dbDict = dbAccessor.getDbDict();
        if (Config.USE_DOUBLE_INDEX) {
            dbDict.setInt(DB_DICT_LAST_INDEXED_DOUBLE, num);
            dbDict.setDate(DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date());
        } else {
            dbDict.setInt(DB_DICT_LAST_INDEXED, num);
            dbDict.setDate(DB_DICT_LAST_INDEXED_DATE, new Date());
        }
    }

    public int getLastIndexedNumber() throws DbException {
        return dbAccessor.getDbDict().getInt(Config.USE_DOUBLE_INDEX ? DB_DICT_LAST_INDEXED_DOUBLE : DB_DICT_LAST_INDEXED, 0);
    }

    public Date getLastIndexedDate() throws DbException {
        return dbAccessor.getDbDict().getDate(Config.USE_DOUBLE_INDEX ? DB_DICT_LAST_INDEXED_DOUBLE_DATE : DB_DICT_LAST_INDEXED_DATE, new Date(0));
    }

    public void setLastSavedDate(Date d) throws DbException {
        dbAccessor.getDbDict().setDate(DB_DICT_LAST_SAVED_DATE, d);
    }

    public Date getLastSavedDate() throws DbException {
        return dbAccessor.getDbDict().getDate(DB_DICT_LAST_SAVED_DATE, new Date(0));
    }

    private ZloMessage getMessage(DbResult rs) throws DbException {
        return new ZloMessage(
                (Site) dbAccessor,
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
