package info.xonix.zlo.search.db;

import java.sql.Types;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 18:46:54
 */
public enum VarType {
    INTEGER,
    STRING,
    BOOLEAN,
    DATE;

    public int getSqlType() {
        switch (this) {
            case INTEGER:
                return Types.INTEGER;
            case STRING:
                return Types.VARCHAR;
            case BOOLEAN:
                return Types.BOOLEAN;
            case DATE:
                return Types.DATE;
            default:
                return -1;
        }
    }

    public int getInt() {
        return ordinal();
    }
}
