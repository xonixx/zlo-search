package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.spring.AppSpringContext;

/**
 * Author: Vovan
 * Date: 28.01.2008
 * Time: 18:18:59
 */
public class DaemonLauncher {
    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);

    public void main(String[] args) {
        for (Site site : siteLogic.getSites()) {
            if (site.isPerformIndexing()) {
                startInNewThread(new DbDaemon(site));
                startInNewThread(new IndexerDaemon(site));
            }
        }
    }

    private static void startInNewThread(final Daemon d) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                d.start();
            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
}
