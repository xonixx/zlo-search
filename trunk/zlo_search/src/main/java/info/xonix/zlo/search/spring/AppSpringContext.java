package info.xonix.zlo.search.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Utility class for creating spring application contexts
 */
public final class AppSpringContext {
    // this class is not for creation

    private AppSpringContext() {
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
            synchronized (AppSpringContext.class) {
                if (applicationContext == null) {
                    applicationContext = new ClassPathXmlApplicationContext("beans.xml", AppSpringContext.class);
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
            synchronized (AppSpringContext.class) {
                if (applicationContextTesting == null) {
                    applicationContextTesting = new ClassPathXmlApplicationContext("beans-testing.xml", AppSpringContext.class);
                }
            }
        }
        return applicationContextTesting;
    }

    public static <T> T get(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T get(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static Object get(String name) {
        return getApplicationContext().getBean(name);
    }
}
