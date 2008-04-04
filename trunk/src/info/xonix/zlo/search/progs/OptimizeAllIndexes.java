package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.dao.Site;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 24.03.2008
 * Time: 0:53:31
 */
public class OptimizeAllIndexes {
    public static void main(String[] args) throws IOException {
        for (Site site : Site.getSites()){
            if (site.isPERFORM_INDEXING()) {
                System.out.println("-----" + site.getSiteName() + "-----");
                OptimizeIndex.optimizeDoubleIndexForSite(site);
            }
        }
    }
}
