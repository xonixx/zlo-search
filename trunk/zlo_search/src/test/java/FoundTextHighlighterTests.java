import info.xonix.zlo.search.FoundTextHighlighter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:45
 */
@RunWith(Parameterized.class)
public class FoundTextHighlighterTests {
    private static FoundTextHighlighter foundTextHighlighter = new FoundTextHighlighter();

    static {
        foundTextHighlighter.setPreHl("{");
        foundTextHighlighter.setPostHl("}");
    }

    private String input;
    private String[] words;
    private String expected;

    public FoundTextHighlighterTests(String input, String[] words, String expected) {
        this.input = input;
        this.words = words;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static List<Object[]> generateParams() {
        List<Object[]> params = new LinkedList<Object[]>();

        params.add(new Object[]{
                "ѕ€ть человек пострадали от задымлени€ после взрыва на ќхотном р€ду",
                new String[]{"п*да"},
                "ѕ€ть человек {пострадали} от задымлени€ после взрыва на ќхотном р€ду"});

        params.add(new Object[]{
                "aaa hhhhhhh<bbb cccc dddddd>eee dddd fffffff GgGgGg</hhhh>",
                "AAa ccc ddd hh ggg".split(" "),
                "{aaa} {hhhhhhh}<bbb cccc dddddd>eee {dddd} fffffff {GgGgGg}</hhhh>"});

        params.add(new Object[]{
                "ппѕѕппѕ е®еЄ≈ее цу÷”ц”",
                "ппп е≈≈е".split(" "),
                "{ппѕѕппѕ} {е®еЄ≈ее} цу÷”ц”"});

        params.add(new Object[]{
                "ппѕѕппѕ е®еЄ≈ее цу÷”ц”",
                "ппп ®Є®®".split(" "),
                "{ппѕѕппѕ} {е®еЄ≈ее} цу÷”ц”"});

        return params;
    }

    @Test
    public void test() {
        assertThat(highlight(input, words), equalTo(expected));
    }

    private String highlight(String input, String[] words) {
        foundTextHighlighter.setText(input);
        foundTextHighlighter.setHighlightWords(words);
        return foundTextHighlighter.getHighlightedText();
    }
}
