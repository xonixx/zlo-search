package info.xonix.zlo.search.db;

import java.sql.Timestamp;

/**
 * Author: Vovan
 * Date: 18.11.2007
 * Time: 0:23:01
 */
@Deprecated
public final class DbUtils {
//    private static final Logger logger = Logger.getLogger(DbUtils.class);

    public static Timestamp timestamp(java.util.Date date) {
        return new Timestamp(date.getTime());
    }

/*    public static void setParams(PreparedStatement st, Object[] params, VarType[] types) {
        if (params.length != types.length)
            throw new IllegalArgumentException("Number of params and types does not match");

        try {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                VarType type = types[i];
                int j = i + 1;

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

    //======================================

    private static DbResult executeSelect(Connection connection, String sqlString, Object[] params, VarType[] types) {
        PreparedStatement st;
        try {
            st = connection.prepareStatement(sqlString);
            setParams(st, params, types);
            return new DbResult(connection, st.executeQuery(), st);
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

*//*    private static DbResult executeSelect(String jndiDsName, String sqlString, Object[] params, VarType[] types) {
        try {
            return executeSelect(ConnectionUtils.getConnection(jndiDsName), sqlString, params, types);
        } catch (NamingException e) {
            throw new DbException(e);
        }
    }*//*

    public static DbResult executeSelect(DataSource ds, String sqlString, Object[] params, VarType[] types) {
        return executeSelect(ConnectionUtils.getConnection(ds), sqlString, params, types);
    }

*//*    public static DbResult executeSelect(DbAccessor dbAccessor, String sqlString, Object[] params, VarType[] types) {
        DbResult dbResult;
        dbResult = executeSelect(dbAccessor.getDataSource(), sqlString, params, types);
        dbResult.setDbAccessor(dbAccessor);
        return dbResult;
    }

    //--------------------------------------
    private static DbResult executeSelect(String jndiDsName, String sqlString) {
        return executeSelect(jndiDsName, sqlString, new Object[0], new VarType[0]);
    }  *//*

    public static DbResult executeSelect(DataSource ds, String sqlString) {
        return executeSelect(ds, sqlString, new Object[0], new VarType[0]);
    }
    *//*
    public static DbResult executeSelect(DbAccessor dbAccessor, String sqlString) {
        DbResult dbResult;
        dbResult = executeSelect(dbAccessor.getDataSource(), sqlString, new Object[0], new VarType[0]);
        dbResult.setDbAccessor(dbAccessor);
        return dbResult;
    }*//*
    *//*
     Executes insert, update, delete
     *//*
    //======================================

    private static void executeUpdate(Connection connection, String sqlString, Object[] params, VarType[] types, Integer expectedResult) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sqlString);

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

*//*    private static void executeUpdate(String jndiDsName, String sqlString, Object[] params, VarType[] types, Integer expectedResult) {
        try {
            Connection con = ConnectionUtils.getConnection(jndiDsName);
            executeUpdate(con, sqlString, params, types, expectedResult);
            CloseUtils.close(con); // return to pool
        } catch (NamingException e) {
            throw new DbException(e);
        }
    } *//*

    public static void executeUpdate(DataSource ds, String sqlString, Object[] params, VarType[] types, Integer expectedResult) {
        executeUpdate(ConnectionUtils.getConnection(ds), sqlString, params, types, expectedResult);
    }
    *//*
    public static void executeUpdate(DbAccessor dbAccessor, String sqlString, Object[] params, VarType[] types, Integer expectedResult) {
        executeUpdate(dbAccessor.getDataSource(), sqlString, params, types, expectedResult);
    }*//*
    //--------------------------------------
*//*    private static void executeUpdate(String jndiDsName, String sqlString, Object[] params, VarType[] types) {
        executeUpdate(jndiDsName, sqlString, params, types, null);
    }

    private static void executeUpdate(DataSource ds, String sqlString, Object[] params, VarType[] types) {
        executeUpdate(ds, sqlString, params, types, null);
    }
*//*

    public static void executeUpdate(DataSource dataSource, String sqlString, Object[] params, VarType[] types) {
        executeUpdate(dataSource, sqlString, params, types, null);
    }*/
}
