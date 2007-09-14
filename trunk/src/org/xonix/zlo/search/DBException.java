package org.xonix.zlo.search;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 14:32:12
 */
public class DBException extends Exception {
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(Throwable cause) {
        super(cause);
    }
}
