package info.xonix.forumsearch.xmlfp;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 16:08
 */
public class XmlFpIOException extends XmlFpException {
    public XmlFpIOException(Exception e) {
        super(e);
    }

    public XmlFpIOException(Exception e, String s) {
        super(e, s);
    }
}
