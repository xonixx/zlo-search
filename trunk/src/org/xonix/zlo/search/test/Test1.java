package org.xonix.zlo.search.test;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.model.ZloMessage;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.io.IOException;

/**
 * Author: Vovan
 * Date: 06.09.2007
 * Time: 0:48:24
 */
public class Test1 {
    public static void main(String[] args) {
        m7();
    }

    public static void m1(){
        QueryParser qp = new QueryParser("field1", new SimpleAnalyzer());
        try {
            Query q = qp.parse("[1.1.04 TO 5.30.05]");
            System.out.println(q.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void m2() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        System.out.println(df.format(new Date()));
    }

    public static void m3() {
        //new StandardAnalyzer().
        System.out.println(new Date().hashCode());
    }

    public static void m4() {
//        NumberFormat f = new DecimalFormat("0000000000");
//        System.out.println(f.format(-123));
//        System.out.println(Integer.parseInt(f.format(-123)));
//        System.out.println(ZloSearcher.searchMsgByNum(3765011));
        for (Object o : ZloSearcher.searchInNumRange(3765002, 3765007)) {
            System.out.println(o);
        }
    }
    public static void m5() {
        for (int i=0; i<10; i++){
            try {
                System.out.println(">"+ DAO.Site.getLastRootMessageNumber());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class T extends Thread {
        private int i=0;

        public T() {
        }

        public void run() {
            for (;i <= 10; i++) {
                System.out.println(">" + i);
            }
        }
    }

    public static void m6() {
        System.out.println("before");
        T t = new T();
        T t1 = new T();
        t.start();
        t1.start();
        try {
            t.join();
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after");
    }

    public static void m7() {
        try {
            for (ZloMessage m : DAO.Site.getMessages(10, 110)) {
                System.out.println(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
