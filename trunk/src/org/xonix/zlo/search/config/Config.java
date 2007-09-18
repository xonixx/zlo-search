package org.xonix.zlo.search.config;

import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Connection;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 */
public class Config {
    private static Properties props = new Properties();

    static {
        try {
            props.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("org/xonix/zlo/search/config/config.properties"));
        } catch (IOException e) {
            System.out.println("Can't load config!");
            e.printStackTrace();
        }
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
    public static final int TIME_PERIOD_YEARS = Integer.valueOf(TIME_PERIOD.split("y")[0]);
    public static final int TIME_PERIOD_MONTHS = Integer.valueOf(TIME_PERIOD.split("y")[1].split("m")[0]);

    public static String getProp(String key) {
        return props.getProperty(key);
    }

    public static String getProp(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }

    public static final Connection DB_CONNECTION;
    static {
        Connection _a = null;
        if (TRUE.equals(getProp("db.initialize"))) {
            try {
                 Class.forName(getProp("db.driver"));
                _a = DriverManager.getConnection(getProp("db.url"), getProp("db.user"), getProp("db.password"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DB_CONNECTION = _a;
    }
}
