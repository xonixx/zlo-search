package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.daemons.DaemonLauncher;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * User: Vovan
 * Date: 03.09.2009
 * Time: 0:53:55
 */
public class DaemonsStartingServlet extends GenericServlet {
    private final static Logger log = Logger.getLogger(DaemonsStartingServlet.class);

    private Config config = AppSpringContext.get(Config.class);

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        ;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if (config.isStartDaemons()) {
            log.info("Starting daemons...");
            new DaemonLauncher().main(new String[0]);
        } else {
            log.info("Daemons not started.");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
