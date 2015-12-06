package info.xonix.zlo.search.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: xonix
 * Date: 06.12.15
 * Time: 23:08
 */
final public class IOUtil {
    /**
     * Returns list of strings from a stream, where string is any not blank
     * and not comment(starting with #) line.
     *
     * @param inputStream UTF-8 stream
     * @return list of strings
     */
    public static List<String> loadStrings(InputStream inputStream) {
        try {
            String fileContent = IOUtils.toString(inputStream, "UTF-8");
            List<String> strings = Arrays.asList(fileContent.split("\n"));
            List<String> resStrings = new ArrayList<String>();

            // rm blank & comments
            for (String string : strings) {
                string = string.trim();

                if (StringUtils.isNotBlank(string) && !string.startsWith("#"))
                    resStrings.add(string);
            }

            return resStrings;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private IOUtil() {
    }
}
