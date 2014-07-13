package info.xonix.zlo.web.listeners;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.daemons.DaemonLauncher;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: xonix
 * Date: 7/13/14
 * Time: 11:29 PM
 */
public class DaemonsStartingListener implements ServletContextListener {
    private final static Logger log = Logger.getLogger(DaemonsStartingListener.class);
    private final Config config = AppSpringContext.get(Config.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (config.isStartDaemons()) {
            log.info("Starting daemons...");
            DaemonLauncher.startAllActive();
        } else {
            log.info("Daemons not started.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
