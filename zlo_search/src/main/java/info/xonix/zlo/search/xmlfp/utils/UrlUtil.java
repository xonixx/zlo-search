package info.xonix.zlo.search.xmlfp.utils;

import java.net.URI;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 17:05
 */
public class UrlUtil {
    public static String combineUrls(String rootUrl, String possiblyRelativeUrl) {
        if (isAbsoluteUrl(possiblyRelativeUrl)) {
            return possiblyRelativeUrl;
        }

        final URI uri = URI.create(rootUrl);
        final URI resolvedUri = uri.resolve(possiblyRelativeUrl);
        return resolvedUri.toString();
    }

    public static boolean isAbsoluteUrl(String url) {
        // TODO: possibly do smth more intelligent
        return url.startsWith("http:") || url.startsWith("https:");
    }
}
