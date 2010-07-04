package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbAccessor;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 22:31:10
 */
@Deprecated
public class DB implements IndexingSource {
    private static Logger logger = Logger.getLogger("DB");

    private DbAccessor dbAccessor;

    public DB(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }


}
