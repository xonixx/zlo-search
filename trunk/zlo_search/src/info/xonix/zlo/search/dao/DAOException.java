package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.IndexingSource;

import java.io.IOException;

/**
 * Author: Vovan
* Date: 11.01.2008
* Time: 22:30:31
*/
public class DAOException extends IOException {
    private IndexingSource source;

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(IndexingSource source, Throwable cause) {
        super(cause);
        this.source = source;
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexingSource getSource() {
        return source;
    }
}
