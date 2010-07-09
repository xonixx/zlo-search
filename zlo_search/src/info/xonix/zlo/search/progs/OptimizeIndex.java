package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.ZloSearcher;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 26.11.2007
 * Time: 19:27:49
 */
public class OptimizeIndex {
    public static final Logger log = Logger.getLogger(OptimizeIndex.class);

    private ZloSearcher zloSearcher = AppSpringContext.get(ZloSearcher.class);

    public static void main(String[] args) {
        new OptimizeIndex().main();
    }

    public void main() {
        Site site = Site.forName(Config.getSiteEnvName());
        if (!Config.USE_DOUBLE_INDEX) {
            throw new IllegalArgumentException("Not supported!");
            /*IndexWriter w = new IndexerLogicImpl(site).getWriter();
            log.info("Optimizing index...");
            w.optimize();
            w.close();
            log.info("Done.");*/
        } else {
            optimizeDoubleIndexForSite(site);
        }
    }

    public void optimizeDoubleIndexForSite(Site site) {
        log.info("Optimizing index for " + site.getName());

        zloSearcher.optimizeIndex(site);
    }
}
