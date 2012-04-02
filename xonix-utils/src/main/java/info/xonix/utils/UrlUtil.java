package info.xonix.utils;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 17:05
 * <p/>
 * see
 * http://forums.iis.net/t/1159852.aspx
 * http://tools.ietf.org/html/rfc3986#section-5.4
 * http://bugs.sun.com/view_bug.do?bug_id=6791060
 */
public class UrlUtil {

    private static final String URL_START_REGEXP_S = "^[^:]+://";
    private static final Pattern URL_START_REGEXP = Pattern.compile(URL_START_REGEXP_S);

    public static String combineUrls(String rootUrl, String possiblyRelativeUrl) {
        if (isAbsoluteUrl(possiblyRelativeUrl)) {
            return possiblyRelativeUrl;
        }

        final URI uri = URI.create(rootUrl);

        // fix for rfc3986
        if (possiblyRelativeUrl.startsWith("?")){
            possiblyRelativeUrl = uri.getPath() + possiblyRelativeUrl;
        }

        final URI resolvedUri = uri.resolve(possiblyRelativeUrl);
        return resolvedUri.toString();
    }

    public static boolean isAbsoluteUrl(String url) {
        // TODO: possibly do smth more intelligent
        return url.startsWith("http:") || url.startsWith("https:");
    }

    public static String urlWithoutSchema(String url) {
        return url.replaceAll(URL_START_REGEXP_S, "");
    }

    // TODO: this may be not good (f.e. file:/)
    public static boolean isUrl(String urlOrNot) {
        return URL_START_REGEXP.matcher(urlOrNot).find();
    }
}
