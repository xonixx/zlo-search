import info.xonix.zlo.search.utils.obscene.ObsceneUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: gubarkov
 * Date: 16.01.12
 * Time: 17:09
 */
public class ObsceneUtilsTests {
    @Test
    public void test1() {
        assertFalse(ObsceneUtils.containsObsceneWord(""));
        assertFalse(ObsceneUtils.containsObsceneWord("привет"));

        assertFalse(ObsceneUtils.containsObsceneWord("оскорблять"));
        assertTrue(ObsceneUtils.containsObsceneWord("блять"));

        assertFalse(ObsceneUtils.containsObsceneWord("не психуй"));
        assertTrue(ObsceneUtils.containsObsceneWord("соси ХУЙ"));

        assertTrue(ObsceneUtils.containsObsceneWord("привет хуёвый"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет блядь"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет блядский"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет бля"));

        assertTrue(ObsceneUtils.containsObsceneWord("пИзДлявая"));
        assertTrue(ObsceneUtils.containsObsceneWord("пUзДлявAя"));

        assertTrue(ObsceneUtils.containsObsceneWord("хYй"));
        assertTrue(ObsceneUtils.containsObsceneWord("XУй"));

        assertTrue(ObsceneUtils.containsObsceneWord("гавна поешь"));
        assertTrue(ObsceneUtils.containsObsceneWord("имярек мудак"));
        assertTrue(ObsceneUtils.containsObsceneWord("мудак имярек"));
        assertTrue(ObsceneUtils.containsObsceneWord("имярек гей"));
    }
}
