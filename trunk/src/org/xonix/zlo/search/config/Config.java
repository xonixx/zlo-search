package org.xonix.zlo.search.config;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;
import java.io.FileReader;
import java.util.Properties;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 */
public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
    private static final Properties props;
    private static final String CONFIG_PATH_ENV_NAME = "ZLO_CONFIG";

    public static void loadProperties(Properties pr, String path) {
        try {
            pr.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path));
        } catch (IOException e) {
            logger.fatal("Can't load config: " + path, e);
            e.printStackTrace();
        }
    }

    public static Properties loadProperties(String path) {
        Properties pr = new Properties();
        loadProperties(pr, path);
        return pr;
    }

    public static Properties getAppProperties() {
        return props;
    }

    private static boolean loadPropertiesFromEnv(Properties pr) {
        String envPath = System.getenv(CONFIG_PATH_ENV_NAME);
        if (envPath != null) {
            System.out.println("Loading config from: " + envPath + " ...");
            try {
                pr.load(new FileReader(envPath));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            return true;
        } else {
            return false;
        }
    }

    static {
        props = new Properties();
        if (!loadPropertiesFromEnv(props)) {
            System.out.println("Loading internal config...");
            loadProperties(props, "org/xonix/zlo/search/config/config.properties");
        }
    }

    // configuring log4j
    static {
        PropertyConfigurator.configure(props);
    }

    public static String [] NUMS_PER_PAGE = getProp("nums.per.page").split("\\|");

    public static final int BUFFER = Integer.parseInt(getProp("buffer", "512"));

    public static final String CHARSET_NAME = "windows-1251";

    // todo: remove
    public static final String INDEX_DIR = getProp("indexer.dir");
    public static final String USER_AGENT = getProp("user.agent");
    public static final String TRUE = "true";

    public static final boolean DEBUG = TRUE.equals(getProp("debug"));
    public static final boolean SEARCH_PERFORM_SORT = TRUE.equals(getProp("search.perform.sort"));
    public static boolean USE_DOUBLE_INDEX = TRUE.equals(getProp("search.use.double.index"));

    public static final Analyzer ANALYZER;
    static {
        Analyzer _a = null;
        try {
            _a = (Analyzer) Class.forName(getProp("analyzer")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ANALYZER = _a;
    }

    private static final String TIME_PERIOD = getProp("time.period");
    public static final int TIME_PERIOD_YEARS = Integer.parseInt(TIME_PERIOD.split("y")[0]);
    public static final int TIME_PERIOD_MONTHS = Integer.parseInt(TIME_PERIOD.split("y")[1].split("m")[0]);

    public static String getProp(String key) {
        return props.getProperty(key);
    }

    public static String getProp(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }
}
