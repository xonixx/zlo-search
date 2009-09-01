package info.xonix.zlo.search.db;

import info.xonix.zlo.search.config.Config;
import static info.xonix.zlo.search.db.VarType.*;

import java.util.Date;
import java.util.Properties;
import java.text.MessageFormat;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 17:05:56
 */
public class DbDict {
//    private static final Logger logger = Logger.getLogger(DbDict.class);

    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/db_dict.sql.properties");

    private final String SQL_SET_VAL;// = props.getProperty("sql.set.val");
    private final String SQL_GET_VAL;// = props.getProperty("sql.get.val");
    private final String SQL_REMOVE_VAL;// = props.getProperty("sql.remove.val");

    private DbAccessor dbAccessor;

    public DbDict(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;

        String name = dbAccessor.getName();

        SQL_SET_VAL = MessageFormat.format(props.getProperty("sql.set.val"), name);
        SQL_GET_VAL = MessageFormat.format(props.getProperty("sql.get.val"), name);
        SQL_REMOVE_VAL = MessageFormat.format(props.getProperty("sql.remove.val"), name);
    }

    public void setVal(String name, Object val, VarType type) throws DbException {
        // int, txt, bool, date
        Object[] vals = new Object[] {null, null, null, null};
        vals[getValIndex(type)] = val;

        DbUtils.executeUpdate(
                dbAccessor,
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
        DbResult res = DbUtils.executeSelect(dbAccessor, SQL_GET_VAL, new Object[]{name}, new VarType[]{STRING});
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

    public int getInt(String name, int defaultVal) throws DbException {
        Integer i = getInt(name);
        return i == null ? defaultVal : i;
    }

    public String getStr(String name) throws DbException {
        return (String) getVal(name);
    }

    public String getStr(String name, String defaultVal) throws DbException {
        String s = getStr(name);
        return s == null ? defaultVal : s;
    }

    public Boolean getBool(String name) throws DbException {
        return (Boolean) getVal(name);
    }

    public boolean getBool(String name, boolean defaultVal) throws DbException {
        Boolean b = getBool(name);
        return b == null ? defaultVal : b;
    }
    
    public Date getDate(String name) throws DbException {
        return (Date) getVal(name);
    }

    public Date getDate(String name, Date defaultVal) throws DbException {
        Date d = getDate(name);
        return d == null ? defaultVal : d;
    }

    public void remove(String name) throws DbException {
        DbUtils.executeUpdate(dbAccessor, SQL_REMOVE_VAL, new Object[]{name}, new VarType[]{STRING});
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
