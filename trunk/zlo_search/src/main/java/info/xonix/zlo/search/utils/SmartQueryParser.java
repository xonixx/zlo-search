package info.xonix.zlo.search.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.Utf8Appendable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * SmartQueryParser tries to parse query with UTF-8 first, if this fails - then {@link #encoding} is used.
 * It serves to handle URL urlencoded with legacy encoding.
 * <p/>
 * User: gubarkov
 * Date: 02.10.11
 * Time: 17:57
 */
public class SmartQueryParser {
    private final static Logger log = Logger.getLogger(SmartQueryParser.class);

    /**
     * this is replacement char, see {@link java.nio.charset.CharsetDecoder#CharsetDecoder(Charset, float, float)}
     */
    public static final String UNDECODABLE_CHAR = "\uFFFD";
    public static final String UTF_8 = "UTF-8";
    private final String encoding;

    public SmartQueryParser(String encoding) {
        this.encoding = encoding;
    }

    public String smartDecodeUrlencoded(String query) {
        for (String encoding : new String[]{UTF_8, this.encoding}) {
            try {
                String decoded = URLDecoder.decode(query, encoding);

                if (decoded.contains(UNDECODABLE_CHAR)) {
                    continue;
                }

                log.debug("decoded using enc: " + encoding);
                return decoded;

            } catch (UnsupportedEncodingException exc) {
                throw ExceptionUtils.rethrowAsRuntime(exc);
            }
        }

        log.error("Can't parse urlencoded: " + query);

        return null;
    }

    public MultiMap<String> parseUrlencodedParams(String query) {
        MultiMap<String> params = new MultiMap<String>();

        if (StringUtils.isEmpty(query)) {
            return params; // empty
        }

        try {
            UrlEncoded.decodeTo(query, params, UTF_8);
        } catch (Utf8Appendable.NotUtf8Exception ex) {
            UrlEncoded.decodeTo(query, params, encoding);
            log.info("Query is in " + encoding + ": " + query);
        }

        return params;
    }
}
