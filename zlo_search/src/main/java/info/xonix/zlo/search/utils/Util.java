package info.xonix.zlo.search.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xonix
 * Date: 29.11.15
 * Time: 22:48
 */
final public class Util {
    public static List<Long> toLongsList(int[] ids) {
        List<Long> res = new ArrayList<Long>(ids.length);
        for (int id : ids) {
            res.add((long)id);
        }
        return res;
    }

    private Util() {
    }
}
