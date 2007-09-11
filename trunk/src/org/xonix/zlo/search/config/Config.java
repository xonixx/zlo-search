package org.xonix.zlo.search.config;

import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;
import java.util.Properties;

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

    public static String [] SITES = props.getProperty("sites").split("\\|");
    public static String [] NUMS_PER_PAGE = props.getProperty("nums.per.page").split("\\|");

    public static String INDEXING_URL = props.getProperty("indexing.url");
    public static String READ_QUERY = props.getProperty("query.read");
    public static final int BUFFER = Integer.parseInt(props.getProperty("buffer", "512"));

    public static final String CHARSET_NAME = "windows-1251";
    public static final String END_MSG_MARK = "<BIG>Сообщения в этом потоке</BIG>";
    public static final String INDEX_DIR = props.getProperty("index.dir");

    public static final Analyzer ANALYZER;
    static {
        Analyzer _a = null;
        try {
            _a = (Analyzer) Class.forName(props.getProperty("analyzer")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ANALYZER = _a;
    }

    private static final String TIME_PERIOD = props.getProperty("time.period");
    public static final int TIME_PERIOD_YEARS = Integer.valueOf(TIME_PERIOD.split("y")[0]);
    public static final int TIME_PERIOD_MONTHS = Integer.valueOf(TIME_PERIOD.split("y")[1].split("m")[0]);

    public static String getProp(String key) {
        return props.getProperty(key);
    }

    public static String getProp(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }
}
