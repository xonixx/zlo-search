package info.xonix.zlo.web.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Vovan
 * Date: 01.05.2008
 * Time: 5:11:18
 */
public class RequestUtils {
    /**
     *  Weather the ip of client sending request is local ip
     * @param request
     * @param localIps
     * @return
     */
    public static String [][] BROWSERS = {
            {"MSIE", "Internet Explorer"},
            {"Firefox", "Firefox"},
            {"Opera", "Opera"},
            {"Minefield", "Firefox"},
            {"Chrome", "Chrome"},
            {"Safari", "Safari"},
            {"Feedfetcher-Google", "Google Feed Reader"},
            {"YandexBlog", "Yandex Feed Reader"},
            {"Yahoo Pipes", "Yahoo Feed Reader"},
            {"Yahoo! Slurp", "Yahoo! Slurp"},
            {"Googlebot", "Googlebot"},
            {"Konqueror", "Konqueror"},
            {"Mozilla", "Mozilla"},
    };

    public static boolean isLocalIp(HttpServletRequest request, String localIps) {
        return StringUtils.indexOf(localIps, getClientIp(request)) != -1;
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("x-forwarded-for");
        return StringUtils.isNotEmpty(remoteAddr) ? remoteAddr : request.getRemoteAddr();
    }

    public static String getUserAgentSmall(String userAgent) {
        if (userAgent == null)
            userAgent = "";

        for (String[] br : BROWSERS) {
            if (userAgent.contains(br[0]))
                return br[1];
        }
        
        return "other";
    }
}
