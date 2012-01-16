package info.xonix.zlo.search;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Администратор
 * Date: 1/8/12
 * Time: 1:33 AM
 */
public class VersionManager {
    private final static Logger log = Logger.getLogger(VersionManager.class.getName());

    public static final Properties VERSION_PROPERTIES = new Properties();
    public static final String UNKNOWN = "UNKNOWN";

    public static final String VERSION_FILE = "info/xonix/zlo/search/version.properties";

    static {
        try {
            VERSION_PROPERTIES.load(VersionManager.class.getClassLoader().getResourceAsStream(VERSION_FILE));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Can't read project version", e);
        }
    }

    public static String getApplicationVersion() {
        return VERSION_PROPERTIES.getProperty("pom", UNKNOWN) +
                "r" + VERSION_PROPERTIES.getProperty("revision", UNKNOWN);
    }
}
