package org.xonix.zlo.search.test.storage;

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
            System.out.println(zs.getMessageByNumber(5000000));
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
