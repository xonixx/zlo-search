package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.SearchLogic;
import info.xonix.zlo.search.logic.SearchLogicImpl;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 26.11.2007
 * Time: 19:27:49
 */
public class OptimizeIndex {
    public static final Logger log = Logger.getLogger(OptimizeIndex.class);

    private SearchLogic searchLogic = AppSpringContext.get(SearchLogicImpl.class);

    public static void main(String[] args) {
        new OptimizeIndex().main();
    }

    public void main() {
        Site site = Site.forName(Config.getSiteEnvName());

        optimizeDoubleIndexForSite(site);
    }

    public void optimizeDoubleIndexForSite(Site site) {
        log.info("Optimizing index for " + site.getName());

        searchLogic.optimizeIndex(site);
    }
}
