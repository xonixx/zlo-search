<%@ page import="info.xonix.zlo.search.logic.AppLogic" %>
<%@ page import="info.xonix.zlo.search.model.Message" %>
<%@ page import="info.xonix.zlo.search.xmlfp.ZloJaxb" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--
  User: Vovan
  Date: 13.08.2008
  Time: 21:16:40
--%>

<%@ page contentType="text/html; charset=windows-1251" %>
<%@ include file="../WEB-INF/jsp/import.jsp" %>
<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<%!
    private final AppLogic appLogic = AppSpringContext.get(AppLogic.class);
%>

<c:choose>
    <c:when test="${empty param}">
        <%@ include file="xmlfpInfo.jsp" %>
    </c:when>
    <c:otherwise>
        <%
            try {
                response.setStatus(200);
                response.setContentType("text/xml; charset=windows-1251");

                if (StringUtils.isNotEmpty(request.getParameter("num"))) {
                    Message m = appLogic.getMessageByNumber(site, Integer.parseInt(request.getParameter("num")));
                    response.getWriter().write(ZloJaxb.zloMessageToXml(m));
                } else if (request.getParameter("lastMessageNumber") != null) {
                    response.getWriter().write(ZloJaxb.lastNessageNumberToXml(
                            appLogic.getLastSavedMessageNumber(site)));
                }
            } catch (Exception ex) {
                response.setStatus(500); // todo: ?
                response.setContentType("text/html");
                response.getWriter().write("<h3>Error</h3>");
            }
        %>
    </c:otherwise>

</c:choose>

