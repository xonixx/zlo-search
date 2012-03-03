package info.xonix.zlo.web.utils;

import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.SiteName;

import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Vovan
 * Date: 01.05.2008
 * Time: 5:11:18
 */
public final class RequestUtils {
    private static final Config config = AppSpringContext.get(Config.class);

    public static String[][] BROWSERS = {
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

    /**
     * Weather the ip of client sending request is local ip
     *
     * @param request  http request
     * @param localIps list of local ips from settings
     * @return true if local
     */
    private static boolean isLocalIp(HttpServletRequest request, String[] localIps) {
        String clientIp = getClientIp(request);
        for (String localIp : localIps) {
            if (localIp.equals(clientIp))
                return true;
        }
        return false;
    }

    /**
     * use {@link #isPowerUser} instead
     *
     * @param request request
     * @return true if local
     */
    @Deprecated
    public static boolean isLocalIp(HttpServletRequest request) {
        return isLocalIp(request, config.getProp("localIps").split("\\|"));
    }

    /**
     * checking power user rights based on secret key presence in cookie
     *
     * @param request http request
     * @return true if secret key present
     */
    public static boolean isPowerUser(HttpServletRequest request) {
        return CookieUtils.isCookiePresent(request, config.getPowerUserKey());
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader(HttpHeader.X_FORWARDED_FOR);
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

    // TODO: VVV--- improve or del
    // TODO: store users site in cookie

    /**
     * @param referer referer header
     * @param site    site
     * @return site root url, based on Host header

    public static String getSiteRoot(String referer, String forumId) {
        if (SiteName.ZLO.equals(forumId.getName())) {
            final String zloRoot = "zlo.rt.mipt.ru";
            final String boardRoot = "board.rt.mipt.ru";

            if (StringUtils.isNotEmpty(referer)) {
                if (referer.contains(zloRoot)) {
                    return zloRoot;
                } else if (referer.contains(boardRoot)) {
                    return boardRoot;
                }
            }
        }

        return site.getSiteUrl();
    }*/

    /**
     * @param request http request
     * @param site    site
     * @return site root url, based on Host header

    public static String getSiteRoot(HttpServletRequest request, String forumId) {
//        return getSiteRoot(request.getHeader(HttpHeader.REFERER), site);
    }*/
}
