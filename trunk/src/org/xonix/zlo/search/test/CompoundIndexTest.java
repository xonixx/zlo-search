package org.xonix.zlo.search.test;

import org.apache.lucene.index.IndexWriter;
import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.dao.Site;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 01.12.2007
 * Time: 22:04:19
 */
public class CompoundIndexTest {
    public static void main(String[] args) {
        ZloIndexer zi = new ZloIndexer(Site.forName("zlo"));
        IndexWriter indexWriter = zi.getWriter();
        System.out.println(indexWriter.getUseCompoundFile());
        indexWriter.setUseCompoundFile(true);
        try {
            indexWriter.optimize();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
