package org.xonix.zlo.web.servlets;

import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:23:38
 */
public class SavedMessageServlet extends ForwardingServlet {

    public static final String JSP_SAVED_MSG = "/SavedMsg.jsp"; 

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.forwardTo(JSP_SAVED_MSG);
    }
}
