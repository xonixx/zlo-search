package info.xonix.zlo.search.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: Vovan
 * Date: 19.12.2007
 * Time: 16:06:50
 */
public class CloseUtils {
    public static void close(Object obj) {
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

    public static void close(Object... all) {
        for(Object obj : all) {
            close(obj);
        }
    }
}
