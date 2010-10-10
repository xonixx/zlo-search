package info.xonix.zlo.web.servlets.helpful;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 20:58:31
 */
public abstract class ForwardingServletAbstract extends HttpServlet {
    public static final String GET = "GET";
    public static final String POST = "POST";

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException {
        ForwardingRequest request = new ForwardingRequest(httpServletRequest);
        request.setServlet(this);
        request.setResponse(response);
        if (request.getMethod().equalsIgnoreCase(GET)) {
            doGet(request, response);
        } else if (request.getMethod().equalsIgnoreCase(POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(ForwardingRequest forwardingRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException;

    protected abstract void doPost(ForwardingRequest forwardingRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException;
}
