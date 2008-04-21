package info.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.DB;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:23:38
 */
public class SavedMessageServlet extends BaseServlet {
    public static final String QS_NUM = "num";
    public static final String ERROR = "error";
    public static final String SAVED_MSG = "savedMsg";

    public static final String JSP_SAVED_MSG = "/SavedMsg.jsp";

    protected void doGet(ForwardingRequest request, HttpServletResponse response) throws ServletException, IOException {
        int num = -1;
        if (StringUtils.isEmpty(request.getParameter(QS_NUM))) {
            request.setAttribute(ERROR, ErrorMessage.NumParameterInvalid);
        } else {
            try {
                num = Integer.parseInt(request.getParameter(QS_NUM));
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR, ErrorMessage.NumParameterInvalid);
            }
        }

        if (num == -1) {
            request.forwardTo(JSP_SAVED_MSG);
            return;
        }

        setSiteInSession(request, response);

        ZloMessage msg;
        try {
            msg = getSite(request).getDB().getMessageByNumber(num);
            if (msg != null && msg.getStatus() != ZloMessage.Status.DELETED) {
                request.setAttribute(SAVED_MSG, msg);
            } else {
                request.setAttribute(ERROR, ErrorMessage.MessageNotFound);
            }
        } catch (DAOException e) {
            request.setAttribute(ERROR, ErrorMessage.DbError);
        }

        request.forwardTo(JSP_SAVED_MSG);
    }
}
