import junit.framework.TestCase;
import org.junit.Test;
import org.xonix.zlo.search.daemon.TimeUtils;

/**
 * Author: gubarkov
 * Date: 03.10.2007
 * Time: 22:56:24
 */
public class Tests1 extends TestCase {
    @Test
    public void testTimeUtils() {
        assertEquals(125000, TimeUtils.parseToMilliSeconds("2m5s"));
        assertEquals(120000, TimeUtils.parseToMilliSeconds("2m"));
        assertEquals(5000, TimeUtils.parseToMilliSeconds("5s"));
    }
}
