package info.xonix.zlo.web.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public static void forgetCookie(HttpServletResponse response, String fieldName) {
        rememberInCookie(response, fieldName, "", 0);
    }

    /**
     * @param request   http request
     * @param fieldname cookie name
     * @return null if no cookie with this name present
     */
    public static String recallFromCookie(HttpServletRequest request, String fieldname) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        for (Cookie cookie : cookies) {
            if (fieldname.equals(cookie.getName()))
                return cookie.getValue();
        }

        return null;
    }

    public static boolean isCookiePresent(HttpServletRequest request, String fieldName) {
        return recallFromCookie(request, fieldName) != null;
    }
}
