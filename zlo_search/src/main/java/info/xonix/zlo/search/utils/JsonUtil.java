package info.xonix.zlo.search.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 4:36 PM
 */
public class JsonUtil {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
