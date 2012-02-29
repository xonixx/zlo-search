package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.utils.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Date;

import static info.xonix.zlo.search.dao.VarType.*;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 17:05:56
 */
public class DbDictImpl extends DaoImplBase implements DbDict {
    @Autowired
    private QueryProvider queryProvider;

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        Check.isSet(queryProvider, "queryProvider");
    }

    private void setVal(String forumId, String name, Object val, VarType type) {
        // int, txt, bool, date
        Object[] vals = new Object[]{null, null, null, null};
        vals[getValIndex(type)] = val;

        getSimpleJdbcTemplate().update(queryProvider.getDbDictSetValQuery(forumId),
                name, type.getInt(), vals[0], vals[1], vals[2], vals[3],
                type.getInt(), vals[0], vals[1], vals[2], vals[3]);
    }

    @Override
    public void setInt(String forumId, String name, Integer val) {
        setVal(forumId, name, val, INTEGER);
    }

    @Override
    public void setStr(String forumId, String name, String val) {
        setVal(forumId, name, val, STRING);
    }

    @Override
    public void setBool(String forumId, String name, Boolean val) {
        setVal(forumId, name, val, BOOLEAN);
    }

    @Override
    public void setDate(String forumId, String name, Date val) {
        setVal(forumId, name, val, DATE);
    }

    private Object getVal(String forumId, String name, VarType varType) {
        try {
            return getSimpleJdbcTemplate().queryForObject(queryProvider.getDbDictGetValQuery(forumId),
                    varType.getJavaType(),
                    name);
        } catch (EmptyResultDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public Integer getInt(String forumId, String name) {
        return (Integer) getVal(forumId, name, INTEGER);
    }

    @Override
    public int getInt(String forumId, String name, int defaultVal) {
        Integer i = getInt(forumId, name);
        return i == null ? defaultVal : i;
    }

    @Override
    public String getStr(String forumId, String name) {
        return (String) getVal(forumId, name, STRING);
    }

    @Override
    public String getStr(String forumId, String name, String defaultVal) {
        String s = getStr(forumId, name);
        return s == null ? defaultVal : s;
    }

    @Override
    public Boolean getBool(String forumId, String name) {
        return (Boolean) getVal(forumId, name, BOOLEAN);
    }

    @Override
    public boolean getBool(String forumId, String name, boolean defaultVal) {
        Boolean b = getBool(forumId, name);
        return b == null ? defaultVal : b;
    }

    @Override
    public Date getDate(String forumId, String name) {
        return (Date) getVal(forumId, name, DATE);
    }

    @Override
    public Date getDate(String forumId, String name, Date defaultVal) {
        Date d = getDate(forumId, name);
        return d == null ? defaultVal : d;
    }

    @Override
    public void remove(String forumId, String name) {
        getSimpleJdbcTemplate().update(queryProvider.getDbDictRemoveValQuery(forumId), name);
    }

    private int getValIndex(VarType type) {
        switch (type) {
            case INTEGER:
                return 0;

            case STRING:
                return 1;

            case BOOLEAN:
                return 2;

            case DATE:
                return 3;

            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported parameter type: %s", type));
        }
    }

}
