package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;
import static org.xonix.zlo.search.db.VarType.*;

import java.util.Properties;

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

    public static void setVal(String name, Object val, VarType type) throws DbException {
        // int, txt, bool, date
        Object[] vals = new Object[] {null, null, null, null};
        vals[getValIndex(type)] = val;

        DbUtils.executeUpdate(
                SQL_SET_VAL,
                new Object[]{name, type.getSqlType(), vals[0], vals[1], vals[2], vals[3],
                                    type.getSqlType(), vals[0], vals[1], vals[2], vals[3]},
                new VarType[]{STRING, INTEGER, INTEGER, STRING, BOOLEAN, DATE,
                                        INTEGER, INTEGER, STRING, BOOLEAN, DATE});
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
