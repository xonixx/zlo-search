package info.xonix.zlo.search.db;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author: Vovan
 * Date: 19.12.2007
 * Time: 15:58:59
 */
public class ConnectionUtils {
    private static final Logger logger = Logger.getLogger(ConnectionUtils.class);

    static final String SQL_SELECT_CHECK_ALIVE = "SELECT 1;";
    static Connection DB_CONNECTION = null;

    /*
    Supposed to return valid connection
    if can't recreate one -> throws DbException
     */
/*    public static Connection getConnection() throws DbException {
*//*        if (Config.USE_CONTAINER_POOL) {
            try {
                DataSource connection = getDataSource();
                if (connection != null)
                    return connection.getConnection();
            } catch (SQLException e) {
                logger.error(e);
                throw new DbException(e);
            } catch (NamingException e) {
                logger.error("Error while creating DataSource: " + e.getClass().getName() + ", Starting using single db connection!");
            }
        }*//*

        // if not container pool or not in container environment
        reopenConnectionIfNeeded();
        return DB_CONNECTION;
    }*/

/*    public static Connection getConnection(String jndiDsName) throws DbException, NamingException {
//        if (jndiDsName == null)
//            return getConnection();

        DataSource ds = getDataSource(jndiDsName);
        return ds != null ? getConnection(ds) : null;
    }*/

    @Deprecated
    public static Connection getConnection(DataSource ds) throws DbException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // todo: tmp
/*    private static DataSource getDataSource() throws NamingException {
        return getDataSource("java:comp/env/jdbc/zlo_storage");
    }*/

//    private static DataSource dataSource = null;
/*    private static HashMap<String, DataSource> dsHashMap = new HashMap<String, DataSource>();

    public static DataSource getDataSource(String jndiDsName) throws NamingException {
        if (!dsHashMap.containsKey(jndiDsName)) {
            InitialContext ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup(jndiDsName);
            dsHashMap.put(jndiDsName, dataSource);
            return dataSource;
        } else {
            return dsHashMap.get(jndiDsName);
        }
    }

    private static void reopenConnectionIfNeeded() throws DbException {
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
                DB_CONNECTION = createConnection();
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

    private static Connection createConnection() throws DbException {
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
                logger.warn("Connection not created because of exception: " + e.getClass());
                throw new DbException(e);
            }
        } else {
            logger.info("Starting without db because of config...");
        }
        return _conn;
    }*/
}
