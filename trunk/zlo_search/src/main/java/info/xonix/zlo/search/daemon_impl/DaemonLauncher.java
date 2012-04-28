package info.xonix.zlo.search.daemon_impl;

import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.daemon.Daemon;
import info.xonix.zlo.search.daemon.DaemonManager;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 28.01.2008
 * Time: 18:18:59
 */
public class DaemonLauncher {
    private static final Logger log = Logger.getLogger(DaemonLauncher.class);
    private static final DaemonManager daemonManager = AppSpringContext.get(DaemonManager.class);

    public void main(String[] args) {
        for (String forumId : GetForum.ids()) {
            if (GetForum.params(forumId).isPerformIndexing()) {
                log.info("Starting daemons for: " + forumId);

                daemonManager.startDaemon(new DbDaemon(forumId));
                daemonManager.startDaemon(new IndexerDaemon(forumId));

//                startInNewThread(new DbDaemon(forumId));
//                startInNewThread(new IndexerDaemon(forumId));
            } else {
                log.info("Not starting daemons for: " + forumId);
            }
        }
    }

/*    private static void startInNewThread(final Daemon d) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                d.doOnStart();
            }
        }, "Control thread for: " + d.describe());
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }*/
}
