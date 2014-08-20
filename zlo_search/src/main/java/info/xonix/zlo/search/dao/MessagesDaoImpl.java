package info.xonix.zlo.search.dao;


import info.xonix.utils.Check;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageShallow;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.model.Topic;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static info.xonix.utils.DbUtils.timestamp;
import static org.apache.commons.lang.StringUtils.substring;

/**
 * User: boost
 * Date: Sep 13, 2007
 * Time: 11:33:31 PM
 */
public class MessagesDaoImpl extends DaoImplBase implements MessagesDao {
    private static final Logger log = Logger.getLogger(MessagesDaoImpl.class);

    @Autowired
    private QueryProvider queryProvider;

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        Check.isSet(queryProvider, "queryProvider");
    }

    private RowMapper<Message> messageRowMapper = new RowMapper<Message>() {
        @Override
        public Message mapRow(ResultSet rs, int i) throws SQLException {
            return new Message(
                    rs.getString("nick")
                    , rs.getString("user_id")
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

    private RowMapper<MessageShallow> messageShallowRowMapper = new RowMapper<MessageShallow>() {
        @Override
        public MessageShallow mapRow(ResultSet rs, int i) throws SQLException {
            return new MessageShallow(
                    rs.getInt("num")
                    , rs.getString("nick")
                    , rs.getString("host")
                    , rs.getBoolean("reg")
                    , rs.getString("topic")
                    , rs.getString("title")
                    , rs.getTimestamp("msgDate"));
        }
    };

    @Override
    public void saveMessagesFast(String forumId, List<Message> msgs) {
        saveMessagesFast(forumId, msgs, false);
    }

    @Override
    public void saveMessagesFast(final String forumId, final List<Message> msgs, boolean updateIfExists) {
        Assert.isTrue(!updateIfExists, "updating not implemented!");

        final SqlParameterSource[] batch = new SqlParameterSource[msgs.size()];

        for (int i = 0; i < msgs.size(); i++) {
            Message msg = msgs.get(i);

            validateMsgBeforeSave(forumId, msg);

            batch[i] = new MapSqlParameterSource()
                    .addValue("num", msg.getNum())
                    .addValue("user_id", msg.getUserId())
                    .addValue("parentNum", msg.getParentNum())
                    .addValue("host", msg.getHost())
                    .addValue("topicCode", msg.getTopicCode())
                    .addValue("title", msg.getTitle())
                    .addValue("nick", msg.getNick())
                    .addValue("altName", msg.getAltName())
                    .addValue("msgDate", timestamp(msg.getDate()))
                    .addValue("reg", msg.isReg())
                    .addValue("body", msg.getBody())
                    .addValue("status", msg.getStatus().getInt());
        }

        newSimpleJdbcInsert()
                .withTableName(forumTable(forumId, "messages"))
                .executeBatch(batch);

/*
        getJdbcTemplate().batchUpdate(queryProvider.getInsertMsgQuery(forumId), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Message msg = msgs.get(i);

                validateMsgBeforeSave(forumId, msg);
                */
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
                *//*

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
*/
    }

    private void validateMsgBeforeSave(String forumId, Message msg) {
        final MessageStatus status = msg.getStatus();
        if (status != MessageStatus.OK &&
                status != MessageStatus.DELETED) {
            throw new IllegalArgumentException(forumId + ": wrong msg status: " + status);
        }

        if (msg.getNum() == -1) {
            throw new IllegalStateException(forumId +
                    ": num not set for msg:\n " + msg);
        } /*else if (msg.getStatus() != MessageStatus.DELETED) {
            if (msg.getDate() == null) {
                throw new IllegalStateException("site=" + forumId +
                        " date not set:\n " + msg);
            }
        }*/
    }

    @Override
    public Message getMessageByNumber(String forumId, int num) {
        try {
            return getSimpleJdbcTemplate().queryForObject(
                    queryProvider.getSelectMsgByIdQuery(forumId),
                    messageRowMapper,
                    num);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Message> getMessagesByRange(String forumId, int start, int end) {
        return getSimpleJdbcTemplate().query(
                queryProvider.getSelectMsgsInRangeQuery(forumId),
                messageRowMapper,
                start, end);
    }

    @Override
    public List<Message> getMessages(String forumId, int[] nums) {
        String sql = String.format(queryProvider.getSelectSetQuery(forumId), joinByComma(nums));

        return getSimpleJdbcTemplate().query(sql, messageRowMapper);
    }

    @Override
    public List<Message> getChildMessages(String forumId, long messageId) {
        return getSimpleJdbcTemplate().query(
                "select m.num, m.parentNum, m.nick, m.altName, m.user_id, m.title, m.host, " +
                        "m.msgDate, m.body, m.reg, m.status, " +
                        " t.name as topic, t.id as topicCode" +
                        " from " + forumTable(forumId, "messages") + " m" +
                        " join " + forumTable(forumId, "topics") + " t on m.topicCode=t.id" +
                        " where parentNum=? " + // TODO : add key by parentNum
                        " order by num desc",
                messageRowMapper, messageId);
    }

    @Override
    public List<MessageShallow> getShallowMessages(String forumId, int[] nums) {
        String sql = String.format(queryProvider.getSelectShallowSetQuery(forumId), joinByComma(nums));

        return getSimpleJdbcTemplate().query(sql, messageShallowRowMapper);
    }

    private String joinByComma(int[] nums) {
        if (nums == null || nums.length == 0) {
            return "-1";
        }

        StringBuilder sbNums = new StringBuilder(Integer.toString(nums[0]));

        for (int i = 1; i < nums.length; i++) {
            sbNums.append(",").append(nums[i]);
        }

        return sbNums.toString();
    }

    @Override
    public int getLastMessageNumber(String forumId) {
        return getSimpleJdbcTemplate().queryForInt(queryProvider.getSelectLastMsgNumQuery(forumId));
    }

    @Override
    public void saveSearchTextForAutocomplete(String forumId, String text) {
        final int res = getSimpleJdbcTemplate().update(
                queryProvider.getInsertUpdateAutocompleteQuery(forumId),
                substring(text, 0, 255));

/*        // TODO: this is broken for now, see http://bugs.mysql.com/bug.php?id=46675
        // see http://stackoverflow.com/questions/3747314/why-are-2-rows-affected-in-my-insert-on-duplicate-key-update
        if (res != 1 && res != 2) {
            log.error("saveSearchTextForAutocomplete: res=" + res);
        }*/
    }

    @Override
    public List<String> autoCompleteText(String forumId, String text, int limit) {
        return getJdbcTemplate().queryForList(
                queryProvider.getSelectAutocompleteQuery(forumId),
                String.class,
                text + '%',
                limit
        );
    }

    @Override
    public int insertTopic(String forumId, String topic) {
        final HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("name", substring(topic, 0, 50));
        args.put("isNew", true);

        // id must be auto_incremented!
        return newSimpleJdbcInsert()
                .withTableName(forumTable(forumId, "topics"))
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(args)
                .intValue();
    }

    @Override
    public List<Date> getMessageDates(String forumId, List<String> nicks, Date start, Date end) {
        String query = queryProvider.getSelectDatesQuery(forumId);

        List<Object> args = new LinkedList<Object>();
        args.add(start);
        args.add(end);

        List<String> qq = new LinkedList<String>();

        for (String nick : nicks) {
            qq.add("?");
            args.add(nick);
        }

        query = String.format(query, StringUtils.join(qq,','));

        return getJdbcTemplate().queryForList(
                query,
                Date.class,
                args.toArray());
    }

    @Override
    public List<Topic> getTopicList(String forumId) {
        return getSimpleJdbcTemplate().query(
                queryProvider.getSelectTopicsQuery(forumId),
                getRowMappersHelper().beanRowMapper(Topic.class));
    }
}
