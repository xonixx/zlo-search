package org.xonix.zlo.web.servlets.helpful;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 21:22:43
 */
public class ForwardingRequest extends HttpServletRequestWrapper {
    private ForwardingServletAbstract servlet;
    private HttpServletResponse response;

    public ForwardingRequest(HttpServletRequest request) {
        super(request);
    }

    public void setServlet(ForwardingServletAbstract servlet) {
        this.servlet = servlet;
    }

    public void forwardTo(String pathToRender) throws IOException, ServletException {
        servlet.getServletContext().getRequestDispatcher(pathToRender).forward(this, response);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
