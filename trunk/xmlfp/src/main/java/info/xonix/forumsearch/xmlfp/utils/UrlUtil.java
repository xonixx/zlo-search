package info.xonix.forumsearch.xmlfp.utils;

import java.net.URI;

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
        return url.replaceAll("^[^:]+://", "");
    }
}
