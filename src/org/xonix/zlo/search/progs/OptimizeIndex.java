package org.xonix.zlo.search.progs;

import org.xonix.zlo.search.ZloIndexer;
import org.xonix.zlo.search.DAO;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 26.11.2007
 * Time: 19:27:49
 */
public class OptimizeIndex {
    public static void main(String[] args) {
        IndexWriter w = new ZloIndexer(DAO.DB.SOURCE).getWriter();
        try {
            System.out.println("Optimizing index...");
            w.optimize();
            w.close();
            System.out.println("Done.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Done with error.");
        }
    }
}
