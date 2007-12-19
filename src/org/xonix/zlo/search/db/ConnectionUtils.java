package org.xonix.zlo.search.db;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.config.Config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Author: Vovan
 * Date: 19.12.2007
 * Time: 15:58:59
 */
public class ConnectionUtils {
    private static final Logger logger = Logger.getLogger(ConnectionUtils.class);

    private static boolean USE_CONTAINER_POOL = Config.TRUE.equals(Config.getProp("db.use.container.pull"));

    static final String SQL_SELECT_CHECK_ALIVE = "SELECT 1;";
    static Connection DB_CONNECTION = null;

    /*
    Supposed to return valid connection
    if can't recreate one -> throws DbException
     */
    public static Connection getConnection() throws DbException {
        if (USE_CONTAINER_POOL) {
            try {
                return getDataSource().getConnection();
            } catch (SQLException e) {
                logger.error(e);
                throw new DbException(e);
            }
        } else {
            reopenConnectionIfNeeded();
            return DB_CONNECTION;
        }
    }

    private static DataSource dataSource = null;
    private static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                InitialContext ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/zlo_storage");
            } catch (NamingException e) {
                logger.error("Error while creating DataSource: ", e);
            }
        }
        return dataSource;
    }

    static void reopenConnectionIfNeeded() throws DbException {
        Statement checkStmt = null;
        try {
            boolean closed;
            try {
                closed = DB_CONNECTION == null || DB_CONNECTION.isClosed();
            } catch (SQLException e) {
                closed = true;
            }

            if (!closed) {
                try {
                    checkStmt = DB_CONNECTION.createStatement();
                    checkStmt.executeQuery(SQL_SELECT_CHECK_ALIVE);
                } catch(SQLException e) {
                    closed = true;
                }
            }

            if (closed) {
                CloseUtils.close(DB_CONNECTION);
                logger.info("Db connection closed, recreating...");
                DB_CONNECTION = DbUtils.createConnection();
            }
        } catch (DbException e) {
            logger.error("Problem with recreating connection: " + e.getClass());
            throw e;
        } finally {
            CloseUtils.close(checkStmt);
        }
    }

    public static void clean() {
        CloseUtils.close(DB_CONNECTION);
        DB_CONNECTION = null;
    }
}
