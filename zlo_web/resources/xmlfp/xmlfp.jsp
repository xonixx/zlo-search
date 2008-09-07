<%@ page import="info.xonix.zlo.search.model.ZloMessage, org.apache.commons.lang.StringUtils, info.xonix.zlo.search.xmlfp.ZloJaxb" %>
<%--
  User: Vovan
  Date: 13.08.2008
  Time: 21:16:40
--%>

<%@ page contentType="text/html; charset=windows-1251" %>
<%@ include file="../WEB-INF/jsp/import.jsp" %>
<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<c:choose>
    <c:when test="${empty param}">
        <%@ include file="xmlfpInfo.jsp"%>
    </c:when>
    <c:otherwise>
        <%
            try {
                response.setStatus(200);
                response.setContentType("text/xml; charset=windows-1251");

                if (StringUtils.isNotEmpty(request.getParameter("num"))) {
                    ZloMessage m = site.getDB().getMessageByNumber(Integer.parseInt(request.getParameter("num")));
                    response.getWriter().write(ZloJaxb.zloMessageToXml(m));
                } else if (request.getParameter("lastMessageNumber") != null) {
                    response.getWriter().write(ZloJaxb.lastNessageNumberToXml(
                            site.getDB().getLastMessageNumber()
                    ));
                }
            } catch (Exception ex) {
                response.setStatus(500); // todo: ?
                response.setContentType("text/html");
                response.getWriter().write("<h3>Error</h3>");
            }
        %>
    </c:otherwise>

</c:choose>

