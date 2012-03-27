package info.xonix.forumsearch.xmlfp;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 0:17
 */
public class XmlFpException extends Exception {
    public XmlFpException(Exception e) {
        super(e);
    }

    public XmlFpException(Exception e, String s) {
        super(s, e);
    }
}
