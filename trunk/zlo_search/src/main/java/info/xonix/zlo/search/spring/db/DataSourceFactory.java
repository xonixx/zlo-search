package info.xonix.zlo.search.spring.db;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * User: Vovan
 * Date: 06.06.2010
 * Time: 19:40:35
 */
public class DataSourceFactory implements FactoryBean<DataSource>, InitializingBean {
    private static final Logger log = Logger.getLogger(DataSourceFactory.class);

    @Autowired
    private Config config;

    private DataSource dataSourceLocal;
    private DataSource dataSourceRt;

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

    public void setDataSourceLocal(DataSource dataSourceLocal) {
        this.dataSourceLocal = dataSourceLocal;
    }

    public void setDataSourceRt(DataSource dataSourceRt) {
        this.dataSourceRt = dataSourceRt;
    }
}
