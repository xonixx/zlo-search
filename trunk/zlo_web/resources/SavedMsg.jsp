<%@ page import="org.xonix.zlo.search.config.HtmlStrings" %>
<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>

<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="savedMsg" scope="request" class="org.xonix.zlo.search.model.ZloMessage" />
<jsp:useBean id="siteRoot" class="java.lang.String" scope="session" />

<html>
    <head>
        <title><%= HtmlStrings.PAGE_TITLE %></title>
        <link rel="stylesheet" type="text/css" href="main.css" />
        <script type="text/javascript" src="script.js"></script>
    </head>
    <body>
        <c:choose>
            <c:when test="${empty requestScope['error']}">
                <div align="center">
                    <big>
                        <c:if test="${not empty savedMsg.topic}">
                            [<c:out value="${savedMsg.topic}" />]
                        </c:if>
                        <c:out value="${savedMsg.title}" />
                    </big>
                    <br />Сообщение было послано:
                    <span class="nick">
                        <c:choose>
                            <c:when test="${not savedMsg.reg}">
                                <c:out value="${savedMsg.nick}" />
                            </c:when>
                            <c:otherwise>
                                <a href="http://<c:out value="${siteRoot}" />/?uinfo=<c:out value="${savedMsg.nick}" />"><c:out value="${savedMsg.nick}" /></a>
                            </c:otherwise>
                        </c:choose>
                    </span>
                    (<c:out value="${savedMsg.host}" />)
                    <br />Дата:
                    <fmt:formatDate value="${savedMsg.date}" pattern="EEEE, MMMM d HH:mm:ss yyyy" />
                </div>
                <br />
                <div id="body">
                    <c:out value="${savedMsg.body}" />
                </div>
            </c:when>
            <c:otherwise>
                <div class="error">
                    <c:out value="${requestScope['error']}" />
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>
