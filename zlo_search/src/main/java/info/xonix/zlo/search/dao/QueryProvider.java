package info.xonix.zlo.search.dao;

import info.xonix.utils.ConfigUtils;
import info.xonix.utils.factory.StringFactory;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:15:18
 */
public class QueryProvider {

    private static final class ForumQueries {
//        private String INSERT_MSG;

        //        private String INSERT_UPDATE_MSG;
//        private String UPDATE_MSG;
//        private String DELETE_MSG;
        private String SELECT_MSG_BY_ID;
        private String SELECT_MSGS_IN_RANGE;
        private String SELECT_LAST_MSG_NUM;
        private String SELECT_MSGS_SET;
        private String SELECT_MSGS_SET_SHALLOW;

        //        private String SELECT_ALL_TOPICS;
        private String SELECT_NEW_TOPICS;

        private String DICT_SQL_SET_VAL;
        private String DICT_SQL_GET_VAL;
        private String DICT_SQL_REMOVE_VAL;

        private String INSERT_UPDATE_AUTOCOMPLETE;
        private String SELECT_AUTOCOMPLETE;
        private String SELECT_DATES;
    }

    private StringFactory<ForumQueries> siteQueriesSiteFactory = new StringFactory<ForumQueries>() {
        @Override
        protected ForumQueries create(String forumId) {
            ForumQueries sq = new ForumQueries();

            fillForumQueries(sq, forumId);

            return sq;
        }

        private void fillForumQueries(ForumQueries sq, String name) {
//            sq.INSERT_MSG = MessageFormat.format(props.getProperty("sql.insert.msg"), name);

//            sq.INSERT_UPDATE_MSG = MessageFormat.format(props.getProperty("sql.insert.update.msg"), name);
//
//            sq.UPDATE_MSG = MessageFormat.format(props.getProperty("sql.update.msg"), name);
//            sq.DELETE_MSG = MessageFormat.format(props.getProperty("sql.delete.msg"), name);

            sq.SELECT_MSG_BY_ID = MessageFormat.format(props.getProperty("sql.select.msg.by.id"), name);
            sq.SELECT_MSGS_IN_RANGE = MessageFormat.format(props.getProperty("sql.select.msg.in.range"), name);
            sq.SELECT_LAST_MSG_NUM = MessageFormat.format(props.getProperty("sql.select.last.msg.num"), name);
            sq.SELECT_MSGS_SET = MessageFormat.format(props.getProperty("sql.select.set"), name);
            sq.SELECT_MSGS_SET_SHALLOW = MessageFormat.format(props.getProperty("sql.select.set.shallow"), name);

//            sq.SELECT_ALL_TOPICS = MessageFormat.format(props.getProperty("sql.select.all.topics"), name);
            sq.SELECT_NEW_TOPICS = MessageFormat.format(props.getProperty("sql.select.new.topics"), name);

            sq.DICT_SQL_SET_VAL = MessageFormat.format(dbDictProps.getProperty("sql.set.val"), name);
            sq.DICT_SQL_GET_VAL = MessageFormat.format(dbDictProps.getProperty("sql.get.val"), name);
            sq.DICT_SQL_REMOVE_VAL = MessageFormat.format(dbDictProps.getProperty("sql.remove.val"), name);

            sq.INSERT_UPDATE_AUTOCOMPLETE = MessageFormat.format(props.getProperty("sql.insert_or_update.autocomplete"), name);
            sq.SELECT_AUTOCOMPLETE = MessageFormat.format(props.getProperty("sql.select.autocomplete"), name);
            sq.SELECT_DATES = MessageFormat.format(props.getProperty("sql.select.msg_dates"), name);
        }
    };

    private Properties props;
    private Properties dbDictProps;


    public QueryProvider() {
        props = ConfigUtils.loadProperties("info/xonix/zlo/search/db/sql.properties");
        dbDictProps = ConfigUtils.loadProperties("info/xonix/zlo/search/db/db_dict.sql.properties");
    }

    private ForumQueries getForumQueries(String forumId) {
        return siteQueriesSiteFactory.get(forumId);
    }

    // queries

    /*public String getInsertMsgQuery(String forumId) {
        return getForumQueries(forumId).INSERT_MSG;
    }*/

/*    public String getInsertUpdateMsgQuery(String forumId) {
        return getForumQueries(forumId).INSERT_UPDATE_MSG;
    }

    public String getUpdateMsgQuery(String forumId) {
        return getForumQueries(forumId).UPDATE_MSG;
    }

    public String getDeleteMsgQuery(String forumId) {
        return getForumQueries(forumId).DELETE_MSG;
    }*/

    public String getSelectMsgByIdQuery(String forumId) {
        return getForumQueries(forumId).SELECT_MSG_BY_ID;
    }

    public String getSelectMsgsInRangeQuery(String forumId) {
        return getForumQueries(forumId).SELECT_MSGS_IN_RANGE;
    }

    public String getSelectLastMsgNumQuery(String forumId) {
        return getForumQueries(forumId).SELECT_LAST_MSG_NUM;
    }

    public String getSelectSetQuery(String forumId) {
        return getForumQueries(forumId).SELECT_MSGS_SET;
    }

    public String getSelectShallowSetQuery(String forumId) {
        return getForumQueries(forumId).SELECT_MSGS_SET_SHALLOW;
    }

/*    public String getSelectTopicsIncludingObsoleteQuery(String forumId) {
        return getForumQueries(forumId).SELECT_ALL_TOPICS;
    }*/

    public String getSelectTopicsQuery(String forumId) {
        return getForumQueries(forumId).SELECT_NEW_TOPICS;
    }

    // db dict

    public String getDbDictSetValQuery(String forumId) {
        return getForumQueries(forumId).DICT_SQL_SET_VAL;
    }

    public String getDbDictGetValQuery(String forumId) {
        return getForumQueries(forumId).DICT_SQL_GET_VAL;
    }

    public String getDbDictRemoveValQuery(String forumId) {
        return getForumQueries(forumId).DICT_SQL_REMOVE_VAL;
    }

    public String getInsertUpdateAutocompleteQuery(String forumId) {
        return getForumQueries(forumId).INSERT_UPDATE_AUTOCOMPLETE;
    }

    public String getSelectAutocompleteQuery(String forumId) {
        return getForumQueries(forumId).SELECT_AUTOCOMPLETE;
    }

    public String getSelectDatesQuery(String forumId) {
        return getForumQueries(forumId).SELECT_DATES;
    }
}
