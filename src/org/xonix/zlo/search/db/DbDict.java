package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import static org.xonix.zlo.search.db.VarType.*;

import java.util.Properties;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 17:05:56
 */
public class DbDict {
    private static final Logger logger = Logger.getLogger(DbDict.class);

    private static Properties props = Config.loadProperties("org/xonix/zlo/search/db/db_dict.sql.properties");

    private static final String SQL_SET_VAL = props.getProperty("sql.set.val");
    private static final String SQL_GET_VAL = props.getProperty("sql.get.val");
    private static final String SQL_REMOVE_VAL = props.getProperty("sql.remove.val");

    public static void setVal(String name, Object val, VarType type) throws DbException {
        // int, txt, bool, date
        Object[] vals = new Object[] {null, null, null, null};
        vals[getValIndex(type)] = val;

        DbUtils.executeUpdate(
                SQL_SET_VAL,
                new Object[]{name, type.getInt(), vals[0], vals[1], vals[2], vals[3],
                                    type.getInt(), vals[0], vals[1], vals[2], vals[3]},
                new VarType[]{STRING, INTEGER, INTEGER, STRING, BOOLEAN, DATE,
                                        INTEGER, INTEGER, STRING, BOOLEAN, DATE});
    }

    public static void setInt(String name, Integer val) throws DbException {
        setVal(name, val, INTEGER);
    }

    public static void setStr(String name, String val) throws DbException {
        setVal(name, val, STRING);
    }

    public static void setBool(String name, Boolean val) throws DbException {
        setVal(name, val, BOOLEAN);
    }

    public static void setDate(String name, Date val) throws DbException {
        setVal(name, val, DATE);
    }

    public static Object getVal(String name) throws DbException {
        DbUtils.Result res = DbUtils.executeSelect(SQL_GET_VAL, new Object[]{name}, new VarType[]{STRING});
        try {
            ResultSet rs = res.getResultSet();
            if (rs.next()) {
                int type = rs.getInt(1);
                return rs.getObject(2 + type);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            res.close();
        }
    }

    public static Integer getInt(String name) throws DbException {
        return (Integer) getVal(name);
    }

    public static String getStr(String name) throws DbException {
        return (String) getVal(name);
    }

    public static Boolean getBool(String name) throws DbException {
        return (Boolean) getVal(name);
    }
    
    public static Date getDate(String name) throws DbException {
        return (Date) getVal(name);
    }

    public static void remove(String name) throws DbException {
        DbUtils.executeUpdate(SQL_REMOVE_VAL, new Object[]{name}, new VarType[]{STRING});    
    }

    private static int getValIndex(VarType type) {
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
