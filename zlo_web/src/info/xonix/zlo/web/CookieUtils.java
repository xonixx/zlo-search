package info.xonix.zlo.web;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Author: Vovan
 * Date: 25.10.2007
 * Time: 18:10:36
 */
public final class CookieUtils {
    public static void rememberInCookie(HttpServletResponse response, String fieldname, String value, int age) {
        Cookie cookie = new Cookie(fieldname, value);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public static void rememberInCookie(HttpServletResponse response, String fieldname, String value) {
        rememberInCookie(response, fieldname, value, Integer.MAX_VALUE); // forever
    }

    public static String recallFromCookie(HttpServletRequest request, String fieldname) {
        Cookie [] cookies = request.getCookies();

        if (cookies == null)
            return StringUtils.EMPTY;

        for (Cookie cookie: cookies) {
            if (fieldname.equals(cookie.getName()))
                return cookie.getValue();
        }
        return StringUtils.EMPTY;
    }
}
