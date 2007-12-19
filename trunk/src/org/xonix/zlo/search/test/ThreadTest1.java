package org.xonix.zlo.search.test;

/**
 * Author: Vovan
 * Date: 19.12.2007
 * Time: 21:40:59
 */
public class ThreadTest1 {
    public static void main(String[] args) {
        new T1("1").start();
        new T1("2").start();
    }
}

class T1 extends Thread {

    private final static Object l = new Object();

    public T1(String name) {
        super(name);
    }

    public void run() {
        for (int i=0; i<100; i++) {
            if ("1".equals(getName())) {
                do1();
            } else {
                do2();
            }

        }
    }

    private void sl() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void do1() {
        synchronized(l) {
            System.out.println("1");
            sl();
            System.out.println("1");
            sl();
            System.out.println("1");
            sl();
            System.out.println("1");
            sl();
            System.out.println("1");
            sl();
        }
    }
    private void do2() {
        synchronized(l) {
            System.out.println("2");
            sl();
            System.out.println("2");
            sl();
            System.out.println("2");
            sl();
            System.out.println("2");
            sl();
            System.out.println("2");
            sl();
        }
    }
}