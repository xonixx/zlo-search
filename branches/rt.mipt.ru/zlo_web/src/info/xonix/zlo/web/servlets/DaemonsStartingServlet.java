package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.daemon.DaemonLauncher;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: Vovan
 * Date: 03.09.2009
 * Time: 0:53:55
 */
public class DaemonsStartingServlet extends GenericServlet {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        ;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if ("1".equals(getInitParameter("startDaemons"))) {
            log("Starting daemons...");
            DaemonLauncher.main(new String[0]);
        } else {
            log("Daemons not started.");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
