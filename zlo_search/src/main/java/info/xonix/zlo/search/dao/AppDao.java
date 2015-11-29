package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.NickHost;

import java.util.List;

/**
 * User: xonix
 * Date: 30.11.15
 * Time: 0:05
 */
public interface AppDao {
    List<NickHost> getNicks(String forumId, String host);

    List<NickHost> getHosts(String forumId, String nick);
}
