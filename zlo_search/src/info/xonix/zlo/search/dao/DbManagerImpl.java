package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.db.*;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageFields;
import info.xonix.zlo.search.model.Site;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static info.xonix.zlo.search.db.VarType.*;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DbManagerImpl extends DaoImplBase implements DbManager {
    private static final Logger logger = Logger.getLogger(DbManagerImpl.class);

    // TODO: remove
    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/sql.properties");

    // TODO: make this not use MessageFields!
    public static final String MSG_NICK = MessageFields.NICK;
    public static final String MSG_ALT_NAME = "altName";
    public static final String MSG_HOST = MessageFields.HOST;
    public static final String MSG_TOPIC = "topic";
    public static final String MSG_TOPIC_CODE = "topicCode";
    public static final String MSG_TITLE = "title"; // with html
    public static final String MSG_BODY = "body"; // with html
    public static final String MSG_DATE = "msgDate";
    public static final String MSG_REG = MessageFields.REG;
    public static final String MSG_URL_NUM = MessageFields.URL_NUM;
    public static final String MSG_PARENT_NUM = "parentNum";
    public static final String MSG_STATUS = Message.STATUS;

//    private DbAccessor dbAccessor;

/*
    private final String SQL_INSERT_MSG;// =            props.getProperty("sql.insert.msg");
    private final String SQL_INSERT_UPDATE_MSG;// =     props.getProperty("sql.insert.update.msg");
    private final String SQL_UPDATE_MSG;// =            props.getProperty("sql.update.msg");
    private final String SQL_DELETE_MSG;// =            props.getProperty("sql.delete.msg");
    private final String SQL_SELECT_MSG_BY_ID;// =      props.getProperty("sql.select.msg.by.id");
    private final String SQL_SELECT_MSG_IN_RANGE;// =   props.getProperty("sql.select.msg.in.range");
    private final String SQL_SELECT_LAST_MSG_NUM;// =   props.getProperty("sql.select.last.msg.num");
    private final String SQL_SELECT_SET;// =            props.getProperty("sql.select.set");
    private final String SQL_SELECT_ALL_TOPICS;// =     props.getProperty("sql.select.all.topics");
    private final String SQL_SELECT_NEW_TOPICS;// =     props.getProperty("sql.select.new.topics");
*/

    private final String SQL_LOG_REQUEST = props.getProperty("sql.log.request");

//    private Site site;
    private QueryProvider queryProvider;

    public void setQueryProvider(QueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    @Deprecated
    public DbManagerImpl(/*DbAccessor dbAcessor*/) {
//        this.dbAccessor = dbAcessor;

//        this.site = site;
//        String name = site.getName();
/*        SQL_INSERT_MSG = MessageFormat.format(props.getProperty("sql.insert.msg"), name);
        SQL_INSERT_UPDATE_MSG = MessageFormat.format(props.getProperty("sql.insert.update.msg"), name);

        SQL_UPDATE_MSG = MessageFormat.format(props.getProperty("sql.update.msg"), name);
        SQL_DELETE_MSG = MessageFormat.format(props.getProperty("sql.delete.msg"), name);

        SQL_SELECT_MSG_BY_ID = MessageFormat.format(props.getProperty("sql.select.msg.by.id"), name);
        SQL_SELECT_MSG_IN_RANGE = MessageFormat.format(props.getProperty("sql.select.msg.in.range"), name);
        SQL_SELECT_LAST_MSG_NUM = MessageFormat.format(props.getProperty("sql.select.last.msg.num"), name);
        SQL_SELECT_SET = MessageFormat.format(props.getProperty("sql.select.set"), name);

        SQL_SELECT_ALL_TOPICS = MessageFormat.format(props.getProperty("sql.select.all.topics"), name);
        SQL_SELECT_NEW_TOPICS = MessageFormat.format(props.getProperty("sql.select.new.topics"), name);*/
    }

    @Deprecated
    private void fillPreparedStatement(PreparedStatement pstmt, Message msg) throws DbException {
        DbUtils.setParams(pstmt,
                new Object[]{msg.getNum(), msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(),
                        msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt()},
                new VarType[]{INTEGER, INTEGER, STRING, INTEGER, STRING, STRING,
                        STRING, DATE, BOOLEAN, STRING, INTEGER});
    }

    @Override
    public void saveMessagesFast(Site site, List<Message> msgs) throws DbException {
        saveMessagesFast(site, msgs, false);
    }

    @Override
    public void saveMessagesFast(Site site, List<Message> msgs, boolean updateIfExists) throws DbException {
        PreparedStatement insertPstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection(getDataSource());
            conn.setAutoCommit(false);
            insertPstmt = conn.prepareStatement(updateIfExists
                    ? queryProvider.getInsertUpdateMsgQuery(site)
                    : queryProvider.getInsertMsgQuery(site));

            for (Message msg : msgs) {
                logger.debug("Adding msg: " + msg.getNum() + " to batch... ");

                if (!updateIfExists)
                    fillPreparedStatement(insertPstmt, msg);
                else {
                    DbUtils.setParams(insertPstmt,
                            new Object[]{msg.getNum(), msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(), msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt(),
                                    msg.getParentNum(), msg.getHost(), msg.getTopicCode(), msg.getTitle(), msg.getNick(), msg.getAltName(), msg.getTimestamp(), msg.isReg(), msg.getBody(), msg.getStatus().getInt()},
                            new VarType[]{INTEGER, INTEGER, STRING, INTEGER, STRING, STRING, STRING, DATE, BOOLEAN, STRING, INTEGER,
                                    INTEGER, STRING, INTEGER, STRING, STRING, STRING, DATE, BOOLEAN, STRING, INTEGER});
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

/*    public void saveMessages(List<Message> msgs) throws DbException {
        saveMessages(msgs, false);
    }*/

    // todo: test

    @Override
    public Message getMessageByNumber(Site site, int num) throws DbException {
        DbResult res = DbUtils.executeSelect(
                getDataSource(),
                queryProvider.getSelectMsgByIdQuery(site),
                new Object[]{num},
                new VarType[]{INTEGER});

        Message zm = null;
        if (res.next())
            zm = getMessage(res, site);

        res.close();
        return zm;
    }

    @Override
    public List<Message> getMessagesByRange(Site site, int start, int end) throws DbException {
        DbResult res = DbUtils.executeSelect(
                getDataSource(),
                queryProvider.getSelectMsgsInRangeQuery(site),
                new Object[]{start, end},
                new VarType[]{INTEGER, INTEGER});
        try {
            List<Message> msgs = new ArrayList<Message>();

            while (res.next())
                msgs.add(getMessage(res, site));

            return msgs;
        } finally {
            res.close();
        }
    }

    @Override
    public List<Message> getMessages(Site site, int[] nums, int fromIndex) throws DbException {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for (int i = 1; i < nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(queryProvider.getSelectSetQuery(site), sbNums.toString());

        DbResult res = DbUtils.executeSelect(getDataSource(), sql);

        List<Message> msgs = new ArrayList<Message>();

        while (res.next()) {
            Message msg = getMessage(res, site);
            msg.setHitId(fromIndex++);
            msg.setSite(site);
            msgs.add(msg);
        }

        res.close();
        return msgs;
    }

    @Override
    public int getLastMessageNumber(Site site) throws DbException {
        DbResult res = DbUtils.executeSelect(getDataSource(), queryProvider.getSelectLastMsgNumQuery(site));
        try {
            return res.getOneInt();
        } finally {
            res.close();
        }
    }

    private HashMap<String, Integer> topicsHashMap;
    // returns <topic name, topic code> where "topic name"s also include old codes

    public HashMap<String, Integer> getTopicsHashMap(Site site) throws DbException {
        if (topicsHashMap == null) {
            DbResult res = DbUtils.executeSelect(getDataSource(), queryProvider.getSelectAllTopicsQuery(site));
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

    public String[] getTopics(Site site) throws DbException {
        if (topics == null) {
            Map<Integer, String> topicsMap = new HashMap<Integer, String>();
            DbResult res = DbUtils.executeSelect(getDataSource(), queryProvider.getSelectNewTopicsQuery(site));
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

    @Override
    public void logRequest(int siteNum, String host, String userAgent,
                           String reqText, String reqNick, String reqHost,
                           String reqQuery, String reqQueryString, String referer, boolean rssAsked) throws DbException {
        DbUtils.executeUpdate(
                getDataSource(),
                SQL_LOG_REQUEST
                , new Object[]{
                        siteNum,
                        StringUtils.substring(host, 0, 100),
                        StringUtils.substring(userAgent, 0, 200),

                        StringUtils.substring(reqText, 0, 200),
                        StringUtils.substring(reqNick, 0, 100),
                        StringUtils.substring(reqHost, 0, 100),

                        StringUtils.substring(reqQuery, 0, 200),
                        StringUtils.substring(reqQueryString, 0, 400),
                        StringUtils.substring(referer, 0, 100),
                        rssAsked}
                , new VarType[]{INTEGER, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, BOOLEAN}
                , 1);
    }

    private Message getMessage(DbResult rs, Site site) throws DbException {
        return new Message(
                site,
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
