package info.xonix.zlo.search.dao.rowmappers;

import info.xonix.utils.factory.Factory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:40:22
 */
public class RowMappersHelper {
    private Factory<Class, RowMapper> rowMappersCache = new Factory<Class, RowMapper>() {
        @Override
        @SuppressWarnings("unchecked")
        protected RowMapper create(Class aClass) {
            return BeanPropertyRowMapper.newInstance(aClass);
        }
    };

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> beanRowMapper(Class<T> clazz) {
        return rowMappersCache.get(clazz);
    }
}
