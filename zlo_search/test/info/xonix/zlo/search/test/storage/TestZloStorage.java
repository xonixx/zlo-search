package storage;

import info.xonix.zlo.search.dao.DAOException;

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
        } catch (DAOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
