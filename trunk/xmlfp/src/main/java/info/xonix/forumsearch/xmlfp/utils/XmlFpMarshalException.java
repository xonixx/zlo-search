package info.xonix.forumsearch.xmlfp.utils;

/**
 * User: gubarkov
 * Date: 11.02.12
 * Time: 22:11
 */
public class XmlFpMarshalException extends Exception {
    public XmlFpMarshalException(Exception exception, String message) {
        super(message, exception);
    }
}
