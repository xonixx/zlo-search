package info.xonix.zlo.web.decorators;

import org.displaytag.decorator.TableDecorator;
import org.apache.commons.lang.StringUtils;

import java.util.TreeMap;

/**
 * Author: Vovan
 * Date: 30.03.2008
 * Time: 16:56:20
 */
public class HistoryTableDecorator extends TableDecorator {

    public static final int MAX_LEN = 40;

    private String shortenString(String s, int len) {
        if (s.length() > len) {
            s = StringUtils.substring(s, 0, len-3) + "...";
        }

        return s;
    }

    private String shortenString(Object s) {
        if (s == null)
            return "";
        return shortenString(s.toString(), MAX_LEN);
    }


    public String getSearchText() {
        return shortenString(((TreeMap) getCurrentRowObject()).get("req_text"));
    }

    public String getSearchNick() {
        return shortenString(((TreeMap) getCurrentRowObject()).get("req_nick"));
    }

    public String getSearchHost() {
        return shortenString(((TreeMap) getCurrentRowObject()).get("req_host"));
    }

    public String getUserAgentSmall() {
        TreeMap row = (TreeMap) getCurrentRowObject();
        String userAgent = (String) row.get("user_agent");
        return userAgent.contains("MSIE")
            ? "Internet Explorer"
            : userAgent.contains("Firefox") || userAgent.contains("Minefield")
            ? "Firefox"
            : userAgent.contains("Safari")
            ? "Safari"
            : userAgent.contains("Mozilla")
            ? "Mozilla"
            : userAgent.contains("Opera")
            ? "Opera"
            : userAgent.contains("Konqueror")
            ? "Konqueror"
            : "other";
    }
}
