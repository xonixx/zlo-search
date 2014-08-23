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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            if (AppSpringContext.get(Config.class).isStartDaemons()) {
                log.info("Starting daemons...");
                DaemonLauncher.startAllActive();
            } else {
                log.info("Daemons not started.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
