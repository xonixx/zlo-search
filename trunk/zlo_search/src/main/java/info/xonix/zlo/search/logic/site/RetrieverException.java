package info.xonix.zlo.search.logic.site;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 23:44:51
 */
public class RetrieverException extends SiteException {
    public RetrieverException(String message) {
        super(message);
    }

    public RetrieverException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrieverException(Throwable cause) {
        super(cause);
    }
}
