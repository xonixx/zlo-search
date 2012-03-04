package info.xonix.zlo.search.utils;

import java.io.*;
import java.util.Properties;

/**
 * User: gubarkov
 * Date: 04.03.12
 * Time: 18:17
 */
public class ConfigUtils {
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

    public static Properties loadProperties(String filePath, String propertiesDescription) {
        final InputStream inputStream = resolvePath(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid path: " + filePath);
        }

        Properties properties = new Properties();

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing " + propertiesDescription, e);
        }
        return properties;
    }
}
