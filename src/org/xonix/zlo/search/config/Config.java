package org.xonix.zlo.search.config;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.xonix.zlo.search.db.DbUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 */
public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);
    private static final Properties props = loadProperties("org/xonix/zlo/search/config/config.properties");

    public static Properties loadProperties(String path) {
        Properties pr = new Properties();
        try {
            pr.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path));
        } catch (IOException e) {
            logger.fatal("Can't load config: " + path, e);
            e.printStackTrace();
        }
        return pr;
    }

    // configuring log4j
    static {
        PropertyConfigurator.configure(props);
    }

    public static String [] SITES = getProp("sites").split("\\|");
    public static String [] NUMS_PER_PAGE = getProp("nums.per.page").split("\\|");

    public static String INDEXING_URL = getProp("indexing.url");
    public static String READ_QUERY = getProp("query.read");
    public static final int BUFFER = Integer.parseInt(getProp("buffer", "512"));

    public static final String CHARSET_NAME = "windows-1251";
    public static final String INDEX_DIR = getProp("index.dir");
    public static final String USER_AGENT = getProp("user.agent");
    public static final int THREADS_NUMBER = Integer.parseInt(getProp("threads"));
    public static final String TRUE = "true";

    public static final boolean DEBUG = TRUE.equals(getProp("debug"));

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

    private static Connection DB_CONNECTION = DbUtils.createConnection();

    public static Connection getConnection() throws SQLException {
        if (DB_CONNECTION == null) {
            throw new SQLException("Connection is null");
        }
        return DB_CONNECTION;
    }

    public static void setConnection(Connection con) {
        DB_CONNECTION = con;        
    }
}
