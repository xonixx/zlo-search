package org.xonix.zlo.web.servlets;

import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.search.config.ErrorMessages;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.ZloSearcher;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:23:38
 */
public class SavedMessageServlet extends ForwardingServlet {
    public static final String QS_NUM = "num";
    public static final String ERROR = "error";
    public static final String SAVED_MSG = "savedMsg";

    public static final String JSP_SAVED_MSG = "/SavedMsg.jsp";

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        int num = -1;
        if (StringUtils.isEmpty(request.getParameter(QS_NUM))) {
            request.setAttribute(ERROR, ErrorMessages.NumParameterInvalid);
        } else {
            try {
                num = Integer.parseInt(request.getParameter(QS_NUM));
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR, ErrorMessages.NumParameterInvalid);
            }
        }

        if (num == -1) {
            request.forwardTo(JSP_SAVED_MSG);
            return;
        }

        ZloMessage msg = ZloSearcher.searchMsgByNum(num);
        if (msg != null) {
            request.setAttribute(SAVED_MSG, msg);    
        } else {
            request.setAttribute(ERROR, ErrorMessages.MessageNotFound);
        }

        request.forwardTo(JSP_SAVED_MSG);
    }
}
