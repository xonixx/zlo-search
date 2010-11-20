package info.xonix.zlo.search.logic.site;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:12:43
 */
public abstract class SiteException extends Exception {
    public SiteException(String message) {
        super(message);
    }

    public SiteException(String message, Throwable cause) {
        super(message, cause);
    }

    public SiteException(Throwable cause) {
        super(cause);
    }
}
