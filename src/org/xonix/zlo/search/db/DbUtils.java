package org.xonix.zlo.search.db;

import org.xonix.zlo.search.config.Config;
import org.apache.log4j.Logger;

import java.sql.*;
import java.io.Closeable;

/**
 * Author: Vovan
 * Date: 18.11.2007
 * Time: 0:23:01
 */
public final class DbUtils {
    private static final Logger logger = Logger.getLogger(DbUtils.class);

    public static class Result implements Closeable {
        private ResultSet resultSet;
        private Statement statement;
        private Connection connection;

        public Result(Connection connection, ResultSet resultSet, Statement statement) {
            this.connection = connection;
            this.resultSet = resultSet;
            this.statement = statement;
        }

        public boolean next() throws DbException {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                throw new DbException(e);
            }
        }

        public int getOneInt() throws DbException {
            try {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (SQLException e) {
                throw new DbException(e);
            }
            return -1;
        }

        // by columnIndex
        public Integer getInt(int n) throws DbException {
            try { return resultSet.getInt(n); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public String getString(int n) throws DbException {
            try { return resultSet.getString(n); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Boolean getBoolean(int n) throws DbException {
            try { return resultSet.getBoolean(n); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Timestamp getTimestamp(int n) throws DbException {
            try { return resultSet.getTimestamp(n); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Object getObject(int n) throws DbException {
            try { return resultSet.getObject(n); }
            catch (SQLException e) { throw new DbException(e); }
        }

        // by columnName
        public Integer getInt(String s) throws DbException {
            try { return resultSet.getInt(s); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public String getString(String s) throws DbException {
            try { return resultSet.getString(s); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Boolean getBoolean(String s) throws DbException {
            try { return resultSet.getBoolean(s); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Timestamp getTimestamp(String s) throws DbException {
            try { return resultSet.getTimestamp(s); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public Object getObject(String s) throws DbException {
            try { return resultSet.getObject(s); }
            catch (SQLException e) { throw new DbException(e); }
        }

        public void close() {
            CloseUtils.close(resultSet, statement);
            if (Config.USE_CONTAINER_POOL) {
                CloseUtils.close(connection);
            }
        }
    }

    public static void setParams(PreparedStatement st, Object[] params, VarType[] types) throws DbException {
        if (params.length != types.length)
            throw new IllegalArgumentException("Number of params and types does not match");

        try {
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
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public static Result executeSelect(String sqlString, Object[] params, VarType[] types) throws DbException {
        PreparedStatement st;
        try {
            Connection connection = ConnectionUtils.getConnection();
            st = connection.prepareStatement(sqlString);
           setParams(st, params, types);
           return new Result(connection, st.executeQuery(), st);
        } catch (SQLException e) {
           throw new DbException(e);
        }
    }

    public static Result executeSelect(String sqlString) throws DbException {
        return executeSelect(sqlString, new Object[0], new VarType[0]);
    }

    /*
     Executes insert, update, delete
     */
    public static void executeUpdate(String sqlString, Object[] params, VarType[] types, Integer expectedResult) throws DbException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = ConnectionUtils.getConnection().prepareStatement(sqlString);

            setParams(st, params, types);

            int res = st.executeUpdate();
            if (expectedResult != null && res != expectedResult)
                throw new SQLException(String.format("Expected result: %s, actual result: %s", expectedResult, res));
        } catch (SQLException e) {
           throw new DbException(e);
        } finally {
            CloseUtils.close(st, rs);
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
}
