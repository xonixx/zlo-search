package info.xonix.zlo.search.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Utility class for creating spring application contexts
 */
public final class AppSpringContext implements ApplicationContextAware {
    // this class is not for creation

    private AppSpringContext() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppSpringContext.applicationContext = applicationContext;
    }

    // for volatile word - see http://en.wikipedia.org/wiki/Double-checked_locking
    private static volatile ApplicationContext applicationContext;
    private static volatile ApplicationContext applicationContextTesting;

    private static boolean initializing = false;

    /**
     * get "production" application context
     *
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        if (initializing) {
            throw new RuntimeException("Already in process of initialization!");
        }

        if (applicationContext == null) {
            synchronized (AppSpringContext.class) {
                if (applicationContext == null) {
                    initializing = true;
                    applicationContext = new ClassPathXmlApplicationContext("all_beans.xml", AppSpringContext.class);
                    initializing = false;
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
