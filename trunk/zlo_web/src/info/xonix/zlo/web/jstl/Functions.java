package info.xonix.zlo.web.jstl;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Author: Vovan
 * Date: 25.04.2008
 * Time: 4:07:44
 */
public class Functions {
    public static String urlencode(String s) {
        try {
            return URLEncoder.encode(s, "windows-1251");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return StringEscapeUtils.escapeHtml(s);
        }
    }

    public static String highlight(String text, String wordsStr, String hlClass) {
        return ""; // todo: ?
    }

    public static void main(String[] args) {
        System.out.println(urlencode("<sb>"));
    }
}
