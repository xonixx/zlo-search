<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>

<%@ include file="WEB-INF/include/import.jsp" %>
<%@ include file="WEB-INF/include/notDirectlyAccessible.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="savedMsg" scope="request" class="org.xonix.zlo.search.model.ZloMessage" />
<jsp:useBean id="siteRoot" class="java.lang.String" scope="session" />

<jsp:useBean id="hl" class="org.xonix.zlo.search.FoundTextHighlighter" scope="session" />
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
                        <c:if test="${not empty savedMsg.topic}">[${savedMsg.topic}]</c:if>
                        <jsp:setProperty name="hl" property="text" value="${savedMsg.title}" />
                        <c:out value="${hl.highlightedText}" escapeXml="false" />
                        <a href="http://${siteRoot}/?read=${savedMsg.num}">?</a>
                    </big>
                    <br />Сообщение было послано:
                    <span class="nick">
                        <c:choose>
                            <c:when test="${not savedMsg.reg}"><c:out value="${savedMsg.nick}" escapeXml="true" /></c:when>
                            <c:otherwise>
                                <a href="http://${siteRoot}/?uinfo=<c:out value="${savedMsg.nick}" escapeXml="false" />"><c:out value="${savedMsg.nick}" escapeXml="false" /></a>
                            </c:otherwise>
                        </c:choose>
                        <a class="search" href="search?site=${savedMsg.site.num}&nick=<c:out value="${savedMsg.nick}" escapeXml="false"/>">?</a>
                    </span>
                    (${savedMsg.host} <a class="search" href="search?site=${savedMsg.site.num}&host=${savedMsg.host}">?</a>)
                    <br />Дата:
                    <fmt:formatDate value="${savedMsg.date}" pattern="EEEE, MMMM d HH:mm:ss yyyy" />
                </div>
                <br />
                <div id="body">
                    <jsp:setProperty name="hl" property="text" value="${savedMsg.body}" />
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
    <jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />
</html>
