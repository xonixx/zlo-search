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

    private static Connection DB_CONNECTION = null;

    /*
    Supposed to return valid connection
    if can't recreate one -> throws DbException
     */
    public static Connection getConnection() throws DbException {
        reopenConnectionIfNeeded();
        return DB_CONNECTION;
    }

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
                close(DB_CONNECTION);
                logger.info("Db connection closed, recreating...");
                DB_CONNECTION = createConnection();
            }
        } catch (DbException e) {
            logger.error("Problem with recreating connection: " + e.getClass());
            throw e;
        } finally {
            close(checkStmt);
        }
    }

    public static void setParams(PreparedStatement st, Object[] params, VarType[] types) throws SQLException {
        if (params.length != types.length)
            throw new IllegalArgumentException("Number of params and types does not match");

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            VarType type = types[i];
            int j = i+1;

            if (param == null) {
                st.setNull(j, type.getSqlType());
            } else {
                switch (type) {
                    case STRING:
                        st.setString(j, (String) param);
                        break;

                    case INTEGER:
                        st.setInt(j, (Integer) param);
                        break;

                    case BOOLEAN:
                        st.setBoolean(j, (Boolean) param);
                        break;

                    case DATE:
                        st.setTimestamp(j, new Timestamp(((java.util.Date) param).getTime()));
                        break;

                    default:
                        throw new IllegalArgumentException(
                                String.format("Unsupported parameter type: %s of parameter: %s",
                                        param.getClass(), param));
                }
            }
        }
    }

    public static Result executeSelect(String sqlString, Object[] params, VarType[] types) throws DbException {
        PreparedStatement st;
        try {
           st = getConnection().prepareStatement(sqlString);
           setParams(st, params, types);
           return new Result(st.executeQuery(), st);
        } catch (SQLException e) {
           throw new DbException(e);
        }
    }

    public static Result executeSelect(String sqlString) throws DbException {
        return executeSelect(sqlString, new Object[0], new VarType[0]);
    }

    /**
     * Executes insert, update, delete
     */
    public static void executeUpdate(String sqlString, Object[] params, VarType[] types, Integer expectedResult) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().prepareStatement(sqlString);

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

    public static void executeUpdate(String sqlString, Object[] params, VarType[] types) throws DbException {
        executeUpdate(sqlString, params, types, null);
    }

    public static Connection createConnection() throws DbException {
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
    }

    public static void clean() {
        close(DB_CONNECTION);
        DB_CONNECTION = null;
    }
}
