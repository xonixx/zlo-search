package info.xonix.zlo.search.logic;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 22:54:37
 */
public class SiteAccessException extends RuntimeException {
    public SiteAccessException(String message) {
        super(message);
    }

    public SiteAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SiteAccessException(Throwable cause) {
        super(cause);
    }
}
