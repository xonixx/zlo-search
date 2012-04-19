package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vovan
 * Date: 19.03.11
 * Time: 22:02
 */
public class RussianAnalyzerTests {
    private static Analyzer analyzer;

    @BeforeClass
    public static void setUp() {
        Config config = AppSpringContext.getApplicationContextForTesting().getBean(Config.class);
        analyzer = config.getMessageAnalyzer();
        System.out.println("Using analyzer: " + analyzer.getClass());
    }

    @Test
    public void test1() throws IOException {
        checkCorrectAnalyzing(analyzer, "привет мир, !!! ййй qqq 123 123qw 123привет",
                new String[]{"привет", "мир", "ййй", "qqq", "123", "123qw", "123привет"});
    }

    @Test
    public void test2() throws IOException {
        checkCorrectAnalyzing(analyzer, "бежать 777777 хотел Кровать? и как всЕгда ,,,во cisco",
                new String[]{"бежа", "777777", "хотел", "крова", "всегд", "cisco"});
    }

    @Test
    public void test3() throws IOException {
        checkCorrectAnalyzing(analyzer, "в чем смысл жизни? Жизнь это 01234567890 Ёж ёлка",
                new String[]{"смысл", "жизн", "жизн", "01234567890", "еж", "елк"});
    }

    @Test
    public void testElka() throws IOException {
        checkCorrectAnalyzing(analyzer, "елка ёлка Ёлки ЁлКоЙ",
                new String[]{"елк", "елк", "елк", "елк"});
    }

    @Test
    public void testDots() throws IOException {
        final String str = "aaa.bbb.com:8888 " +
                "a,b;c/d'e$f&g*h+i-j%k/l_m#n@o!p?q>r\"s~t(u`v|z}y\\z";

        checkCorrectAnalyzing(analyzer, str,
                new String[]{"aaa", "bbb", "com", "8888", "a", "b", "c", "d", "e",
                        "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
                        "r", "s", "t", "u", "v", "z", "y", "z"});
    }

    @Test
    public void testDotsShow() throws IOException {
        final String str = "aaa.bbb.com:8888 " +
                "a,b;c/d'e$f&g*h+i-j%k/l_m#n@o!p?q>r\"s~t(u`v|z}y\\z";

        System.out.println("New analyzer:");
        System.out.println(getTokens(new RussianAnalyzer(Version.LUCENE_36), str));

        System.out.println("Old analyzer:");
        System.out.println(getTokens(new RussianAnalyzer(Version.LUCENE_30), str));
    }

    private void checkCorrectAnalyzing(final Analyzer theAnalyzer, String str, String[] expectedResult) throws IOException {
        System.out.println("-----");

        List<String> tokens = getTokens(theAnalyzer, str);

        Assert.assertArrayEquals(expectedResult, tokens.toArray());
    }

    private List<String> getTokens(Analyzer theAnalyzer, String str) throws IOException {
        final TokenStream tokenStream = theAnalyzer.tokenStream(MessageFields.BODY, new StringReader(str));

        tokenStream.reset();

        final CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);

        List<String> tokens = new LinkedList<String>();

        while (tokenStream.incrementToken()) {
            final String term = new String(termAttribute.buffer(), 0, termAttribute.length());
            tokens.add(term);
//            System.out.println(">>" + term);
        }
        return tokens;
    }
}
