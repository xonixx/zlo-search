package org.xonix.zlo.search.test;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 17:36:51
 */
public class t1 {
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<1000; i++) {
            sb.append((char)((int)(Math.random()*26) + 97));
        }
        System.out.println(sb.toString());
    }
}
