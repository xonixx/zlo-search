package info.xonix.zlo.search.xmlfp.utils.jaxb;

import org.apache.log4j.Logger;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import javax.xml.transform.Source;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

/**
 * User: gubarkov
 * Date: 13.03.12
 * Time: 18:58
 */
public class SimpleResolver implements LSResourceResolver {
    private final static Logger logger = Logger.getLogger(SimpleResolver.class);
    private String xsdPath;

    public SimpleResolver(String xsdPath) {
        this.xsdPath = xsdPath;
    }


    @Override
    public LSInput resolveResource(String type, String namespaceURI,
                                   String publicId, String systemId, String baseURI) {

        DOMImplementationRegistry registry;

        try {
            registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
                    .getDOMImplementation("LS 3.0");

            LSInput ret = domImplementationLS.createLSInput();

            final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(xsdPath + systemId);

            if (inputStream != null) {
                ret.setSystemId(systemId);
                ret.setByteStream(inputStream);
            }

/*            for (Source source : streams) {
                SchemaSource schema = (SchemaSource) source;
                if (schema.getResourceName().equals(
                        schema.getResourceName(systemId))
                        & schema.getTargetNamespace().equals(namespaceURI)) {
                    logger.debug(
                            "Resolved systemid [{}] with namespace [{}]",
                            schema.getResourceName(systemId), namespaceURI);

                    URL url = new URL(schema.getSystemId());
                    URLConnection uc = url.openConnection();

                    ret.setByteStream(uc.getInputStream());
                    ret.setSystemId(systemId);
                    return ret;
                }
            }*/

        } catch (ClassCastException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
/*        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        */
        }

        logger.error("No stream found for system id " + systemId);
        return null;
    }

}