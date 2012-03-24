package info.xonix.zlo.search.xmlfp.utils.jaxb;

import org.apache.log4j.Logger;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

/**
 * User: gubarkov
 * Date: 13.03.12
 * Time: 18:58
 */
public class SimpleResolver implements LSResourceResolver {
    private final static Logger logger = Logger.getLogger(SimpleResolver.class);
    private String xsdPath;

    private final static DOMImplementationRegistry domImplementationRegistry;

    static {
        try {
            domImplementationRegistry = DOMImplementationRegistry.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public SimpleResolver(String xsdPath) {
        this.xsdPath = xsdPath;
    }


    @Override
    public LSInput resolveResource(String type, String namespaceURI,
                                   String publicId, String systemId, String baseURI) {

        DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementationRegistry
                .getDOMImplementation("LS 3.0");

        LSInput ret = domImplementationLS.createLSInput();

        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(xsdPath + systemId);

        if (inputStream != null) {
            ret.setSystemId(systemId);
            ret.setByteStream(inputStream);
            return ret;
        }

        logger.error("No stream found for system id " + systemId);
        return null;
    }

}