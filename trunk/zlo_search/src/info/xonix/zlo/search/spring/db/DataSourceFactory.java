package info.xonix.zlo.search.spring.db;

import info.xonix.zlo.search.config.Config;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

/**
 * User: Vovan
 * Date: 06.06.2010
 * Time: 19:40:35
 */
public class DataSourceFactory implements FactoryBean<DataSource> {
    private BasicDataSource dataSourceLocal;
    private BasicDataSource dataSourceRt;

    @Override
    public DataSource getObject() throws Exception {
        String env = Config.ENVIRONMENT;
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

    public void setDataSourceLocal(BasicDataSource dataSourceLocal) {
        this.dataSourceLocal = dataSourceLocal;
    }

    public BasicDataSource getDataSourceLocal() {
        return dataSourceLocal;
    }

    public void setDataSourceRt(BasicDataSource dataSourceRt) {
        this.dataSourceRt = dataSourceRt;
    }

    public BasicDataSource getDataSourceRt() {
        return dataSourceRt;
    }
}
