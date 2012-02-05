package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domainobj.Site;
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

    private void setVal(Site site, String name, Object val, VarType type) {
        // int, txt, bool, date
        Object[] vals = new Object[]{null, null, null, null};
        vals[getValIndex(type)] = val;

        getSimpleJdbcTemplate().update(queryProvider.getDbDictSetValQuery(site),
                name, type.getInt(), vals[0], vals[1], vals[2], vals[3],
                type.getInt(), vals[0], vals[1], vals[2], vals[3]);
    }

    @Override
    public void setInt(Site site, String name, Integer val) {
        setVal(site, name, val, INTEGER);
    }

    @Override
    public void setStr(Site site, String name, String val) {
        setVal(site, name, val, STRING);
    }

    @Override
    public void setBool(Site site, String name, Boolean val) {
        setVal(site, name, val, BOOLEAN);
    }

    @Override
    public void setDate(Site site, String name, Date val) {
        setVal(site, name, val, DATE);
    }

    private Object getVal(Site site, String name, VarType varType) {
        try {
            return getSimpleJdbcTemplate().queryForObject(queryProvider.getDbDictGetValQuery(site),
                    varType.getJavaType(),
                    name);
        } catch (EmptyResultDataAccessException ignore) {
            return null;
        }
    }

    @Override
    public Integer getInt(Site site, String name) {
        return (Integer) getVal(site, name, INTEGER);
    }

    @Override
    public int getInt(Site site, String name, int defaultVal) {
        Integer i = getInt(site, name);
        return i == null ? defaultVal : i;
    }

    @Override
    public String getStr(Site site, String name) {
        return (String) getVal(site, name, STRING);
    }

    @Override
    public String getStr(Site site, String name, String defaultVal) {
        String s = getStr(site, name);
        return s == null ? defaultVal : s;
    }

    @Override
    public Boolean getBool(Site site, String name) {
        return (Boolean) getVal(site, name, BOOLEAN);
    }

    @Override
    public boolean getBool(Site site, String name, boolean defaultVal) {
        Boolean b = getBool(site, name);
        return b == null ? defaultVal : b;
    }

    @Override
    public Date getDate(Site site, String name) {
        return (Date) getVal(site, name, DATE);
    }

    @Override
    public Date getDate(Site site, String name, Date defaultVal) {
        Date d = getDate(site, name);
        return d == null ? defaultVal : d;
    }

    @Override
    public void remove(Site site, String name) {
        getSimpleJdbcTemplate().update(queryProvider.getDbDictRemoveValQuery(site), name);
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
