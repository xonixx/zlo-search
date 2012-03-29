<%@ page import="info.xonix.zlo.web.utils.RequestUtils" %><%
    if (!RequestUtils.isPowerUser(request)) {
        response.sendError(404);
        response.flushBuffer();
        return;
    }
%>