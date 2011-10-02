package info.xonix.zlo.search.utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * User: gubarkov
 * Date: 02.10.11
 * Time: 17:57
 */
public class SmartQueryParser {
    private final static Logger log = Logger.getLogger(SmartQueryParser.class);

    /**
     * this is replacement char, see {@link java.nio.charset.CharsetDecoder#CharsetDecoder(Charset,float,float)}
     */
    public static final String UNDECODABLE_CHAR = "\uFFFD";
    private final String[] encodings;

    public SmartQueryParser(String... encodings) {
        this.encodings = encodings;
    }

    public String parseUrlencoded(String query) {
        for (String encoding : encodings) {
            Charset charset = Charset.forName(encoding);
            CharsetDecoder charsetDecoder = charset.newDecoder();
            String decoded = null;
            try {
                decoded = URLDecoder.decode(query, encoding);

                if (decoded.contains(UNDECODABLE_CHAR)) {
                    continue;
                }

                log.debug("decoded using enc: " + encoding);
            } catch (UnsupportedEncodingException exc) {
                ExceptionUtils.rethrowAsRuntime(exc);
            }
            return decoded;
        }

        log.error("Can't parse urlencoded: " + query);

        return null;
    }


}
