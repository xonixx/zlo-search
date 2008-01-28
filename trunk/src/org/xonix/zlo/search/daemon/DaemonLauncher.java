package org.xonix.zlo.search.daemon;

import org.xonix.zlo.search.dao.Site;

/**
 * Author: Vovan
 * Date: 28.01.2008
 * Time: 18:18:59
 */
public class DaemonLauncher {
    public static void main(String[] args) {
        for (Site site : Site.getSites()) {
            if (site.PERFORM_INDEXING) {
                startInNewThread(new DbDaemon(site));
                startInNewThread(new IndexerDaemon(site));
            }
        }
    }

    private static void startInNewThread(final Daemon d) {
        Thread t = new Thread(new Runnable(){
            public void run() {
                d.start();
            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
}
