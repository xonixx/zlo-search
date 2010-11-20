package info.xonix.zlo.search.dao;

import java.sql.Types;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 05.12.2007
 * Time: 18:46:54
 */
enum VarType {
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
                throw new IllegalArgumentException("Unknown vartype:" + this);
        }
    }

    public Class getJavaType() {
        switch (this) {
            case INTEGER:
                return Integer.class;
            case STRING:
                return String.class;
            case BOOLEAN:
                return Boolean.class;
            case DATE:
                return Date.class;
            default:
                throw new IllegalArgumentException("Unknown vartype:" + this);
        }
    }

    public int getInt() {
        return ordinal();
    }
}
