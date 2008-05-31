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

        A a = new A();
        new B().b((IA)a);
        new B().b(null);
    }

    public void q(Collection<IA> col) {};
}

interface IA {

}

class A implements IA {

}

class B {

    public void b(A a){
        System.out.println("A called");}

    public void b(IA a){
        System.out.println("IA called");}

}
