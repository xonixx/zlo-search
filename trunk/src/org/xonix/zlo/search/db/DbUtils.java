package org.xonix.zlo.search.db;

import org.xonix.zlo.search.config.Config;
import org.apache.log4j.Logger;

import java.sql.*;
import java.io.Closeable;
import java.io.IOException;

/**
 * Author: Vovan
 * Date: 18.11.2007
 * Time: 0:23:01
 */
public final class DbUtils {
    private static final Logger logger = Logger.getLogger(DbUtils.class);
    private static final String SQL_SELECT_CHECK_ALIVE = "SELECT 1;";

    static void close(Object obj) {
        if (obj == null)
            return;

        try {
            if (obj instanceof Statement) {
                ((Statement) obj).close();
            } else if (obj instanceof ResultSet) {
                ((ResultSet) obj).close();
            } else if (obj instanceof Closeable) {
                ((Closeable) obj).close();
            } else if (obj instanceof Connection) {
                ((Connection) obj).close();
            }
        } catch (SQLException e) {
            ;
        } catch(IOException e) {
            ;
        }
    }

    static void close(Object... all) {
        for(Object obj : all) {
            close(obj);
        }
    }

    public static void reopenConnectionIfNeeded() throws DbException {
        Statement checkStmt = null;
        try {
            Connection con = null;
            boolean closed;
            try {
                con = Config.getConnection();
                closed = con.isClosed();
            } catch (SQLException e) {
                closed = true;
            }

            if (!closed) {
                checkStmt = Config.getConnection().createStatement();

                try {
                    checkStmt.executeQuery(SQL_SELECT_CHECK_ALIVE);
                } catch(SQLException e) {
                    closed = true;
                }
            }

            if (closed) {
                close(con);
                logger.info("Db connection closed, recreating...");
                Config.setConnection(createConnection());
                Config.getConnection();
            }
        } catch (SQLException e) {
            logger.error("Problem with recreating connection: " + e);
            throw new DbException(e);
        } finally {
            close(checkStmt);
        }
    }

    /**
     * Executes insert or update
     */
    public static void executeDML() {

    }

    public static Connection createConnection() {
        Connection _conn = null;
        logger.info("Creating db connection...");

        if (Config.TRUE.equals(Config.getProp("db.initialize"))) {
            try {
                 Class.forName(Config.getProp("db.driver"));
                _conn = DriverManager.getConnection(Config.getProp("db.url"), Config.getProp("db.user"), Config.getProp("db.password"));
            } catch (Exception e) {
                if (e instanceof ClassNotFoundException) {
                    logger.error("Can't load MySQL drivers...");
                } else if (e instanceof SQLException) {
                    logger.error("Can't connect to DB...");
                }
                logger.warn("Starting without DB because of exception: " + e.getClass());
            }
        } else {
            logger.info("Starting without db because of config...");
        }
        return _conn;
    }
}
