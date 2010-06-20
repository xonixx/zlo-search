package info.xonix.zlo.search.db;

import info.xonix.zlo.search.dao.DAOException;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 14:32:12
 */
@Deprecated
public class DbException extends DAOException {
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbException(Throwable cause) {
        super(cause);
    }
}
