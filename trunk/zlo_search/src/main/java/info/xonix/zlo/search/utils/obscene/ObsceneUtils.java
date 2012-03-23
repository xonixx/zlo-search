package info.xonix.zlo.search.utils.obscene;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.ExceptionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.IO;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: gubarkov
 * Date: 16.01.12
 * Time: 15:44
 */
public class ObsceneUtils {
    public static final String OBSCENE_FILE = "obscene.txt";
    public static final char COMMENT_CHAR = '#';

    private static final char[][] EN_2_RU_CHARS = {
            {'a', 'а'},
            {'b', 'в'},
            {'c', 'с'},
            {'e', 'е'},
            {'m', 'м'},
            {'o', 'о'},
            {'p', 'р'},
            {'u', 'и'},
            {'x', 'х'},
            {'y', 'у'},
    };

    private static final Pattern OBSCENE_REGEXP = prepareObsceneRegexp();

    private static Pattern prepareObsceneRegexp() {
        final String txt;
        try {
            txt = IO.toString(ObsceneUtils.class.getResourceAsStream(OBSCENE_FILE), Config.UTF_8);
        } catch (IOException e) {
            throw ExceptionUtils.rethrowAsRuntime(e);
        }

        final String[] lines = txt.split("\n");

        List<String> _words_re_parts = new LinkedList<String>();

        for (String line : lines) {
            final String trimmedLine = line.trim();
            if (!"".equals(trimmedLine) && COMMENT_CHAR != trimmedLine.charAt(0)) {
                _words_re_parts.add("\\b" + trimmedLine);
            }
        }

        return Pattern.compile(StringUtils.join(_words_re_parts, "|"));
    }

    public static String unObscene(String txt, String defaultStr) {
        return containsObsceneWord(txt) ? defaultStr : txt;
    }

    public static boolean containsObsceneWord(String txt) {
        if (StringUtils.isEmpty(txt)) {
            return false;
        }

        txt = normalize(txt);

        return OBSCENE_REGEXP.matcher(txt).find();
    }

    private static String normalize(String txt) {
        Check.isSet(txt);

        txt = txt.toLowerCase();
        txt = tr(txt, EN_2_RU_CHARS);

        txt = txt.replace('ё', 'е');

        return txt;
    }

    private static String tr(String str, char[][] from_to) {
        for (char[] chars : from_to) {
            str = str.replace(chars[0], chars[1]);
        }
        return str;
    }
}
