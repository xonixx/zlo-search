package info.xonix.zlo.search.logic.exceptions;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 0:48:24
 */
public interface ExceptionsLogger {
    void logException(Throwable throwable, String msg, String source, ExceptionCategory category);

    void logException(Throwable throwable, String msg, Class sourceCls, ExceptionCategory category);

    void logException(Throwable throwable, String msg, ExceptionCategory category);

    void logException(Throwable throwable);
}
