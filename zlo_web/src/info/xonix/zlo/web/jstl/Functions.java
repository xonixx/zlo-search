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

    public static String plural(int n, String word1, String word2, String wordMany) {
        int n20 = n % 100;
        if (n20 >= 10 && n20 < 20)
            return wordMany;
        else {
            int n10 = n20 % 10;
            switch (n10) {
                case 1:
                    return word1;
                case 2:
                case 3:
                case 4:
                    return word2;
                default:
                    return wordMany;
            }
        }
    }
}
