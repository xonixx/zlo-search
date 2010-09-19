package info.xonix.zlo.search.spring.db;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

/**
 * User: Vovan
 * Date: 06.06.2010
 * Time: 19:40:35
 */
public class DataSourceFactory implements FactoryBean<DataSource>, InitializingBean {
    private static final Logger log = Logger.getLogger(DataSourceFactory.class);

    private Config config;
    private BasicDataSource dataSourceLocal;
    private BasicDataSource dataSourceRt;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config);
        Check.isSet(dataSourceLocal);
        Check.isSet(dataSourceRt);
    }

    @Override
    public DataSource getObject() throws Exception {
        String env = config.getEnvironment();

        log.info("Using datasource for env=" + env);

        if ("rt".equals(env)) {
            return dataSourceRt;
        } else if ("local".equals(env)) {
            return dataSourceLocal;
        }

        throw new IllegalArgumentException("Can't determine db settings for environment: " + env);
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setDataSourceLocal(BasicDataSource dataSourceLocal) {
        this.dataSourceLocal = dataSourceLocal;
    }

/*    public BasicDataSource getDataSourceLocal() {
        return dataSourceLocal;
    }*/

    public void setDataSourceRt(BasicDataSource dataSourceRt) {
        this.dataSourceRt = dataSourceRt;
    }

/*    public BasicDataSource getDataSourceRt() {
        return dataSourceRt;
    }*/
}
