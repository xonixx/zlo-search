package info.xonix.zlo.search.db;

import java.io.Closeable;
import java.sql.*;

/**
 * Author: Vovan
 * Date: 15.01.2008
 * Time: 22:44:48
 */
@Deprecated
public class DbResult implements Closeable {
    private ResultSet resultSet;
    private Statement statement;
    private Connection connection;
//    private DbAccessor dbAccessor;

    public DbResult(Connection connection, ResultSet resultSet, Statement statement) {
        this.connection = connection;
        this.resultSet = resultSet;
        this.statement = statement;
    }

    public boolean next() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public int getOneInt() {
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

    public Integer getInt(int n) {
        try {
            return resultSet.getInt(n);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public String getString(int n) {
        try {
            return resultSet.getString(n);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Boolean getBoolean(int n) {
        try {
            return resultSet.getBoolean(n);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Timestamp getTimestamp(int n) {
        try {
            return resultSet.getTimestamp(n);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Object getObject(int n) {
        try {
            return resultSet.getObject(n);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // by columnName

    public Integer getInt(String s) {
        try {
            return resultSet.getInt(s);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public String getString(String s) {
        try {
            return resultSet.getString(s);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Boolean getBoolean(String s) {
        try {
            return resultSet.getBoolean(s);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Timestamp getTimestamp(String s) {
        try {
            return resultSet.getTimestamp(s);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public Object getObject(String s) {
        try {
            return resultSet.getObject(s);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

/*    public DbAccessor getDbAccessor() {
        return dbAccessor;
    }

    public void setDbAccessor(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }*/

    public void close() {
        CloseUtils.close(resultSet, statement);
        CloseUtils.close(connection);
    }
}
