package info.xonix.zlo.search.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * TODO: dateformats are not thread-safe!!!
 * User: Vovan
 * Date: 09.07.2010
 * Time: 22:10:32
 */
public class DateFormats {
    public static final DateFormat ddMMyyyyy_HHmm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static final DateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
    public static final DateFormat ddMMyyyy_dots = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat DF_BOARD_MSG = new SimpleDateFormat("M d HH:mm:ss yyyy");
}
