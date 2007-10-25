package org.xonix.zlo.search.test;

/**
 * Author: Vovan
 * Date: 25.10.2007
 * Time: 3:23:44
 */
public class Obj1 {
    public int a() {
        a:
        for (int j=0; j<10; j++) {
        for (int i=0; i<10; i++) {
            if (j>5)
            break a;
        }
        }

        int i=Integer.parseInt("12");

        switch (i) {
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
                break;
            case 3:
                System.out.println("3");
        }

        i=0;

        lbl1: {
            i++;
            System.out.println(">" + i);
            if (i<10)
               f();
        }
          lbl2 : {int b = 123;}
        return 1;
    }

    private void f() {
    }

    public static void main(String[] args) {
        new Obj1().a();
    }
}
