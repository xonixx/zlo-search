package info.xonix.zlo.search.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: Vovan
 * Date: 09.07.2010
 * Time: 22:10:32
 */
public class DateFormats {
    public static final ThreadLocal<DateFormat> ddMMyyyyy_HHmm = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm");
        }
    };
    public static final ThreadLocal<DateFormat> ddMMyyyy = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };
    public static final ThreadLocal<DateFormat> ddMMyyyy_dots = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy");
        }
    };
    public static final ThreadLocal<DateFormat> DF_BOARD_MSG = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("M d HH:mm:ss yyyy");
        }
    };
}
