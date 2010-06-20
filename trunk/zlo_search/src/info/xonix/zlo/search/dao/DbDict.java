package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.model.Site;

import java.util.Date;

/**
 * User: Vovan
 * Date: 20.06.2010
 * Time: 23:50:40
 */
public interface DbDict {
    void setInt(Site site, String name, Integer val) throws DbException;

    void setStr(Site site, String name, String val) throws DbException;

    void setBool(Site site, String name, Boolean val) throws DbException;

    void setDate(Site site, String name, Date val) throws DbException;

    Integer getInt(Site site, String name) throws DbException;

    int getInt(Site site, String name, int defaultVal) throws DbException;

    String getStr(Site site, String name) throws DbException;

    String getStr(Site site, String name, String defaultVal) throws DbException;

    Boolean getBool(Site site, String name) throws DbException;

    boolean getBool(Site site, String name, boolean defaultVal) throws DbException;

    Date getDate(Site site, String name) throws DbException;

    Date getDate(Site site, String name, Date defaultVal) throws DbException;

    void remove(Site site, String name) throws DbException;
}
