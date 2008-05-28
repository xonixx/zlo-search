package info.xonix.zlo.web.test;

import java.util.Collection;
import java.util.Arrays;

/**
 * Author: Vovan
 * Date: 28.05.2008
 * Time: 18:57:13
 */
public class Test1 {
    public static void main(String[] args) {

        Test1 t = new Test1();
        t.q(Arrays.asList((IA)new A()));
    }

    public void q(Collection<IA> col) {};
}

interface IA {

}

class A implements IA {

}
