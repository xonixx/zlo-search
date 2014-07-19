package info.xonix.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * User: gubarkov
 * Date: 04.03.12
 * Time: 18:17
 */
public class ConfigUtils {
    private static final Logger log = Logger.getLogger(ConfigUtils.class);

    /**
     * @param filePath file path OR classpath to resource
     * @return input stream or null
     */
    public static InputStream resolvePath(String filePath) {
        final File file = new File(filePath);

        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
    }

    public static Properties loadProperties(String filePath) {
        return loadProperties(filePath, filePath);
    }

    public static Properties loadProperties(String filePath, String propertiesDescription) {
        log.info("Loading prop file: " + filePath);

        final InputStream inputStream = resolvePath(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid path: " + filePath);
        }

        Properties properties = new Properties();

        try {
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error initializing " + propertiesDescription, e);
        }
        return properties;
    }
}
