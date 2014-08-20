package info.xonix.zlo.search.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: Vovan
 * Date: 09.07.2010
 * Time: 22:10:32
 */
public class DateFormats {
    public static final ThreadLocal<DateFormat> ddMMyyyyy_HHmm = df("dd/MM/yyyy HH:mm");
    public static final ThreadLocal<DateFormat> ddMMyyyy = df("dd/MM/yyyy");
    public static final ThreadLocal<DateFormat> ddMMyyyy_dots = df("dd.MM.yyyy");
    public static final ThreadLocal<DateFormat> DF_BOARD_MSG = df("M d HH:mm:ss yyyy");

    public static final ThreadLocal<DateFormat> Hour = df("HH");
    public static final ThreadLocal<DateFormat> WeekDay = df("E");

    private static ThreadLocal<DateFormat> df(final String format) {
        return new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(format, Locale.US);
            }
        };
    }
}
