<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>

<%@ include file="WEB-INF/include/import.jsp" %>
<%@ include file="WEB-INF/include/notDirectlyAccessible.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="msg" scope="request" class="info.xonix.zlo.search.model.ZloMessage" />
<jsp:useBean id="siteRoot" class="java.lang.String" scope="session" />

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
                    <span class="nick">
                        <c:set var="nickEscaped"><c:out value="${msg.nick}" /></c:set>
                        <c:set var="nickUrlencoded"><c:out value="${xx:urlencode(msg.nick)}" /></c:set>
                        <c:choose>
                            <c:when test="${not msg.reg}">${nickEscaped}</c:when>
                            <c:otherwise>
                                <a href="http://${siteRoot}/?uinfo=${nickUrlencoded}">${nickEscaped}</a>
                            </c:otherwise>
                        </c:choose>
                        <a class="search" href="search?site=${msg.site.num}&nick=${nickUrlencoded}">?</a>
                        <a class="search" href="nickhost.jsp?site=${msg.site.num}&w=n&t=${nickUrlencoded}" title="хосты этого ника">h</a>
                    </span>
                    (${msg.host}
                    <a class="search" href="search?site=${msg.site.num}&host=${msg.host}">?</a>
                    <a class="search" href="nickhost.jsp?site=${msg.site.num}&w=h&t=${msg.host}" title="ники этого хоста">n</a>)
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
    <jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />
</html>
