package info.xonix.zlo.search.utils.obscene;

import info.xonix.utils.Check;
import info.xonix.utils.ConfigUtils;
import info.xonix.utils.ExceptionUtils;
import info.xonix.zlo.search.config.Config;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: gubarkov
 * Date: 16.01.12
 * Time: 15:44
 */
public class ObsceneAnalyzer {
    private static final String OBSCENE_FILE = "obscene.txt";
    private static final char COMMENT_CHAR = '#';

    private static final char[][] EN_2_RU_CHARS = {
            {'a', 'а'},
            {'b', 'в'},
            {'c', 'с'},
            {'e', 'е'},
            {'k', 'к'},
            {'m', 'м'},
            {'o', 'о'},
            {'n', 'п'},
            {'p', 'р'},
            {'u', 'и'},
            {'i', 'и'},
            {'x', 'х'},
            {'y', 'у'},
    };
    private static final char[][] RU_2_EN_CHARS = {
            {'а', 'a'},
            {'в', 'b'},
            {'с', 'c'},
            {'е', 'e'},
            {'к', 'k'},
            {'м', 'm'},
            {'о', 'o'},
            {'п', 'n'},
            {'р', 'p'},
            {'и', 'u'},
            {'х', 'x'},
            {'у', 'y'},
    };
/*
    private static final char[][] RU_2_EN_CHARS;

    static {
        RU_2_EN_CHARS = new char[EN_2_RU_CHARS.length][];

        for (int i = 0; i < EN_2_RU_CHARS.length; i++) {
            char[] pair = EN_2_RU_CHARS[i];
            RU_2_EN_CHARS[i] = new char[]{pair[1], pair[0]};
        }
    }
*/

    private final Pattern obsceneRegexp;

    public ObsceneAnalyzer() {
        this(Collections.<String>emptyList());
    }

    public ObsceneAnalyzer(String fileName) {
        this(wordsFromInputStream(ConfigUtils.resolvePath(fileName)));
    }

    public ObsceneAnalyzer(List<String> additionalObsceneWords) {
        Check.isSet(additionalObsceneWords, "additionalObsceneWords");

        obsceneRegexp = prepareObsceneRegexp(additionalObsceneWords);
    }

    private Pattern prepareObsceneRegexp(List<String> additionalObsceneWords) {
        List<String> allObsceneWords = new LinkedList<String>();

        allObsceneWords.addAll(wordsFromInputStream(
                ObsceneAnalyzer.class.getResourceAsStream(OBSCENE_FILE)));
        allObsceneWords.addAll(additionalObsceneWords);

        List<String> _words_re_parts = new LinkedList<String>();

        for (String obsceneWord : allObsceneWords) {
            _words_re_parts.add("\\b" + obsceneWord);
        }

        return Pattern.compile(StringUtils.join(_words_re_parts, "|"));
    }

    private static List<String> wordsFromInputStream(final InputStream stream) {
        if (stream == null) {
            throw new NullPointerException("stream");
        }

        List<String> obsceneWords = new LinkedList<String>();
        final String txt;

        try {
            txt = IO.toString(stream, Config.UTF_8);
        } catch (IOException e) {
            throw ExceptionUtils.rethrowAsRuntime(e);
        }

        final String[] lines = txt.split("\n");

        for (String line : lines) {
            final String trimmedLine = line.trim();
            if (!"".equals(trimmedLine) && COMMENT_CHAR != trimmedLine.charAt(0)) {
                obsceneWords.add(trimmedLine);
            }
        }

        return obsceneWords;
    }

    public String unObscene(String txt, String defaultStr) {
        return containsObsceneWord(txt) ? defaultStr : txt;
    }

    public boolean containsObsceneWord(String txt) {
        if (StringUtils.isEmpty(txt)) {
            return false;
        }

        return obsceneRegexp.matcher(txt).find() ||
                obsceneRegexp.matcher(normalize(txt, NormalizeDirection.TO_RU)).find() ||
                obsceneRegexp.matcher(normalize(txt, NormalizeDirection.TO_EN)).find();
    }

    public boolean isSafe(String string) {
        return !containsObsceneWord(string);
    }

    public boolean allSafe(String... strings) {
        for (String string : strings) {
            if (containsObsceneWord(string)) {
                return false;
            }
        }

        return true;
    }

    private static String normalize(String txt, NormalizeDirection normalizeDirection) {
        Check.isSet(txt);

        txt = txt.toLowerCase();

        if (normalizeDirection == NormalizeDirection.TO_RU) {
            txt = tr(txt, EN_2_RU_CHARS);
            txt = txt.replace('ё', 'е');
        } else if (normalizeDirection == NormalizeDirection.TO_EN) {
            txt = tr(txt, RU_2_EN_CHARS);
        }

        return txt;
    }

    private static String tr(String str, char[][] from_to) {
        for (char[] chars : from_to) {
            str = str.replace(chars[0], chars[1]);
        }
        return str;
    }
}
