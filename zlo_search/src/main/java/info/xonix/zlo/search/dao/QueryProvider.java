package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.utils.factory.SiteFactory;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:15:18
 */
public class QueryProvider {

    private static final class SiteQueries {
        private String INSERT_MSG;
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
    }

    private SiteFactory<SiteQueries> siteQueriesSiteFactory = new SiteFactory<SiteQueries>() {
        @Override
        protected SiteQueries create(Site site) {
            String name = site.getName();

            SiteQueries sq = new SiteQueries();

            fillSiteQueries(sq, name);

            return sq;
        }

        private void fillSiteQueries(SiteQueries sq, String name) {
            sq.INSERT_MSG = MessageFormat.format(props.getProperty("sql.insert.msg"), name);
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
        }
    };

    private Properties props;
    private Properties dbDictProps;


    public QueryProvider() {
        props = Config.loadProperties("info/xonix/zlo/search/db/sql.properties");
        dbDictProps = Config.loadProperties("info/xonix/zlo/search/db/db_dict.sql.properties");
    }

    private SiteQueries getSiteQueries(Site site) {
        return siteQueriesSiteFactory.get(site);
    }

    // queries

    public String getInsertMsgQuery(Site site) {
        return getSiteQueries(site).INSERT_MSG;
    }

/*    public String getInsertUpdateMsgQuery(Site site) {
        return getSiteQueries(site).INSERT_UPDATE_MSG;
    }

    public String getUpdateMsgQuery(Site site) {
        return getSiteQueries(site).UPDATE_MSG;
    }

    public String getDeleteMsgQuery(Site site) {
        return getSiteQueries(site).DELETE_MSG;
    }*/

    public String getSelectMsgByIdQuery(Site site) {
        return getSiteQueries(site).SELECT_MSG_BY_ID;
    }

    public String getSelectMsgsInRangeQuery(Site site) {
        return getSiteQueries(site).SELECT_MSGS_IN_RANGE;
    }

    public String getSelectLastMsgNumQuery(Site site) {
        return getSiteQueries(site).SELECT_LAST_MSG_NUM;
    }

    public String getSelectSetQuery(Site site) {
        return getSiteQueries(site).SELECT_MSGS_SET;
    }

    public String getSelectShallowSetQuery(Site site) {
        return getSiteQueries(site).SELECT_MSGS_SET_SHALLOW;
    }

/*    public String getSelectTopicsIncludingObsoleteQuery(Site site) {
        return getSiteQueries(site).SELECT_ALL_TOPICS;
    }*/

    public String getSelectTopicsQuery(Site site) {
        return getSiteQueries(site).SELECT_NEW_TOPICS;
    }

    // db dict

    public String getDbDictSetValQuery(Site site) {
        return getSiteQueries(site).DICT_SQL_SET_VAL;
    }

    public String getDbDictGetValQuery(Site site) {
        return getSiteQueries(site).DICT_SQL_GET_VAL;
    }

    public String getDbDictRemoveValQuery(Site site) {
        return getSiteQueries(site).DICT_SQL_REMOVE_VAL;
    }

    public String getInsertUpdateAutocompleteQuery(Site site) {
        return getSiteQueries(site).INSERT_UPDATE_AUTOCOMPLETE;
    }

    public String getSelectAutocompleteQuery(Site site) {
        return getSiteQueries(site).SELECT_AUTOCOMPLETE;
    }
}
