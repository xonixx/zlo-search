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

        assertTrue(ObsceneUtils.containsObsceneWord("привет хуёвый"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет блядь"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет блядский"));
        assertTrue(ObsceneUtils.containsObsceneWord("привет бля"));
    }
}
