<%@ page import="org.xonix.zlo.web.servlets.helpful.ForwardingRequest" %>
<%--
  User: gubarkov
  Date: 02.10.2007
  Time: 16:00:13
--%>
<%
    if(request.getAttribute(ForwardingRequest.FORWARDED_FROM) == null) {
        response.sendError(404, request.getServletPath());
        response.flushBuffer();
        return;
    }
%>

