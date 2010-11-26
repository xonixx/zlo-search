import info.xonix.zlo.search.utils.EnvUtils;
import org.junit.Test;

import java.net.UnknownHostException;

/**
 * User: Vovan
 * Date: 26.11.2010
 * Time: 20:18:20
 */
public class Test1 {
    @Test
    public void test1() throws UnknownHostException {
        System.out.println(EnvUtils.getHostName());
        System.out.println(EnvUtils.getUserName());
    }
}
