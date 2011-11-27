package info.xonix.zlo.web.cxf;

import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;

/**
 * This servlet's purpose is to retrieve cxf bus from already created spring context
 * not create new one Bus or Spring context, as standard CXFServlet is doing
 * <p/>
 * User: gubarkov
 * Date: 27.11.11
 * Time: 17:39
 */
public class CXFServlet extends CXFNonSpringServlet {
    @Override
    protected void loadBus(ServletConfig sc) {
        setBus(AppSpringContext.get("cxf", Bus.class));
    }
}
