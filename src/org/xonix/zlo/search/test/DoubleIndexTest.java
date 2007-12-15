package org.xonix.zlo.search.test;

import org.xonix.zlo.search.DoubleIndexSearcher;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.DoubleHits;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 16.12.2007
 * Time: 1:14:23
 */
public class DoubleIndexTest {
    public static void main(String[] args) {
        DoubleIndexSearcher dis = new DoubleIndexSearcher("D:\\TEST\\JAVA\\ZloSearcher\\__test", ZloSearcher.getDateSort());
        try {
            DoubleHits dh = dis.search(new MatchAllDocsQuery(), new Sort());
            System.out.println(dh.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
