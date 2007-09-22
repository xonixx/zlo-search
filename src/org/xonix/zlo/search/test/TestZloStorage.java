package org.xonix.zlo.search.test;

import org.xonix.zlo.search.test.storage.ZloStorage;
import org.xonix.zlo.search.DAO;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 20:25:56
 */
public class TestZloStorage {
    public static void main(String[] args) {
        ZloStorage zs = new ZloStorage();
        try {
            System.out.println(zs.getMessageByNumber(3975000));
        } catch (DAO.Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
