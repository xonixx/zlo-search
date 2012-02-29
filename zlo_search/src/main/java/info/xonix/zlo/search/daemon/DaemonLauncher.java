package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 28.01.2008
 * Time: 18:18:59
 */
public class DaemonLauncher {
    private static final Logger log = Logger.getLogger(DaemonLauncher.class);

    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);

    public void main(String[] args) {
        for (String forumId : siteLogic.getSites()) {
            if (forumId.isPerformIndexing()) {
                log.info("Starting daemons for: " + forumId);
                startInNewThread(new DbDaemon(forumId));
                startInNewThread(new IndexerDaemon(forumId));
            } else {
                log.info("Not starting daemons for: " + forumId);
            }
        }
    }

    private static void startInNewThread(final Daemon d) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                d.start();
            }
        }, "Control thread for: " + d.describe());
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
}
