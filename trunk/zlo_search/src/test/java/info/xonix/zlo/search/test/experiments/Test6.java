package info.xonix.zlo.search.test.experiments;

import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Author: Vovan
 * Date: 27.05.2008
 * Time: 20:31:07
 */
public class Test6 {
    public static void main(String[] args) {
        System.out.println(HtmlUtils.htmlEscape("<привет>"));
    }

    public static void main1(String[] args) {
        A1 o = new B1();
        o.a();

        try {
//            Method m = A1.class.getMethod("a");
//            m.invoke(o);
            //System.out.println(m);
            A1 a = new A1();
            a.c = 123;
            A1.class.getMethod("printC").invoke(a);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

class A1 {
    public int c;

    public void a() {
        System.out.println("a");
    }

    public void printC() {
        System.out.println(c);
    }

}

class B1 extends A1 {
    public void a() {
        System.out.println("b");
    }
}
