package info.xonix.zlo.search.config;

import info.xonix.utils.ConfigUtils;
import info.xonix.utils.EnvUtils;
import info.xonix.utils.ExceptionUtils;
import info.xonix.zlo.search.analyzers.AnalyzerProvider;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.utils.SmartQueryParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;

import javax.annotation.Nullable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

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

    private Properties props;

    // app props
    private String environment;

    private int[] numsPerPage;

    private int buffer;
    private int retrieverThreadNum;

    private String userAgent;

    private boolean debug;
    private boolean searchPerformSort;

    private String websiteDomain;

    private Analyzer messageAnalyzer;

    private boolean useProxy;

    private String proxyHost;
    private int proxyPort;
    // end app props

    private String powerUserKey;

    private boolean startDaemons;
    private ResourceBundle messages;

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
        Analyzer analyzer;
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

        initNumsPerPage();

        buffer = Integer.parseInt(getProp("buffer", "512"));
        retrieverThreadNum = Integer.parseInt(getProp("retriever.threads"));

        userAgent = getProp("user.agent");

        debug = getBoolProp("debug");
        searchPerformSort = getBoolProp("search.perform.sort");

        websiteDomain = getProp("website.domain");

        useProxy = getBoolProp("proxy.use");

        proxyHost = useProxy ? getProp("proxy.host") : null;
        proxyPort = useProxy ? Integer.parseInt(getProp("proxy.port")) : -1;

        startDaemons = getBoolProp("daemons.start");

        initPowerUserPwd();
    }

    private void initNumsPerPage() {
        final String[] numsPerPageString = getProp("nums.per.page").split("\\|");
        numsPerPage = new int[numsPerPageString.length];
        for (int i = 0; i < numsPerPageString.length; i++) {
            numsPerPage[i] = Integer.parseInt(numsPerPageString[i]);
        }
    }

    private boolean getBoolProp(String key) {
        return isTrue(getProp(key));
    }

    public static boolean isTrue(String val) {
        return TRUE.equals(val) || TRUE1.equals(val);
    }

    public static void loadProperties(Properties pr, String path) {
        Properties properties = ConfigUtils.loadProperties(path);
        pr.putAll(properties);
    }

    /**
     * Should be defined in context.xml as follows:
     * <p/>
     * &lt;Environment name="powerUserKey" value="powerUserKey" type="java.lang.String"//>
     */
    private void initPowerUserPwd() {
        final Context context;
        try {
            context = loadEnvJndiCtx();
            powerUserKey = (String) context.lookup("powerUserKey");

            log.info("Loaded powerUserKey: " +
                    (powerUserKey != null && powerUserKey.length() > 0
                            ? powerUserKey.charAt(0) + "..."
                            : "empty"));
        } catch (NamingException e) {
            log.error("Can't get powerUserKey from JNDI, ex=" + e.toString());
        }
    }

    private Context loadEnvJndiCtx() throws NamingException {
        final Context initialContext = new InitialContext();
        return (Context) initialContext.lookup("java:comp/env");
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

    public String getProp(String key) {
        if (!props.containsKey(key)) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return props.getProperty(key);
    }

    public String getProp(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }

    // getters

    public String getEnvironment() {
        return environment;
    }

    public int[] getNumsPerPage() {
        return numsPerPage;
    }

    public int getBuffer() {
        return buffer;
    }

    public int getRetrieverThreadNum() {
        return retrieverThreadNum;
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

    /**
     * @return either String or null (power user not allowed)
     */
    @Nullable
    public String getPowerUserKey() {
        return powerUserKey;
    }

    public boolean isStartDaemons() {
        return startDaemons;
    }

    public String getIndexDir(String forumId) {
        return getProp("indexer.dir") + "/index_" + forumId;
    }

    public String message(String key) {
        if (messages == null) {
            messages = ResourceBundle.getBundle("info.xonix.zlo.web.i18n.messages");
        }
        return messages.getString(key);
    }
}
