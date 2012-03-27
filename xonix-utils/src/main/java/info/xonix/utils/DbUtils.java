package info.xonix.utils;

import java.sql.Timestamp;

/**
 * Author: Vovan
 * Date: 18.11.2007
 * Time: 0:23:01
 */
public final class DbUtils {
//    private static final Logger logger = Logger.getLogger(DbUtils.class);

    public static Timestamp timestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
