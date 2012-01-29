<%@ page import="info.xonix.zlo.search.xmlfp.XmlFpFormer" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.io.PrintWriter" %>
<%--
  User: Vovan
  Date: 13.08.2008
  Time: 21:16:40
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../WEB-INF/jsp/import.jsp" %>
<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<%!
    private final XmlFpFormer xmlFpFormer = AppSpringContext.get(XmlFpFormer.class);
%>

<c:choose>
    <c:when test="${empty param}">
        <%@ include file="xmlfpInfo.jsp" %>
    </c:when>
    <c:otherwise>
        <%
            final PrintWriter responseWriter = response.getWriter();
            try {
                response.setStatus(200);
                response.setContentType("text/xml; charset=UTF-8");

                final String num = request.getParameter("num");
                if (StringUtils.isNotEmpty(num)) {
                    responseWriter.write(xmlFpFormer.getMessage(site, Integer.parseInt(num)));

                } else if (request.getParameter("lastMessageNumber") != null) {
                    responseWriter.write(xmlFpFormer.lastMessageNumber(site));
                }
            } catch (Exception ex) {
                response.setStatus(500); // todo: ?
                response.setContentType("text/html");
                responseWriter.write("<h3>Error</h3>");
                responseWriter.write("<pre>" + ex.toString() + "</pre>");
            }
        %>
    </c:otherwise>

</c:choose>

