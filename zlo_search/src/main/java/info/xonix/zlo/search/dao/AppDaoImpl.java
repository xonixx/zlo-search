package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.NickHost;

import java.util.List;

/**
 * User: xonix
 * Date: 30.11.15
 * Time: 0:06
 */
public class AppDaoImpl extends DaoImplBase implements AppDao {
    @Override
    public List<NickHost> getNicks(String forumId, String host) {
        return getJdbcTemplate().query(
                "select nick, host, cnt from " + forumId + "_nickhost " +
                        "where host=?",
                getRowMappersHelper().beanRowMapper(NickHost.class), host);
    }

    @Override
    public List<NickHost> getHosts(String forumId, String nick) {
        return getJdbcTemplate().query(
                "select nick, host, cnt from " + forumId + "_nickhost " +
                        "where nick=?",
                getRowMappersHelper().beanRowMapper(NickHost.class), nick);
    }
}
