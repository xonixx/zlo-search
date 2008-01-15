package org.xonix.zlo.search.db;

import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.site.SiteSource;

import java.io.Closeable;
import java.sql.*;

/**
 * Author: Vovan
* Date: 15.01.2008
* Time: 22:44:48
*/
public class DbResult extends SiteSource implements Closeable {
    private ResultSet resultSet;
    private Statement statement;
    private Connection connection;

    public DbResult(Connection connection, ResultSet resultSet, Statement statement) {
        super((Site)null);
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
        if (getSite().DB_VIA_CONTAINER) {
            CloseUtils.close(connection);
        }
    }
}
