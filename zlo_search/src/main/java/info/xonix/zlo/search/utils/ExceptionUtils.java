package info.xonix.zlo.search.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * User: Vovan
 * Date: 21.11.2010
 * Time: 1:27:36
 */
public class ExceptionUtils {
    public static String getStackTrace(Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

    public static void rethrowAsRuntime(Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        } else if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        throw new RuntimeException(throwable);
    }
}
