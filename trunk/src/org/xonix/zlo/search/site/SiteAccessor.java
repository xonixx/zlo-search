package org.xonix.zlo.search.site;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;


/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 2:45:21
 */
public class SiteAccessor {

//    public static final String SITE_ACCESSOR_PREFIX = "site.accessor.";
    public static final String SITE_CONFIG_PREFIX = "site.config.";

    public String END_MSG_MARK_END;
    public String END_MSG_MARK_SIGN;
    public String MSG_NOT_EXIST_OR_WRONG;
    public String WITHOUT_TOPIC;

    // regexes
    public String MSG_REG_RE_STR;
    public String MSG_UNREG_RE_STR;
    public String INDEX_UNREG_RE_STR;

    public String SITE_URL;
    public String READ_QUERY;

    public String JNDI_DS_NAME;

    private String DB_DRIVER;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    public boolean DB_VIA_CONTAINER;

    private String siteName;

//    private SiteAccessor() {}

    public SiteAccessor(String siteName) {
//        try {
//            return (SiteAccessor) Class.forName(Config.getProp(SITE_ACCESSOR_PREFIX + siteName)).newInstance();

        Properties p = Config.loadProperties("org/xonix/zlo/search/config/" + Config.getProp(SITE_CONFIG_PREFIX + siteName));
//        SiteAccessor sa = new SiteAccessor();

        setSiteName(siteName);
        MSG_REG_RE_STR = p.getProperty("regex.msg.reg");
        //todo:...

        DB_DRIVER = p.getProperty("db.driver");
        DB_URL = p.getProperty("db.url");
        DB_USER = p.getProperty("db.user");
        DB_PASSWORD = p.getProperty("db.password");

        DB_VIA_CONTAINER = Config.TRUE.equals(p.getProperty("db.use.container.pull"));

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public ZloMessage getMessage(int num) throws IOException {
        return getParser().parseMessage(getRetriever().getPageContentByNumber(num), num);
    }

    private PageRetriever retreiver;
    private PageRetriever getRetriever() {
        if (retreiver == null) {
            retreiver = new PageRetriever(this);
        }
        return retreiver;
    }

    private PageParser parser;
    private PageParser getParser() {
        if (parser == null) {
            parser = new PageParser(this);
        }
        return parser;
    }

    public int getLastRootMessageNumber() throws IOException {
        return getRetriever().getLastRootMessageNumber(); 
    }


    public DataSource getDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(DB_DRIVER);
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds;
    }
}
