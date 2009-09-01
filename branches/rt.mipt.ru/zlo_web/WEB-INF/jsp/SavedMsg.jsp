<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>
<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="msg" scope="request" class="info.xonix.zlo.search.model.ZloMessage" />
<jsp:useBean id="siteRoot" class="java.lang.String" scope="request" />

<jsp:useBean id="hl" class="info.xonix.zlo.search.FoundTextHighlighter" scope="request" />
<jsp:setProperty name="hl" property="hlClass" value="hl" />
<jsp:setProperty name="hl" property="wordsStr" value="<%= request.getParameter("hw") %>" />

<html>
    <head>
        <title><fmt:message key="page.title" /></title>
        <link rel="stylesheet" type="text/css" href="main.css" />
        <script type="text/javascript" src="script.js"></script>
    </head>
    <body>
        <c:choose>
            <c:when test="${empty requestScope['error']}">
                <div align="center">
                    <big>
                        <c:if test="${not empty msg.topic}">[${msg.topic}]</c:if>
                        <jsp:setProperty name="hl" property="text" value="${msg.title}" />
                        <c:out value="${hl.highlightedText}" escapeXml="false" />
                        <a href="http://${siteRoot}/?read=${msg.num}">?</a>
                    </big>
                    <br />Сообщение было послано:
                    <tiles:insertDefinition name="nick">
                        <tiles:putAttribute name="reg" value="${msg.reg}" />
                        <tiles:putAttribute name="nick" value="${msg.nick}" />
                        <tiles:putAttribute name="site" value="${msg.site}" />
                    </tiles:insertDefinition>
                    <tiles:insertDefinition name="host">
                        <tiles:putAttribute name="host" value="${msg.host}" />
                        <tiles:putAttribute name="site" value="${msg.site}" />
                        <tiles:putAttribute name="brackets" value="${true}" />
                    </tiles:insertDefinition>
                    <br />Дата:
                    <fmt:formatDate value="${msg.date}" pattern="EEEE, MMMM d HH:mm:ss yyyy" />
                </div>
                <br />
                <div id="body">
                    <jsp:setProperty name="hl" property="text" value="${msg.body}" />
                    <c:out value="${hl.highlightedText}" escapeXml="false"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="error">
                    <c:out value="${requestScope['error']}" />
                </div>
            </c:otherwise>
        </c:choose>
    </body>
    <tiles:insertDefinition name="ga" />
</html>
