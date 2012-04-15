package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
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
        checkCorrectAnalyzing("привет мир, !!! ййй qqq 123 123qw 123привет",
                new String[]{"привет", "мир", "ййй", "qqq", "123", "123qw", "123привет"});
    }

    @Test
    public void test2() throws IOException {
        checkCorrectAnalyzing("бежать 777777 хотел Кровать? и как всЕгда ,,,во cisco",
                new String[]{"бежа", "777777", "хотел", "крова", "всегд", "cisco"});
    }

    @Test
    public void test3() throws IOException {
        checkCorrectAnalyzing("в чем смысл жизни? Жизнь это 01234567890 Ёж ёлка",
                new String[]{"смысл", "жизн", "жизн", "01234567890", "еж", "елка"});
    }

    @Test
    public void testElka() throws IOException {
        checkCorrectAnalyzing("елка ёлка",
                new String[]{"елк", "елка"}); // TODO: WTF?
    }

    private void checkCorrectAnalyzing(String str, String[] expectedResult) throws IOException {
        System.out.println("-----");
        final TokenStream tokenStream = analyzer.tokenStream(MessageFields.BODY, new StringReader(str));

        tokenStream.reset();

        final TermAttribute termAttribute = tokenStream.getAttribute(TermAttribute.class);
//        final PositionIncrementAttribute positionIncrementAttribute = (PositionIncrementAttribute) tokenStream.getAttribute(PositionIncrementAttribute.class);

        List<String> tokens = new LinkedList<String>();

        while (tokenStream.incrementToken()) {
            tokens.add(termAttribute.term());
            System.out.print(">> ");
//            System.out.println(positionIncrementAttribute.getPositionIncrement());
            System.out.println(termAttribute.term());
        }

        Assert.assertArrayEquals(expectedResult, tokens.toArray());
    }
}
