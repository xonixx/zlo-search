package org.xonix.zlo.search.test;

import org.apache.lucene.document.DateTools;

import java.text.ParseException;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 14:34:18
 */
public class Main1 {
    public static void main(String[] args) {
        System.out.println(DateTools.dateToString(new Date(), DateTools.Resolution.MINUTE));
        try {
            System.out.println(DateTools.stringToDate("200706011035"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
