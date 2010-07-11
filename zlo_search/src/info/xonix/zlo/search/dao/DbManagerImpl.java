package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.Topic;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static info.xonix.zlo.search.db.DbUtils.timestamp;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class DbManagerImpl extends DaoImplBase implements DbManager {
    private static final Logger log = Logger.getLogger(DbManagerImpl.class);

    // TODO: remove
    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/sql.properties");

    private final String SQL_LOG_REQUEST = props.getProperty("sql.log.request");

    private QueryProvider queryProvider;

    public void setQueryProvider(QueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        Check.isSet(queryProvider, "queryProvider");
    }

    private RowMapper<Message> messageRowMapper = new RowMapper<Message>() {
        @Override
        public Message mapRow(ResultSet rs, int i) throws SQLException {
            return new Message(
                    Site.forName(rs.getString("site")),
                    rs.getString("nick")
                    , rs.getString("altName")
                    , rs.getString("host")
                    , rs.getString("topic")
                    , rs.getInt("topicCode")
                    , rs.getString("title")
                    , rs.getString("body")
                    , rs.getTimestamp("msgDate")
                    , rs.getBoolean("reg")
                    , rs.getInt("num")
                    , rs.getInt("parentNum")
                    , rs.getInt("status"));
        }
    };

    @Override
    public void saveMessagesFast(Site site, List<Message> msgs) {
        saveMessagesFast(site, msgs, false);
    }

    @Override
    public void saveMessagesFast(Site site, final List<Message> msgs, boolean updateIfExists) {
        Assert.isTrue(!updateIfExists, "updating not implemented!");

        getJdbcTemplate().batchUpdate(queryProvider.getInsertMsgQuery(site), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Message msg = msgs.get(i);
                /*
                num,\
                parentNum,\
                host,\
                topicCode,\
                title,\
                nick,\
                altName,\
                msgDate,\
                reg,\
                body,\
                status)
                */
                int j = 1;
                ps.setInt(j++, msg.getNum());
                ps.setInt(j++, msg.getParentNum());
                ps.setString(j++, msg.getHost());
                ps.setInt(j++, msg.getTopicCode());
                ps.setString(j++, msg.getTitle());
                ps.setString(j++, msg.getNick());
                ps.setString(j++, msg.getAltName());
                ps.setTimestamp(j++, timestamp(msg.getDate()));
                ps.setBoolean(j++, msg.isReg());
                ps.setString(j++, msg.getBody());
                ps.setInt(j, msg.getStatus().getInt());
            }

            @Override
            public int getBatchSize() {
                return msgs.size();
            }
        });
    }

/*    public void saveMessages(List<Message> msgs) {
        saveMessages(msgs, false);
    }*/

    // todo: test

    @Override
    public Message getMessageByNumber(Site site, int num) {
        return getSimpleJdbcTemplate().queryForObject(
                queryProvider.getSelectMsgByIdQuery(site),
                messageRowMapper,
                num);
    }

    @Override
    public List<Message> getMessagesByRange(Site site, int start, int end) {
        return getSimpleJdbcTemplate().query(
                queryProvider.getSelectMsgsInRangeQuery(site),
                messageRowMapper,
                start, end);
    }

    @Override
    public List<Message> getMessages(Site site, int[] nums, int fromIndex) {
        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for (int i = 1; i < nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        String sql = String.format(queryProvider.getSelectSetQuery(site), sbNums.toString());

        return getSimpleJdbcTemplate().query(sql, messageRowMapper);
/*        DbResult res = DbUtils.executeSelect(getDataSource(), sql);

        List<Message> msgs = new ArrayList<Message>();

        while (res.next()) {
            Message msg = getMessage(res, site);
            msg.setHitId(fromIndex++);
            msg.setSite(site);
            msgs.add(msg);
        }

        res.close();
        return msgs;*/
    }

    @Override
    public int getLastMessageNumber(Site site) {
        return getSimpleJdbcTemplate().queryForInt(queryProvider.getSelectLastMsgNumQuery(site));
    }

    private HashMap<String, Integer> topicsHashMap;
    // returns <topic name, topic code> where "topic name"s also include old codes

    @Override
    public HashMap<String, Integer> getTopicsHashMap(Site site) {
        if (topicsHashMap == null) {
            List<Topic> topicList = getTopicList(site);
            topicsHashMap = new HashMap<String, Integer>();

            for (Topic topic : topicList) {
                topicsHashMap.put(topic.getName(), topic.getId());
            }
        }
        return topicsHashMap;
    }

    @Override
    public List<Topic> getTopicList(Site site) {
        return getSimpleJdbcTemplate().query(
                queryProvider.getSelectAllTopicsQuery(site),
                getRowMappersHelper().beanRowMapper(Topic.class));
    }

    // returns only "new" topics - current posible topics on site
    private String[] topics = null;

    @Override
    public String[] getTopics(Site site) {
        if (topics == null) {
            List<Topic> topicList = getTopicList(site);
            topics = new String[topicList.size()];
            for (Topic topic : topicList) {
                topics[topic.getId()] = topic.getName();
            }
        }
        return topics;
    }

    @Override
    public void saveSearchRequest(int siteNum, String host, String userAgent,
                                  String reqText, String reqNick, String reqHost,
                                  String reqQuery, String reqQueryString, String referer, boolean rssAsked) {
        getSimpleJdbcTemplate().update(SQL_LOG_REQUEST,
                siteNum,
                host,
                userAgent,

                reqText,
                reqNick,
                reqHost,

                reqQuery,
                reqQueryString,
                referer,
                rssAsked);
    }
}
