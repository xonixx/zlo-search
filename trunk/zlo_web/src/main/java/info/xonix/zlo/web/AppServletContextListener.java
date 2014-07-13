package info.xonix.zlo.web;

import info.xonix.zlo.search.spring.AppSpringContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: xonix
 * Date: 7/6/14
 * Time: 3:20 AM
 */
public class AppServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            AppSpringContext.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
