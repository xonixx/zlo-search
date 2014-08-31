package info.xonix.zlo.search.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: xonix
 * Date: 9/1/14
 * Time: 1:32 AM
 */
public class DateUtil {
    public static Date emptyTimePart(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int dateDiff(Date bigger, Date smaller) {
        return (int) ((bigger.getTime() - smaller.getTime()) / (24 * 3600 * 1000));
    }
}
