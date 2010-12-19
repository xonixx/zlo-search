package info.xonix.zlo.web.ws;

/**
 * User: Vovan
 * Date: 20.12.10
 * Time: 0:09
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
