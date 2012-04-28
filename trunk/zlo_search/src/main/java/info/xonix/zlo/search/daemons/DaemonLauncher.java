package info.xonix.zlo.search.daemons;

import info.xonix.utils.daemon.DaemonManager;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.daemons.impl.DownloaderDaemon;
import info.xonix.zlo.search.daemons.impl.IndexerDaemon;
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

                daemonManager.startDaemon(new DownloaderDaemon(forumId));
                daemonManager.startDaemon(new IndexerDaemon(forumId));
            } else {
                log.info("Not starting daemons for: " + forumId);
            }
        }
    }
}
