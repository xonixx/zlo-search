package info.xonix.zlo.search.config;

import info.xonix.zlo.search.model.MessageFields;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 * TODO: turn this to bean!!!
 */
public class Config {
    public static final String SITE_CONFIG_PREFIX = "site.config.";

    private static final Logger log = Logger.getLogger(Config.class);
    private static final String CONFIG_PATH_ENV_NAME = "ZLO_CONFIG";
    public final static String TRUE = "true";

    private Properties props;

    // app props
    private final String environment = getProp("env.name");

    private String[] numsPerPage = getProp("nums.per.page").split("\\|");

    private final int buffer = Integer.parseInt(getProp("buffer", "512"));
    private final int retrieverThreadNum = Integer.parseInt(getProp("retriever.threads"));

    // TODO: this should be site-specific
    private final String charsetName = "windows-1251";

    // todo: remove
//    public static final String INDEX_DIR = getProp("indexer.dir");
    private final String userAgent = getProp("user.agent");

    private final boolean debug = TRUE.equals(getProp("debug"));
    private final boolean searchPerformSort = TRUE.equals(getProp("search.perform.sort"));

    // todo:
//    private boolean USE_DOUBLE_INDEX = TRUE.equals(getProp("search.use.double.index"));

    private final int periodRecreateIndexer = TimeUtils.parseToMilliSeconds(getProp("searcher.period.recreate.indexer"));

    private final String websiteDomain = getProp("website.domain");

    private Analyzer analyzer;
    private Analyzer messageAnalyzer;

    private final boolean useProxy = TRUE.equals(getProp("proxy.use"));

    private final String proxyHost = useProxy ? getProp("proxy.host") : null;
    private final int proxyPort = useProxy ? Integer.parseInt(getProp("proxy.port")) : -1;
    // end app props

    public Config() {
        props = new Properties();
        if (!loadPropertiesFromEnv(props)) {
            System.out.println("Loading internal config...");
            loadProperties(props, "info/xonix/zlo/search/config/config.properties");

            String additionalConfigPath = props.getProperty("config.additional");
            if (additionalConfigPath != null) {
                loadProperties(props, additionalConfigPath);
            }
        }

        // configuring log4j
        PropertyConfigurator.configure(props);

        initAnalyzers();

        if (useProxy)
            log.info("Starting using proxy: " + proxyHost + ":" + proxyPort);
    }

    private void initAnalyzers() {
        try {
            analyzer = (Analyzer) Class.forName(getProp("analyzer")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PerFieldAnalyzerWrapper _messageAnalyzer = new PerFieldAnalyzerWrapper(new KeywordAnalyzer());
        _messageAnalyzer.addAnalyzer(MessageFields.TITLE, analyzer);
        _messageAnalyzer.addAnalyzer(MessageFields.BODY, analyzer);
        messageAnalyzer = _messageAnalyzer;
    }

    public static void loadProperties(Properties pr, String path) {
        System.out.println("Loading props from: " + path); // not through logger as logger maybe not yet inited
        try {
            pr.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path));
        } catch (IOException e) {
            log.fatal("Can't load config: " + path, e);
            e.printStackTrace();
        }
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
}
