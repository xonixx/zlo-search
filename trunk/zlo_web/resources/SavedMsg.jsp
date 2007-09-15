<%@ page import="org.xonix.zlo.search.config.HtmlStrings" %>
<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>

<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<html>
    <head>
        <title><%= HtmlStrings.PAGE_TITLE %></title>
        <link rel="stylesheet" type="text/css" href="main.css" />
        <script type="text/javascript" src="script.js"></script>
    </head>
    <body>
        <c:choose>
            <c:when test="${empty requestScope['error']}">
                Found!!!
                <c:out value="${requestScope['savedMsg']}" />
            </c:when>
            <c:otherwise>
                <div class="error">
                    <c:out value="${requestScope['error']}" />
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>
