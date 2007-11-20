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

        public int getInt1() throws DbException {
            try {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (SQLException e) {
                throw new DbException(e);
            }
            return -1;
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
            logger.error("Problem with recreating connection: ", e);
            throw new DbException(e);
        } finally {
            close(checkStmt);
        }
    }

    private static void setParams(PreparedStatement st, Object[] params, int[] types) throws SQLException {
        if (params.length != types.length)
            throw new IllegalArgumentException("Number of params and types does not match");

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int type = types[i];
            int j = i+1;

            switch (type) {
                case Types.VARCHAR:
                case Types.CHAR:
                    st.setString(j, (String) param);
                    break;

                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                case Types.NUMERIC:
                    st.setInt(j, (Integer) param);
                    break;

                case Types.BOOLEAN:
                    st.setBoolean(j, (Boolean) param);
                    break;

                case Types.DATE:
                    st.setDate(j, (Date) param);
                    break;

                case Types.TIMESTAMP:
                    st.setTimestamp(j, (Timestamp) param);
                    break;

                case Types.TIME:
                    st.setTime(j, (Time) param);
                    break;

                default:
                    throw new IllegalArgumentException(
                            String.format("Unsupported parameter type: %s of parameter: %s",
                                    param.getClass(), param));
            }
        }
    }

    public static Result executeSelect(String sqlString, Object[] params, int[] types) throws DbException {
        PreparedStatement st;
        try {
           st = Config.getConnection().prepareStatement(sqlString);
           setParams(st, params, types);
           return new Result(st.executeQuery(), st);
        } catch (SQLException e) {
           throw new DbException(e);
        }
    }

    public static Result executeSelect(String sqlString) throws DbException {
        return executeSelect(sqlString, new Object[0], new int[0]);
    }

    /**
     * Executes insert, update, delete
     */
    public static void executeUpdate(String sqlString, Object[] params, int[] types, Integer expectedResult) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = Config.getConnection().prepareStatement(sqlString);

            setParams(st, params, types);

            int res = st.executeUpdate();
            if (expectedResult != null && res != expectedResult)
                throw new SQLException(String.format("Expected result: %s, actual result: %s", expectedResult, res));
        } catch (SQLException e) {
           throw new DbException(e);
        } finally {
            DbUtils.close(st, rs);
        }
    }

    public static void executeUpdate(String sqlString, Object[] params, int[] types) throws DbException {
        executeUpdate(sqlString, params, types, null);
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
                logger.warn("Starting without DB because of exception: ", e);
            }
        } else {
            logger.info("Starting without db because of config...");
        }
        return _conn;
    }
}