package info.xonix.zlo.search.utils;

import info.xonix.zlo.search.config.Config;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Vovan
 * Date: 22.09.2007
 * Time: 22:31:40
 */
public class HtmlUtils {
    private static final String[] BOTH_TAGS = {"a", "b", "i", "p", "blockquote", "span", "pre", "u", "center"};
    private static final String[] SINGLE_TAGS = {"br", "hr", "img"};
    private final static String SPACE = " ";
    private final static String NEW_LINE = "\n";

    // суть в том, чтоб не считать смайлы за картинки, потому важно http
    private static final Pattern IMG = Pattern.compile("(?i)<img.*?src\\s*=\\s*(\"?|\'?)https?://(.*)(\\1).*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL = Pattern.compile("(?i)<a\\s.+?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_EXTRACTOR = Pattern.compile("(?i)<a\\s+href=(\"|\')(.+?)(\\1).*?>", Pattern.CASE_INSENSITIVE);

    private static final String POSIBLE_SPACE_AND_ATTRIBS = "(\\s*?|\\s+.*?)";

    public static String cleanHtml(String s) {
        s = extractUrls(s);

        for (String tag : BOTH_TAGS) {
            s = s.replaceAll("(?i)<" + tag + POSIBLE_SPACE_AND_ATTRIBS + ">", SPACE)
                    .replaceAll("(?i)</" + tag + ">", SPACE);
        }

        for (String tag : SINGLE_TAGS) {
            s = s.replaceAll("(?i)<" + tag + POSIBLE_SPACE_AND_ATTRIBS + ">", NEW_LINE)
                    .replaceAll("(?i)<" + tag + POSIBLE_SPACE_AND_ATTRIBS + "/>", NEW_LINE);
        }

        s = unescapeHtml(s);

        return s;
    }

    public static String extractUrls(String s) {
        return URL_EXTRACTOR.matcher(s).replaceAll("$2 ");
    }

    public static String unescapeHtml(String s) {
        return StringEscapeUtils.unescapeHtml(s);
    }

    public static String urlencode(String s, String charset) {
        try {
            return URLEncoder.encode(s, StringUtils.isNotEmpty(charset) ? charset : Config.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return StringEscapeUtils.escapeHtml(s);
        }
    }

    public static boolean hasUrl(String s) {
        return URL.matcher(s).find();
    }

    public static boolean hasImg(String s, String forumId) {
        Matcher matcher = IMG.matcher(s);

        boolean isFound = matcher.find();

        // TODO: SMILES = NOT IMAGES !!! Implement !!!
        if (isFound/* &&
                (forumId != null
                        && StringUtils.isNotEmpty(forumId.getSiteSmilesPath())
                        && matcher.group(2).contains(forumId.getSiteSmilesPath()))*/)
            return false; // this is smile
        else
            return isFound;
    }

    public static boolean hasImg(String s) {
        return hasImg(s, null);
    }

    public static String cleanBoardSpecific(String s) {
        s = cleanHtml(s);
        s = s.replaceAll("\\[q\\]", " ").replaceAll("\\[/q\\]", " ");
        return s;
    }

    public static boolean remindsUrl(String text) {
        if (text == null)
            return false;
        text = text.trim();
        return !text.contains(" ") && !text.contains("\t") && text.startsWith("http");
    }
}
