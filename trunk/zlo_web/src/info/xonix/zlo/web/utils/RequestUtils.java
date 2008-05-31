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

        return userAgent.contains("MSIE")
            ? "Internet Explorer"
            : userAgent.contains("Firefox") || userAgent.contains("Minefield")
            ? "Firefox"
            : userAgent.contains("Safari")
            ? "Safari"
            : userAgent.contains("Feedfetcher-Google")
            ? "Google Feed Reader"
            : userAgent.contains("YandexBlog")
            ? "Yandex Feed Reader"
            : userAgent.contains("Yahoo Pipes")
            ? "Yahoo Feed Reader"
            : userAgent.contains("Mozilla")
            ? "Mozilla"
            : userAgent.contains("Opera")
            ? "Opera"
            : userAgent.contains("Konqueror")
            ? "Konqueror"
            : "other";
    }
}
