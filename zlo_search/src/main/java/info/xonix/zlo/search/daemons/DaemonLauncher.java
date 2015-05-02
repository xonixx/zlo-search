package info.xonix.zlo.search.daemons;

import info.xonix.utils.daemon.DaemonManager;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.daemons.impl.ChartsDaemon;
import info.xonix.zlo.search.daemons.impl.DownloaderDaemon;
import info.xonix.zlo.search.daemons.impl.IndexerDaemon;
import info.xonix.zlo.search.daemons.impl.RefetchParentsDaemon;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 28.01.2008
 * Time: 18:18:59
 */
public class DaemonLauncher {
    private static final Logger log = Logger.getLogger(DaemonLauncher.class);
    public static final String ENV_DONT_START_DAEMONS = "dontStartDaemons";

    private static final DaemonManager daemonManager = AppSpringContext.get(DaemonManager.class);

    public static void startAllActive() {
        if ("1".equals(System.getenv("nodaemons"))) {
            log.info("Not starting download/indexer daemons.");
        } else {
            startDownloadAndIndexerDaemons();
            daemonManager.startDaemon(new RefetchParentsDaemon("zlo"));
        }

        daemonManager.startDaemon(new ChartsDaemon());
    }

    private static void startDownloadAndIndexerDaemons() {
        String dontStartDaemonsForumIds = System.getenv(ENV_DONT_START_DAEMONS);

        for (String forumId : GetForum.ids()) {
            if (dontStartDaemonsForumIds != null && dontStartDaemonsForumIds.contains(forumId)) {
                log.info("Not starting daemon by env: " + forumId);
                continue;
            }

            if (!GetForum.descriptor(forumId).isDead()) {
                log.info("Starting daemons for: " + forumId);

                daemonManager.startDaemon(new DownloaderDaemon(forumId));
                daemonManager.startDaemon(new IndexerDaemon(forumId));
            } else {
                log.info("Not starting daemons for dead forum: " + forumId);
            }
        }
    }

    public static void shutdownAll() {
        daemonManager.shutdownAll(true);
    }
}
