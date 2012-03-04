package info.xonix.zlo.search.config;

import info.xonix.zlo.search.analyzers.AnalyzerProvider;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.utils.EnvUtils;
import info.xonix.zlo.search.utils.ExceptionUtils;
import info.xonix.zlo.search.utils.SmartQueryParser;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;

import java.io.*;
import java.util.Properties;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 */
public class Config {
    private static final Logger log = Logger.getLogger(Config.class);
    private static final String CONFIG_PATH_ENV_NAME = "ZLO_CONFIG";

    private final static String TRUE = "true";
    private final static String TRUE1 = "1";

    public static final String UTF_8 = "UTF-8";
    public static final String WINDOWS_1251 = "windows-1251";

    public final static SmartQueryParser SMART_QUERY_PARSER = new SmartQueryParser(WINDOWS_1251);

    public static final String FORUMS_CONF_PATH = "info/xonix/zlo/search/config/forums/";
    public static final String FORUMS_CONF_DEAD_PATH = FORUMS_CONF_PATH + "dead/";

    private Properties props;

    // app props
    private String environment;

    private String[] numsPerPage;

    private int buffer;
    private int retrieverThreadNum;

    // TODO: this should be site-specific
    private String charsetName;

    private String userAgent;

    private boolean debug;
    private boolean searchPerformSort;

    private int periodRecreateIndexer;

    private String websiteDomain;

    private Analyzer analyzer;
    private Analyzer messageAnalyzer;

    private boolean useProxy;

    private String proxyHost;
    private int proxyPort;
    // end app props

    private String powerUserKey;

    private boolean startDaemons;

    public Config() {
        props = new Properties();
        if (!loadPropertiesFromEnv(props)) {
            System.out.println("Loading internal config...");
            loadProperties(props, "info/xonix/zlo/search/config/config.properties");

            final String additionalConfName = "config.additional." + EnvUtils.getHostName();
            final String additionalConfigPath = props.getProperty(additionalConfName);

            if (additionalConfigPath != null) {
                loadProperties(props, additionalConfigPath);
            } else {
                throw new IllegalStateException("Can't get additional config path: " + additionalConfName);
            }
        }

        // configuring log4j
        PropertyConfigurator.configure(props);

        initAnalyzers();
        initOtherProps();

        if (useProxy) {
            log.info("Starting using proxy: " + proxyHost + ":" + proxyPort);
        }
    }

    private void initAnalyzers() {
        try {
            final Class<?> clazz = Class.forName(getProp("analyzer"));

            if (AnalyzerProvider.class.isAssignableFrom(clazz)) {
                final AnalyzerProvider analyzerProvider = (AnalyzerProvider) clazz.newInstance();
                analyzer = analyzerProvider.getAnalyzer();
            } else {
                analyzer = (Analyzer) clazz.newInstance();
            }
        } catch (Exception e) {
            log.error("Can't create analyzer", e);
            throw ExceptionUtils.rethrowAsRuntime(e);
        }

        final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(new KeywordAnalyzer());
        perFieldAnalyzerWrapper.addAnalyzer(MessageFields.TITLE, analyzer);
        perFieldAnalyzerWrapper.addAnalyzer(MessageFields.BODY, analyzer);
        messageAnalyzer = perFieldAnalyzerWrapper;

        log.info("Got analyzer: " + analyzer.getClass().getName());
    }

    private void initOtherProps() {
        environment = getProp("env.name");

        numsPerPage = getProp("nums.per.page").split("\\|");

        buffer = Integer.parseInt(getProp("buffer", "512"));
        retrieverThreadNum = Integer.parseInt(getProp("retriever.threads"));

        // TODO: this should be site-specific
        charsetName = "windows-1251";

        userAgent = getProp("user.agent");

        debug = getBoolProp("debug");
        searchPerformSort = getBoolProp("search.perform.sort");

        periodRecreateIndexer = TimeUtils.parseToMilliSeconds(getProp("searcher.period.recreate.indexer"));

        websiteDomain = getProp("website.domain");

        useProxy = getBoolProp("proxy.use");

        proxyHost = useProxy ? getProp("proxy.host") : null;
        proxyPort = useProxy ? Integer.parseInt(getProp("proxy.port")) : -1;

        powerUserKey = getProp("powerUserKey");

        startDaemons = getBoolProp("daemons.start");
    }

    private boolean getBoolProp(String key) {
        return isTrue(getProp(key));
    }

    public static boolean isTrue(String val) {
        return TRUE.equals(val) || TRUE1.equals(val);
    }

    @Deprecated
    public static boolean loadProperties(Properties pr, String path) {
        System.out.println("Loading props from: " + path); // not through logger as logger maybe not yet inited
        try {
            final InputStream resourceAsStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path);

            if (resourceAsStream == null) {
                System.out.println("\t-> not found");
                return false;
            } else {
                System.out.println("\t-> found");
            }

            pr.load(resourceAsStream);

        } catch (IOException e) {
            throw new RuntimeException("Can't load config: " + path, e);
        }

        return true;
    }

    public static Properties loadProperties(String path) {
        Properties pr = new Properties();
        loadProperties(pr, path);
        return pr;
    }

    public Properties getAppProperties() {
        return props;
    }

    private boolean loadPropertiesFromEnv(Properties pr) {
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

// todo: del?
//    private static final String TIME_PERIOD = getProp("time.period");
//    public static final int TIME_PERIOD_YEARS = Integer.parseInt(TIME_PERIOD.split("y")[0]);
//    public static final int TIME_PERIOD_MONTHS = Integer.parseInt(TIME_PERIOD.split("y")[1].split("m")[0]);

    public String getProp(String key) {
        if (!props.containsKey(key)) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return props.getProperty(key);
    }

    public String getProp(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }

    public static String getSiteEnvName() {
        String sn = System.getenv("SITE_NAME");
        if (sn == null) {
            log.error("Must set SITE_NAME environment variable!");
            System.exit(-1);
            return null;
        } else {
            return sn;
        }
    }

    // getters

    public String getEnvironment() {
        return environment;
    }

    public String[] getNumsPerPage() {
        return numsPerPage;
    }

    public int getBuffer() {
        return buffer;
    }

    public int getRetrieverThreadNum() {
        return retrieverThreadNum;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isSearchPerformSort() {
        return searchPerformSort;
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    // commenting for now
/*    public Analyzer getAnalyzer() {
        return analyzer;
    }*/

    public Analyzer getMessageAnalyzer() {
        return messageAnalyzer;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public int getPeriodRecreateIndexer() {
        return periodRecreateIndexer;
    }

    public String getPowerUserKey() {
        return powerUserKey;
    }

    public boolean isStartDaemons() {
        return startDaemons;
    }

    public String getIndexDirDouble(String forumId) {
        return getProp("indexer.dir.double") + "/index_" + forumId;
    }
}
