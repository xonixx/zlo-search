package info.xonix.zlo.search.test.junit;

import org.junit.Test;

import static info.xonix.zlo.search.utils.obscene.ObsceneUtils.containsObsceneWord;
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
        assertFalse(containsObsceneWord(""));
        assertFalse(containsObsceneWord("привет"));

        assertFalse(containsObsceneWord("оскорблять"));
        assertTrue(containsObsceneWord("блять"));

        assertFalse(containsObsceneWord("не психуй"));
        assertTrue(containsObsceneWord("соси ХУЙ"));
        assertTrue(containsObsceneWord("СОСИ ХУЁК"));

        assertTrue(containsObsceneWord("пЁзднуться"));

        assertTrue(containsObsceneWord("долбоёб"));
        assertTrue(containsObsceneWord("далбаеб"));
        assertTrue(containsObsceneWord("долБАеб"));
        assertTrue(containsObsceneWord("дАЛбОЁб"));

        assertTrue(containsObsceneWord("привет хуёвый"));
        assertTrue(containsObsceneWord("привет блядь"));
        assertTrue(containsObsceneWord("привет блядский"));
        assertTrue(containsObsceneWord("привет бля"));

        assertTrue(containsObsceneWord("привет пидор"));
        assertTrue(containsObsceneWord("привет пидар"));

        assertTrue(containsObsceneWord("пИзДлявая"));
        assertTrue(containsObsceneWord("пUзДлявAя"));

        assertTrue(containsObsceneWord("хYй"));
        assertTrue(containsObsceneWord("XУй"));

        assertTrue(containsObsceneWord("гавна поешь"));
        assertTrue(containsObsceneWord("имярек мудак"));
        assertTrue(containsObsceneWord("мудак имярек"));
        assertTrue(containsObsceneWord("имярек гей"));
    }
}
