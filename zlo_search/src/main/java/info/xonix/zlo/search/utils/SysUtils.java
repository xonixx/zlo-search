package info.xonix.zlo.search.utils;

import org.apache.log4j.Logger;

/**
 * User: Vovan
 * Date: 23.03.11
 * Time: 18:29
 */
public final class SysUtils {
    private final static Logger log = Logger.getLogger(SysUtils.class);

    public static void gc() {
        log.info("Going to GC...");

        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();

        log.info("GC done...");
    }
}
