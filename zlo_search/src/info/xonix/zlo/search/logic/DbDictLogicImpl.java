package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DbDict;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.model.Site;

import java.util.Date;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:23:00
 */
public class DbDictLogicImpl implements DbDictLogic {
    private static final String DB_DICT_LAST_INDEXED = "lastIndexed";
    private static final String DB_DICT_LAST_INDEXED_DATE = "lastIndexed_date";
    private static final String DB_DICT_LAST_INDEXED_DOUBLE = "lastIndexedDouble";
    private static final String DB_DICT_LAST_INDEXED_DOUBLE_DATE = "lastIndexedDouble_date";
    private static final String DB_DICT_LAST_SAVED_DATE = "lastSavedDate";

    private DbDict dbDict;

    // todo inject
    public void setDbDict(DbDict dbDict) {
        this.dbDict = dbDict;
    }

    public void setLastIndexedNumber(Site site, int num) throws DbException {
//        DbDictImpl dbDict = dbAccessor.getDbDict();
        if (Config.USE_DOUBLE_INDEX) {
            dbDict.setInt(site, DB_DICT_LAST_INDEXED_DOUBLE, num);
            dbDict.setDate(site, DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date());
        } else {
            dbDict.setInt(site, DB_DICT_LAST_INDEXED, num);
            dbDict.setDate(site, DB_DICT_LAST_INDEXED_DATE, new Date());
        }
    }

    public int getLastIndexedNumber(Site site) throws DbException {
        return dbDict.getInt(site, Config.USE_DOUBLE_INDEX ? DB_DICT_LAST_INDEXED_DOUBLE : DB_DICT_LAST_INDEXED, 0);
    }

    public Date getLastIndexedDate(Site site) throws DbException {
        return dbDict.getDate(site, Config.USE_DOUBLE_INDEX ? DB_DICT_LAST_INDEXED_DOUBLE_DATE : DB_DICT_LAST_INDEXED_DATE, new Date(0));
    }

    public void setLastSavedDate(Site site, Date d) throws DbException {
        dbDict.setDate(site, DB_DICT_LAST_SAVED_DATE, d);
    }

    public Date getLastSavedDate(Site site) throws DbException {
        return dbDict.getDate(site, DB_DICT_LAST_SAVED_DATE, new Date(0));
    }
}
