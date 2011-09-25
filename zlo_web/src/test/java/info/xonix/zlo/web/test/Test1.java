package info.xonix.zlo.web.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 28.05.2008
 * Time: 18:57:13
 */
public class Test1 {
    public static void main(String[] args) {
        System.out.println(new Date());
    }

    public static void main1(String[] args) {

        Test1 t = new Test1();
        t.q(Arrays.asList((IA) new A()));

        A a = new A();
        new B().b((IA) a);
        new B().b(null);
    }

    public void q(Collection<IA> col) {
    }

    ;
}

class B {

    public void b(A a) {
        System.out.println("A called");
    }

    public void b(IA a) {
        System.out.println("IA called");
    }

}