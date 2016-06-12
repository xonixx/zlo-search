package info.xonix.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * User: Vovan
 * Date: 26.11.2010
 * Time: 20:30:53
 */
public final class EnvUtils {
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEnvId() {
        return System.getenv("env");
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }
}
