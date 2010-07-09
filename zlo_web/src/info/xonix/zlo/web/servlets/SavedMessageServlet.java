package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.apache.commons.lang.StringUtils;

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
    public static final String SAVED_MSG = "msg";

    public static final String JSP_SAVED_MSG = "/WEB-INF/jsp/SavedMsg.jsp";

    private AppLogic appLogic = AppSpringContext.get(AppLogic.class);

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

        setSiteInReq(request, response);

        Message msg;
//        try {
        Site site = getSite(request);
        msg = appLogic.getMessageByNumber(site, num);
        if (msg != null && msg.getStatus() != MessageStatus.DELETED) {
            request.setAttribute(SAVED_MSG, msg);
        } else {
            request.setAttribute(ERROR, ErrorMessage.MessageNotFound);
        }
        // TODO: handle db error
//        } catch (DAOException e) {
//            request.setAttribute(ERROR, ErrorMessage.DbError);
//        }

        request.forwardTo(JSP_SAVED_MSG);
    }
}
