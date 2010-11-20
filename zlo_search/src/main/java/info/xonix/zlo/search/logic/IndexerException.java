package info.xonix.zlo.search.logic;

/**
 * User: Vovan
 * Date: 09.07.2010
 * Time: 22:54:59
 */
public class IndexerException extends /*RuntimeException*/Exception {
    public IndexerException(String message) {
        super(message);
    }

    public IndexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexerException(Throwable cause) {
        super(cause);
    }
}
