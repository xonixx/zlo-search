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

    public static class Result implements Closeable {
        private ResultSet resultSet;
        private Statement statement;

        public Result(ResultSet resultSet, Statement statement) {
            this.resultSet = resultSet;
            this.statement = statement;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public void close() {
            DbUtils.close(resultSet, statement);
        }
    }

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
            } else {
                throw new IllegalArgumentException(
                        String.format("Can't close object: %s of type: %s", obj, obj.getClass()));    
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

    private static void setParams(PreparedStatement st, Object[] params) throws SQLException {
        int i=1;
        for (Object param : params) {
            if (param instanceof String) {
                st.setString(i, (String)param);
            } else if (param instanceof Integer) {
                st.setInt(i, (Integer) param);
            } else if (param instanceof Boolean) {
                st.setBoolean(i, (Boolean) param);
            } else if (param instanceof Date) {
                st.setDate(i, (Date) param);
            } else if (param instanceof Timestamp) {
                st.setTimestamp(i, (Timestamp) param);
            } else if (param instanceof Time) {
                st.setTime(i, (Time) param);
            }
            else {
                throw new IllegalArgumentException(
                        String.format("Unsupported parameter type: %s of parameter: %s",
                                param.getClass(), param));
            }
            i++;
        }
    }

    public static Result executeSelect(String sqlString, Object[] params) throws DbException {
        PreparedStatement st = null;
        try {
           st = Config.getConnection().prepareStatement(sqlString);
           setParams(st, params);
           return new Result(st.executeQuery(), st);
        } catch (SQLException e) {
           throw new DbException(e);
        }
    }

    public static Result executeSelect(String sqlString) throws DbException {
        return executeSelect(sqlString, new Object[0]);
    }

    /**
     * Executes insert, update, delete
     */
    public static void executeUpdate(String sqlString, Object[] params, Integer expectedResult) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = Config.getConnection().prepareStatement(sqlString);

            setParams(st, params);

            int res = st.executeUpdate();
            if (expectedResult != null && res != expectedResult)
                throw new SQLException(String.format("Expected result: %s, actual result: %s", expectedResult, res));
        } catch (SQLException e) {
           throw new DbException(e);
        } finally {
            DbUtils.close(st, rs);
        }
    }

    public static void executeUpdate(String sqlString, Object[] params) throws DbException {
        executeUpdate(sqlString, params, null);
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
