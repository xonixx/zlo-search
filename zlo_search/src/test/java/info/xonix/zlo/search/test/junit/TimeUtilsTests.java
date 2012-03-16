package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.utils.TimeUtils;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 16.03.12
 * Time: 16:10
 */
public class TimeUtilsTests {
    @Test
    public void testTimeUtils() {
        assertEquals(125000, TimeUtils.parseToMilliSeconds("2m5s"));
        assertEquals(120000, TimeUtils.parseToMilliSeconds("2m"));
        assertEquals(5000, TimeUtils.parseToMilliSeconds("5s"));
    }
}
