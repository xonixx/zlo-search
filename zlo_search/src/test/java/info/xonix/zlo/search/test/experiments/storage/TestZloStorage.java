package info.xonix.zlo.search.test.experiments.storage;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 20:25:56
 */
public class TestZloStorage {
    public static void main(String[] args) {
        ZloStorage zs = new ZloStorage();
        System.out.println(zs.getMessageByNumber(5000000));
        System.exit(0);
    }
}
