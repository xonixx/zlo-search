package info.xonix.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: xonix
 * Date: 06.12.14
 * Time: 19:41
 */
public class Util {
    public static void removeNullValues(Map<?,?> map) {
        List keysToRm = new ArrayList();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                keysToRm.add(entry.getValue());
            }
        }
        for (Object k : keysToRm) {
            map.remove(k);
        }
    }

    public static String showDurationAndRate(long startTimestamp, int number) {
        float durationSecs = (System.currentTimeMillis() - startTimestamp) / 1000f;
        return "in " + (int) durationSecs + "secs. Rate: " + ((float) number) / durationSecs + "mps.";
    }
}
