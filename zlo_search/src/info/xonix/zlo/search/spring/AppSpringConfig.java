package info.xonix.zlo.search.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Utility class for creating spring application contexts
 */
public final class AppSpringConfig {
    // this class is not for creation

    private AppSpringConfig() {
    }

    // for volatile word - see http://en.wikipedia.org/wiki/Double-checked_locking
    private static volatile ApplicationContext applicationContext;
    private static volatile ApplicationContext applicationContextTesting;

    /**
     * get "production" application context
     *
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            synchronized (AppSpringConfig.class) {
                if (applicationContext == null) {
                    applicationContext = new ClassPathXmlApplicationContext("beans.xml", AppSpringConfig.class);
                }
            }
        }
        return applicationContext;
    }

    /**
     * get "testing" application context
     *
     * @return application context
     */
    public static ApplicationContext getApplicationContextForTesting() {
        if (applicationContextTesting == null) {
            synchronized (AppSpringConfig.class) {
                if (applicationContextTesting == null) {
                    applicationContextTesting = new ClassPathXmlApplicationContext("beans-testing.xml", AppSpringConfig.class);
                }
            }
        }
        return applicationContextTesting;
    }

}
