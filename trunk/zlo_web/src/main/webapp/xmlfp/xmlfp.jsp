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

                final String xmlFp = request.getParameter("xmlfp");

                if ("message".equals(xmlFp)) {
                    responseWriter.write(xmlFpFormer.getMessage(forumId,
                            Integer.parseInt(request.getParameter("num"))));

                } else if ("messages".equals(xmlFp)) {
                    responseWriter.write(xmlFpFormer.getMessages(forumId,
                            Integer.parseInt(request.getParameter("from")),
                            Integer.parseInt(request.getParameter("to"))
                    ));
                } else if ("lastMessageNumber".equals(xmlFp)) {
                    responseWriter.write(xmlFpFormer.lastMessageNumber(forumId));

                } else if ("descriptor".equals(xmlFp)) {
                    responseWriter.write(xmlFpFormer.siteXmlFpDescriptor(forumId));

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

