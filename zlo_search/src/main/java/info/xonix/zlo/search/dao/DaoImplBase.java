package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.dao.rowmappers.RowMappersHelper;
import info.xonix.zlo.search.utils.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * User: gubarkov
 * Date: May 27, 2010
 * Time: 2:07:47 PM
 */
public abstract class DaoImplBase extends SimpleJdbcDaoSupport {
    @Autowired
    private RowMappersHelper rowMappersHelper;

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();

        Check.isSet(rowMappersHelper, "rowMappersHelper");
    }

    public RowMappersHelper getRowMappersHelper() {
        return rowMappersHelper;
    }
}
