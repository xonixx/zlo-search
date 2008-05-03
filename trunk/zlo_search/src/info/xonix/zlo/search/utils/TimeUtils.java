package info.xonix.zlo.search.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: gubarkov
 * Date: 03.10.2007
 * Time: 19:36:21
 */
public class TimeUtils {
    private static Pattern PERIOD_RE = Pattern.compile("(\\d+m)*(\\d+s)*", Pattern.CASE_INSENSITIVE);

    public static int parseToMilliSeconds(String period) {
        Matcher m = PERIOD_RE.matcher(period);

        int m_i = 0;
        int s_i = 0;

        if (m.find()) {
            String m_s = m.group(1);
            String s_s = m.group(2);
            if (m_s != null) {
                m_i = Integer.parseInt(m_s.substring(0, m_s.length() - 1));
            }
            if (s_s != null) {
                s_i = Integer.parseInt(s_s.substring(0, s_s.length() - 1));
            }
        } else {
            throw new RuntimeException("period \"" + period + "\" doesn't have format {Nm}m{Ns}s");
        }

        return (60 * m_i + s_i) * 1000;
    }

    public static String toMinutesSeconds(long millis) {
        if (millis == 0)
            return "{0}";
        long allSeconds = millis / 1000;
        long seconds = allSeconds % 60;
        long minutes = allSeconds / 60;
        return "{" +
                (minutes == 0 ? "" : minutes + "min") +
                (minutes == 0 || seconds == 0 ? "" : " ") +
                (seconds == 0 ? "" : seconds + "sec") + "}";
    }
}
