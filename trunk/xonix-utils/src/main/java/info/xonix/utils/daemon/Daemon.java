package info.xonix.utils.daemon;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 4:52 PM
 */
public interface Daemon {
    void perform();

    boolean processException(Exception ex);

    void cleanUp();
}
