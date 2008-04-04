package info.xonix.zlo.search.test;

import info.xonix.zlo.search.ZloSearcher;
import info.xonix.zlo.search.dao.Site;

import java.util.Random;
import java.io.IOException;

/**
 * Author: Vovan
 * Date: 21.12.2007
 * Time: 20:16:58
 */
public class SearchSpeedTest {
    public static void main(String[] args) throws IOException {
        ZloSearcher zs = Site.forName("zlo").getZloSearcher();
        String w = "жопа";
        int len = zs.search(-1, w, true, true, false, false, false, null, null).getHits().length();
        System.out.println(len);
        int N = 50;
        System.out.println("Start");
        Random r = new Random();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            zs.search(-1, w, true, true, false, false, false, null, null).getHits().doc(r.nextInt(len));
        }
        System.out.println("Avg: " + (System.currentTimeMillis() - t0) / N);
    }
}
