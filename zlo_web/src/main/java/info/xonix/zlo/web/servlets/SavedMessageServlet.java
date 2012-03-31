package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.config.ErrorMessage;
import info.xonix.zlo.search.config.forums.ForumDescriptor;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:23:38
 */
public class SavedMessageServlet extends BaseServlet {
    private final Logger log = Logger.getLogger(SavedMessageServlet.class);

    public static final String QS_NUM = "num";
    public static final String ERROR = "error";
    public static final String SAVED_MSG = "msg";
    public static final String PARENT_MSG = "parentMsg";
    public static final String CHILD_MSGS = "childMsgs";

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
        Message parentMsg = null;

        try {
            final ForumDescriptor forumDescriptor = getSite(request);
            final String forumId = forumDescriptor.getForumId();

            msg = appLogic.getMessageByNumber(forumId, num);

            if (msg != null && msg.getStatus() != MessageStatus.DELETED) {
                request.setAttribute(SAVED_MSG, msg);

                if (forumDescriptor.getForumAdapter().supportsParents()) {
                    final int parentNum = msg.getParentNum();
                    if (parentNum > 0) {
                        parentMsg = appLogic.getMessageByNumber(forumId, parentNum);
                    }

                    if (parentMsg != null && parentMsg.isOk()) {
                        request.setAttribute(PARENT_MSG, parentMsg);
                    }

                    final List<Message> childMessages = appLogic.getChildMessages(forumId, msg.getNum());
                    request.setAttribute(CHILD_MSGS, childMessages);
                }
            } else {
                request.setAttribute(ERROR, ErrorMessage.MessageNotFound);
            }
        } catch (DataAccessException e) {
            request.setAttribute(ERROR, ErrorMessage.DbError);
            log.error("DB error while accessing saved msg", e);
        }

        request.forwardTo(JSP_SAVED_MSG);
    }
}
