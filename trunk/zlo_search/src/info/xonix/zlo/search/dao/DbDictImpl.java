package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.db.DbResult;
import info.xonix.zlo.search.db.DbUtils;
import info.xonix.zlo.search.db.VarType;
import info.xonix.zlo.search.model.Site;

import static info.xonix.zlo.search.db.VarType.*;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 17:05:56
 */
public class DbDictImpl extends DaoImplBase implements DbDict {
//    private static final Logger logger = Logger.getLogger(DbDictImpl.class);

//    private static Properties props = Config.loadProperties("info/xonix/zlo/search/db/db_dict.sql.properties");

//    private final String SQL_SET_VAL;// = props.getProperty("sql.set.val");
//    private final String SQL_GET_VAL;// = props.getProperty("sql.get.val");
//    private final String SQL_REMOVE_VAL;// = props.getProperty("sql.remove.val");

//    private DbAccessor dbAccessor;

/*    public DbDictImpl(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;

        String name = dbAccessor.getName();

        SQL_SET_VAL = MessageFormat.format(props.getProperty("sql.set.val"), name);
        SQL_GET_VAL = MessageFormat.format(props.getProperty("sql.get.val"), name);
        SQL_REMOVE_VAL = MessageFormat.format(props.getProperty("sql.remove.val"), name);
    }*/

    private QueryProvider queryProvider;

    // todo inject
    public void setQueryProvider(QueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    private void setVal(Site site, String name, Object val, VarType type) throws DbException {
        // int, txt, bool, date
        Object[] vals = new Object[] {null, null, null, null};
        vals[getValIndex(type)] = val;

        DbUtils.executeUpdate(
                getDataSource(),
                queryProvider.getDbDictSetValQuery(site),
                new Object[]{name, type.getInt(), vals[0], vals[1], vals[2], vals[3],
                                    type.getInt(), vals[0], vals[1], vals[2], vals[3]},
                new VarType[]{STRING, INTEGER, INTEGER, STRING, BOOLEAN, DATE,
                                        INTEGER, INTEGER, STRING, BOOLEAN, DATE});
    }

    @Override
    public void setInt(Site site, String name, Integer val) throws DbException {
        setVal(site, name, val, INTEGER);
    }

    @Override
    public void setStr(Site site, String name, String val) throws DbException {
        setVal(site, name, val, STRING);
    }

    @Override
    public void setBool(Site site, String name, Boolean val) throws DbException {
        setVal(site, name, val, BOOLEAN);
    }

    @Override
    public void setDate(Site site, String name, Date val) throws DbException {
        setVal(site, name, val, DATE);
    }

    private Object getVal(Site site, String name) throws DbException {
        DbResult res = DbUtils.executeSelect(getDataSource(),
                queryProvider.getDbDictGetValQuery(site),
                new Object[]{name}, new VarType[]{STRING});
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

    @Override
    public Integer getInt(Site site, String name) throws DbException {
        return (Integer) getVal(site, name);
    }

    @Override
    public int getInt(Site site, String name, int defaultVal) throws DbException {
        Integer i = getInt(site, name);
        return i == null ? defaultVal : i;
    }

    @Override
    public String getStr(Site site, String name) throws DbException {
        return (String) getVal(site, name);
    }

    @Override
    public String getStr(Site site, String name, String defaultVal) throws DbException {
        String s = getStr(site, name);
        return s == null ? defaultVal : s;
    }

    @Override
    public Boolean getBool(Site site, String name) throws DbException {
        return (Boolean) getVal(site, name);
    }

    @Override
    public boolean getBool(Site site, String name, boolean defaultVal) throws DbException {
        Boolean b = getBool(site, name);
        return b == null ? defaultVal : b;
    }
    
    @Override
    public Date getDate(Site site, String name) throws DbException {
        return (Date) getVal(site, name);
    }

    @Override
    public Date getDate(Site site, String name, Date defaultVal) throws DbException {
        Date d = getDate(site, name);
        return d == null ? defaultVal : d;
    }

    @Override
    public void remove(Site site, String name) throws DbException {
        DbUtils.executeUpdate(getDataSource(),
                queryProvider.getDbDictRemoveValQuery(site),
                new Object[]{name}, new VarType[]{STRING});
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
