package info.xonix.zlo.search.test.experiments;

import info.xonix.utils.EnvUtils;
import org.junit.Test;

import java.net.UnknownHostException;

/**
 * User: Vovan
 * Date: 26.11.2010
 * Time: 20:18:20
 */
public class TestEnvUtils {
    @Test
    public void test1() throws UnknownHostException {
        System.out.println(EnvUtils.getHostName());
        System.out.println(EnvUtils.getUserName());
    }

/*    @Test
    public void test2() throws ClassNotFoundException {
        System.out.println(Class.forName("info.xonix.zlo.search.dao.MessagesDaoImpl$1"));
    }*/
}
