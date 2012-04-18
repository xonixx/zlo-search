package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.utils.obscene.ObsceneAnalyzer;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: gubarkov
 * Date: 16.01.12
 * Time: 17:09
 */
public class ObsceneUtilsTests {

    private ObsceneAnalyzer obsceneAnalyzer = new ObsceneAnalyzer(
//            "info/xonix/zlo/search/test/junit/oscene_testfile.txt"
//            "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\src\\test\\java\\info\\xonix\\zlo\\search\\test\\junit\\obscene_testfile.txt"
            "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\src\\main\\java\\info\\xonix\\zlo\\search\\config\\usernames_to_filter.txt"
    );

    @Test
    public void test1() {
        assertFalse(containsObsceneWord(""));
        assertFalse(containsObsceneWord("привет"));

        assertFalse(containsObsceneWord("оскорблять"));
        assertTrue(containsObsceneWord("блять"));

        assertFalse(containsObsceneWord("не психуй"));
        assertTrue(containsObsceneWord("соси ХУЙ"));
        assertTrue(containsObsceneWord("хуи"));
        assertTrue(containsObsceneWord("хуя"));
        assertTrue(containsObsceneWord("хуёвый"));
        assertTrue(containsObsceneWord("СОСИ ХУЁК"));
        assertTrue(containsObsceneWord("ХУЁК"));
        assertTrue(containsObsceneWord("СОСИ"));

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
        assertTrue(containsObsceneWord("привет пидираст"));
        assertTrue(containsObsceneWord("привет педораз"));

        assertTrue(containsObsceneWord("пИзДлявая"));
        assertTrue(containsObsceneWord("пUзДлявAя"));

        assertTrue(containsObsceneWord("хYй"));
        assertTrue(containsObsceneWord("XУй"));
        assertTrue(containsObsceneWord("хЕр"));

        assertTrue(containsObsceneWord("мудило"));
        assertTrue(containsObsceneWord("мудло"));

        assertTrue(containsObsceneWord("гавна поешь"));
        assertTrue(containsObsceneWord("имярек мудак"));
        assertTrue(containsObsceneWord("мудак имярек"));
        assertTrue(containsObsceneWord("имярек гей"));
        assertTrue(containsObsceneWord("имярек геи"));

        assertTrue(containsObsceneWord("анал"));
        assertTrue(containsObsceneWord("анальный"));
        assertFalse(containsObsceneWord("аналит"));

        // from file
//        assertTrue(containsObsceneWord("ааа"));
//        assertTrue(containsObsceneWord("ввв"));
    }

/*    @Test
    public void normalizationBackTest() {
        assertTrue(containsObsceneWord("uuu"));
    }*/

    @Test
    public void testFizix() {
        assertTrue(containsObsceneWord("fysix"));
        assertTrue(containsObsceneWord("fisyks"));
        assertTrue(containsObsceneWord("phisyks2007"));
        assertTrue(containsObsceneWord("physics2007"));
        assertTrue(containsObsceneWord("фiзикс"));
        assertTrue(containsObsceneWord("fiziкs"));
        assertTrue(containsObsceneWord("химикс"));
        assertTrue(containsObsceneWord("химиkc"));
        assertTrue(containsObsceneWord("хuмukc"));
        assertTrue(containsObsceneWord("xumukc"));
    }

    @Test
    public void testAjgul() {
        assertTrue(containsObsceneWord("айгуль"));
        assertTrue(containsObsceneWord("аигуль"));
        assertTrue(containsObsceneWord("аiгуль"));
        assertTrue(containsObsceneWord("iгуль"));
    }

    private boolean containsObsceneWord(String s) {
        return obsceneAnalyzer.containsObsceneWord(s);
    }
}
