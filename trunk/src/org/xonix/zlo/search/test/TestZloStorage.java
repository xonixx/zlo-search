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
            System.out.println("3974701>" + zs.getMessages(3964700, 3964710));
        } catch (DAO.Exception e) {
            e.printStackTrace();
        }
    }
}
