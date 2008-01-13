package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import static org.xonix.zlo.search.db.VarType.*;
import org.xonix.zlo.search.dao.Site;

import java.util.Date;
import java.util.Properties;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 17:05:56
 */
public class DbDict extends DbManagerSource {
    private static final Logger logger = Logger.getLogger(DbDict.class);

    private static Properties props = Config.loadProperties("org/xonix/zlo/search/db/db_dict.sql.properties");

    private static final String SQL_SET_VAL = props.getProperty("sql.set.val");
    private static final String SQL_GET_VAL = props.getProperty("sql.get.val");
    private static final String SQL_REMOVE_VAL = props.getProperty("sql.remove.val");

    public DbDict(Site site) {
        super(site);
    }

    public void setVal(String name, Object val, VarType type) throws DbException {
        // int, txt, bool, date
        Object[] vals = new Object[] {null, null, null, null};
        vals[getValIndex(type)] = val;

        DbUtils.executeUpdate(
                getSite(),
                SQL_SET_VAL,
                new Object[]{name, type.getInt(), vals[0], vals[1], vals[2], vals[3],
                                    type.getInt(), vals[0], vals[1], vals[2], vals[3]},
                new VarType[]{STRING, INTEGER, INTEGER, STRING, BOOLEAN, DATE,
                                        INTEGER, INTEGER, STRING, BOOLEAN, DATE});
    }

    public void setInt(String name, Integer val) throws DbException {
        setVal(name, val, INTEGER);
    }

    public void setStr(String name, String val) throws DbException {
        setVal(name, val, STRING);
    }

    public void setBool(String name, Boolean val) throws DbException {
        setVal(name, val, BOOLEAN);
    }

    public void setDate(String name, Date val) throws DbException {
        setVal(name, val, DATE);
    }

    public Object getVal(String name) throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(getSite(), SQL_GET_VAL, new Object[]{name}, new VarType[]{STRING});
        try {
            if (res.next()) {
                int type = res.getInt(1);
                return res.getObject(2 + type);
            } else {
                return null;
            }
        } finally {
            res.close();
        }
    }

    public Integer getInt(String name) throws DbException {
        return (Integer) getVal(name);
    }

    public String getStr(String name) throws DbException {
        return (String) getVal(name);
    }

    public Boolean getBool(String name) throws DbException {
        return (Boolean) getVal(name);
    }
    
    public Date getDate(String name) throws DbException {
        return (Date) getVal(name);
    }

    public void remove(String name) throws DbException {
        DbUtils.executeUpdate(getSite(), SQL_REMOVE_VAL, new Object[]{name}, new VarType[]{STRING});    
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
