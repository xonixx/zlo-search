package info.xonix.zlo.search.db;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DB;
import info.xonix.zlo.search.dao.DbDictImpl;
import info.xonix.zlo.search.dao.DbManagerImpl;
import info.xonix.zlo.search.site.Nameable;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

/**
 * TODO: refactor this!!!
 * Author: Vovan
 * Date: 11.02.2008
 * Time: 18:18:05
 */
@Deprecated
public class DbAccessorKillMe implements Nameable {

    private static final Logger logger = Logger.getLogger(DbAccessorKillMe.class);

    private String JNDI_DS_NAME;
    private String DB_DRIVER;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

    private DataSource ds;

    private String name;

    public DbAccessorKillMe() {
    }

    private DbAccessorKillMe(String configFileName) {
        Properties p = new Properties();
        if (!configFileName.endsWith(".properties"))
            configFileName += ".properties";
        Config.loadProperties(p, "info/xonix/zlo/search/config/" + configFileName);
        initDb(p);
    }

    private static HashMap<String, DbAccessorKillMe> dbAccessors = new HashMap<String, DbAccessorKillMe>();

    public static DbAccessorKillMe getInstance(String configFileName) {
        if (!dbAccessors.containsKey(configFileName)) {
            dbAccessors.put(configFileName, new DbAccessorKillMe(configFileName));
        }
        return dbAccessors.get(configFileName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJNDI_DS_NAME() {
        return JNDI_DS_NAME;
    }

    public String getDB_DRIVER() {
        return DB_DRIVER;
    }

    public String getDB_URL() {
        return DB_URL;
    }

    public String getDB_USER() {
        return DB_USER;
    }

    public String getDB_PASSWORD() {
        return DB_PASSWORD;
    }

    private DbManagerImpl dbManager;

    public DbManagerImpl getDbManager() {
        if (dbManager == null) {
//            dbManager = new DbManagerImpl(this);
        }
        return dbManager;
    }

    private DbDictImpl dbDict;

    public DbDictImpl getDbDict() {
        if (dbDict == null) {
//            dbDict = new DbDictImpl(this);
        }
        return dbDict;
    }

    private DB db;

    public DB getDB() {
        if (db == null) {
//            db = new DB(this);
        }
        return db;
    }

    public DataSource getDataSource() {
/*        if (ds == null) {
            if (isDB_VIA_CONTAINER()) {
                try {
                    ds = ConnectionUtils.getDataSource(getJNDI_DS_NAME());
                } catch (NamingException e) {
                    logger.error(e);
                }
            }
            if (ds == null) {
                DriverManagerDataSource ds = new DriverManagerDataSource();
                ds.setDriverClassName(getDB_DRIVER());
                ds.setUrl(getDB_URL());
                ds.setUsername(getDB_USER());
                ds.setPassword(getDB_PASSWORD());
                this.ds = ds;
            }
        }
        return ds;*/
        return AppSpringContext.getApplicationContext().getBean("dataSource", DataSource.class);
    }

    private void setJNDI_DS_NAME(String JNDI_DS_NAME) {
        this.JNDI_DS_NAME = JNDI_DS_NAME;
    }

    private void setDB_DRIVER(String DB_DRIVER) {
        this.DB_DRIVER = DB_DRIVER;
    }

    private void setDB_URL(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    private void setDB_USER(String DB_USER) {
        this.DB_USER = DB_USER;
    }

    private void setDB_PASSWORD(String DB_PASSWORD) {
        this.DB_PASSWORD = DB_PASSWORD;
    }

    protected void initDb(Properties p) {
        setJNDI_DS_NAME(Config.getProp("db.jndi.ds.name"));

        setDB_DRIVER(Config.getProp("db.driver"));
        setDB_URL(Config.getProp("db.url"));
        setDB_USER(Config.getProp("db.user"));
        setDB_PASSWORD(Config.getProp("db.password"));
    }
}
