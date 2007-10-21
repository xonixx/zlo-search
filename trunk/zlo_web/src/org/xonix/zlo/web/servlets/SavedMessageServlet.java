package org.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.config.ErrorMessages;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
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

        ZloMessage msg;
        try {
            msg = DAO.DB._getMessageByNumber(num);
            if (msg != null) {
                request.setAttribute(SAVED_MSG, msg);
            } else {
                request.setAttribute(ERROR, ErrorMessages.MessageNotFound);
            }
        } catch (DAO.DAOException e) {
            request.setAttribute(ERROR, ErrorMessages.DbError);
        }

        request.forwardTo(JSP_SAVED_MSG);
    }
}
