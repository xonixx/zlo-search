package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domainobj.Site;

import java.util.Date;

/**
 * User: Vovan
 * Date: 20.06.2010
 * Time: 23:50:40
 */
public interface DbDict {
    void setInt(Site site, String name, Integer val);

    void setStr(Site site, String name, String val);

    void setBool(Site site, String name, Boolean val);

    void setDate(Site site, String name, Date val);

    Integer getInt(Site site, String name);

    int getInt(Site site, String name, int defaultVal);

    String getStr(Site site, String name);

    String getStr(Site site, String name, String defaultVal);

    Boolean getBool(Site site, String name);

    boolean getBool(Site site, String name, boolean defaultVal);

    Date getDate(Site site, String name);

    Date getDate(Site site, String name, Date defaultVal);

    void remove(Site site, String name);
}
