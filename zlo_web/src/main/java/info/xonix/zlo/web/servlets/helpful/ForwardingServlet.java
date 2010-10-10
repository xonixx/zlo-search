package info.xonix.zlo.web.servlets.helpful;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 22:18:07
 */
public class ForwardingServlet extends ForwardingServletAbstract {

    protected void doGet(ForwardingRequest forwardingRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet((HttpServletRequest) forwardingRequest, httpServletResponse);
    }

    protected void doPost(ForwardingRequest forwardingRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost((HttpServletRequest) forwardingRequest, httpServletResponse);
    }
}
