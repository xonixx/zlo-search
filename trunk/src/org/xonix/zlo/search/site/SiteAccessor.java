package org.xonix.zlo.search.site;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.TimeUtils;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 2:45:21
 */
public class SiteAccessor {

    public static final String SITE_CONFIG_PREFIX = "site.config.";

    public String MARK_END_MSG_1;
    public String MARK_END_MSG_2;
    public String MSG_NOT_EXIST_OR_WRONG;
    public String WITHOUT_TOPIC;

    // regexes
    public String MSG_REG_RE_STR;
    public String MSG_UNREG_RE_STR;
    public String LINK_INDEX_REGEX;

    public String SITE_URL;
    public String READ_QUERY;

    public String JNDI_DS_NAME;

    private String DB_DRIVER;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    public boolean DB_VIA_CONTAINER;

    // index
    public boolean PERFORM_INDEXING;
    public String INDEX_DIR_DOUBLE;

    public int INDEXER_INDEX_PER_TIME;
    public int INDEXER_INDEX_PERIOD;
    public int INDEXER_RECONNECT_PERIOD;

    // db daemon
    public int DB_SCAN_PER_TIME;
    public int DB_SCAN_PERIOD;
    public int DB_RECONNECT_PERIOD;

    public int SITE_NUMBER;

    private String SITE_NAME;
    private String siteName;


    public SiteAccessor(String siteName) {
        Properties p = new Properties();

        for (String propFile : Config.getProp(SITE_CONFIG_PREFIX + siteName).split(";")) {
            Config.loadProperties(p, "org/xonix/zlo/search/config/" + propFile);
        }

        setSiteName(siteName);

        MARK_END_MSG_1 = p.getProperty("str.mark.end.1");
        MARK_END_MSG_2 = p.getProperty("str.mark.end.2");

        MSG_NOT_EXIST_OR_WRONG = p.getProperty("str.msg.not.exists");
        WITHOUT_TOPIC = p.getProperty("str.without.topic");

        MSG_REG_RE_STR = p.getProperty("regex.msg.reg");
        MSG_UNREG_RE_STR = p.getProperty("regex.msg.unreg");

        LINK_INDEX_REGEX = p.getProperty("regex.link.index");

        SITE_URL = p.getProperty("site.url");
        READ_QUERY = p.getProperty("site.read.query");
        // db -----
        JNDI_DS_NAME = p.getProperty("db.jndi.ds.name");

        DB_DRIVER = p.getProperty("db.driver");
        DB_URL = p.getProperty("db.url");
        DB_USER = p.getProperty("db.user");
        DB_PASSWORD = p.getProperty("db.password");

        DB_VIA_CONTAINER = Config.TRUE.equals(p.getProperty("db.use.container.pull"));

        // indexer-----
        PERFORM_INDEXING = Config.TRUE.equals(p.getProperty("indexer.perform.indexing"));
        INDEX_DIR_DOUBLE = p.getProperty("indexer.dir.double");

        INDEXER_INDEX_PER_TIME = Integer.parseInt(p.getProperty("indexer.daemon.index.per.time"));
        INDEXER_INDEX_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.index"));
        INDEXER_RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.reconnect"));

        // db daemon-----
        DB_SCAN_PER_TIME = Integer.parseInt(p.getProperty("db.daemon.scan.per.time"));
        DB_SCAN_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.scan"));
        DB_RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.reconnect"));

        SITE_NUMBER = Integer.parseInt(p.getProperty("site.number"));
        SITE_NAME = p.getProperty("site.name");
    }

    public String getSiteName() {
        // this is for the reason not to create havy objects for same sites (zlo for board)
        return SITE_NAME != null ? SITE_NAME : siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    private DriverManagerDataSource ds;
    public DataSource getDataSource() {
        if (ds == null) {
            ds = new DriverManagerDataSource();
            ds.setDriverClassName(DB_DRIVER);
            ds.setUrl(DB_URL);
            ds.setUsername(DB_USER);
            ds.setPassword(DB_PASSWORD);
        }
        return ds;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SiteAccessor)) {
            return false;
        }

        return StringUtils.equals(getSiteName(), ((SiteAccessor) obj).getSiteName()) &&
                StringUtils.equals(SITE_NAME, ((SiteAccessor) obj).SITE_NAME);
    }
}
