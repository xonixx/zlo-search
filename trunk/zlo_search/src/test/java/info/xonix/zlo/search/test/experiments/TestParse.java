package info.xonix.zlo.search.test.experiments;

import info.xonix.zlo.search.config.DateFormats;
import org.junit.Test;

import java.text.ParseException;

/**
 * User: Vovan
 * Date: 23.02.2009
 * Time: 0:20:23
 */
public class TestParse {

    @Test
    public void test1() {
        try {
            System.out.println(DateFormats.DF_BOARD_MSG.get().parse("2 16 13:14:10 2009"));
            System.out.println(DateFormats.DF_BOARD_MSG.get().parse("2 16 12:14:10 2009"));
            System.out.println(DateFormats.DF_BOARD_MSG.get().parse("2 16 11:14:10 2009"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
