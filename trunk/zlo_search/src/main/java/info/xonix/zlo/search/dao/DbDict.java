package info.xonix.zlo.search.dao;

import java.util.Date;

/**
 * User: Vovan
 * Date: 20.06.2010
 * Time: 23:50:40
 */
public interface DbDict {
    void setInt(String forumId, String name, Integer val);

    void setStr(String forumId, String name, String val);

    void setBool(String forumId, String name, Boolean val);

    void setDate(String forumId, String name, Date val);

    Integer getInt(String forumId, String name);

    int getInt(String forumId, String name, int defaultVal);

    String getStr(String forumId, String name);

    String getStr(String forumId, String name, String defaultVal);

    Boolean getBool(String forumId, String name);

    boolean getBool(String forumId, String name, boolean defaultVal);

    Date getDate(String forumId, String name);

    Date getDate(String forumId, String name, Date defaultVal);

    void remove(String forumId, String name);
}
