package org.xonix.zlo.web.servlets.helpful;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Author: gubarkov
 * Date: 30.08.2007
 * Time: 21:22:43
 */
public class ForwardingRequest extends HttpServletRequestWrapper {
    private ForwardingServletAbstract servlet;
    private HttpServletResponse response;

    private Map<String, String> _parameters = new HashMap<String, String>();

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

    public void setParameter(String key, String value) {
        _parameters.put(key, value);
    }

    public String getParameter(String key) {
        String val = super.getParameter(key);
        if (StringUtils.isEmpty(val)){
            val = _parameters.get(key);
        }
        return val;
    }

    public Enumeration getParameterNames() {
        Vector<String> res = new Vector<String>();
        for (Enumeration en = super.getParameterNames(); en.hasMoreElements();) {
            res.add((String) en.nextElement());
        }
        res.addAll(_parameters.keySet());
        return res.elements();
    }
}
