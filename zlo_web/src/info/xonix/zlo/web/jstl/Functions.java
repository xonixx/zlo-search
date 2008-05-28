package info.xonix.zlo.web.jstl;

import info.xonix.zlo.search.utils.HtmlUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Author: Vovan
 * Date: 25.04.2008
 * Time: 4:07:44
 */
public class Functions {
    public static String urlencode(String s) {
        return HtmlUtils.urlencode(s);
    }

    public static String mysqlRange(String s) {
        return "('" + StringUtils.join(StringUtils.split(s, '|'), "','") + "')";
    }

    public static String highlight(String text, String wordsStr, String hlClass) {
        return ""; // todo: ?
    }

    public static void main(String[] args) {
        System.out.println(urlencode("<sb>"));
    }
}
