package info.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.site.Nameable;
import info.xonix.zlo.search.dao.DB;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

/**
 * Author: Vovan
 * Date: 11.02.2008
 * Time: 18:18:05
 */
public class DbAccessor implements Nameable {

    private static final Logger logger = Logger.getLogger(DbAccessor.class);

    private String JNDI_DS_NAME;
    private String DB_DRIVER;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private boolean DB_VIA_CONTAINER;
    
    private DataSource ds;

    private String name;

    public DbAccessor() {
    }

    private DbAccessor(String configFileName) {
        Properties p = new Properties();
        if (!configFileName.endsWith(".properties"))
            configFileName += ".properties";
        Config.loadProperties(p, "info/xonix/zlo/search/config/" + configFileName);
        initDb(p);
    }

    private static HashMap<String, DbAccessor> dbAccessors = new HashMap<String, DbAccessor>();

    public static DbAccessor getInstance(String configFileName) {
        if (!dbAccessors.containsKey(configFileName)) {
            dbAccessors.put(configFileName, new DbAccessor(configFileName));
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

    public boolean isDB_VIA_CONTAINER() {
        return DB_VIA_CONTAINER;
    }

    private DbManager dbManager;
    public DbManager getDbManager() {
        if (dbManager == null) {
            dbManager = new DbManager(this);
        }
        return dbManager;
    }

    private DbDict dbDict;
    public DbDict getDbDict() {
        if (dbDict == null) {
            dbDict = new DbDict(this);
        }
        return dbDict;
    }

    private DB db;
    public DB getDB() {
        if (db == null) {
            db = new DB(this);
        }
        return db;
    }

    public DataSource getDataSource() {
        if (ds == null) {
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
        return ds;
    }

    public void setJNDI_DS_NAME(String JNDI_DS_NAME) {
        this.JNDI_DS_NAME = JNDI_DS_NAME;
    }

    public void setDB_DRIVER(String DB_DRIVER) {
        this.DB_DRIVER = DB_DRIVER;
    }

    public void setDB_URL(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    public void setDB_USER(String DB_USER) {
        this.DB_USER = DB_USER;
    }

    public void setDB_PASSWORD(String DB_PASSWORD) {
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void setDB_VIA_CONTAINER(boolean DB_VIA_CONTAINER) {
        this.DB_VIA_CONTAINER = DB_VIA_CONTAINER;
    }

    protected void initDb(Properties p) {
        setJNDI_DS_NAME(p.getProperty("db.jndi.ds.name"));

        setDB_DRIVER(p.getProperty("db.driver"));
        setDB_URL(p.getProperty("db.url"));
        setDB_USER(p.getProperty("db.user"));
        setDB_PASSWORD(p.getProperty("db.password"));

        setDB_VIA_CONTAINER(Config.TRUE.equals(p.getProperty("db.use.container.pull")));
    }
}
