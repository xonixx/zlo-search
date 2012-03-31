<%--
  User: gubarkov
  Date: 11.09.2007
  Time: 17:31:09
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="import.jsp" %>
<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<jsp:useBean id="msg" scope="request" class="info.xonix.zlo.search.model.Message"/>

<jsp:useBean id="hl" class="info.xonix.zlo.search.FoundTextHighlighter" scope="request"/>
<jsp:setProperty name="hl" property="hlClass" value="hl"/>
<jsp:setProperty name="hl" property="wordsStr" value='<%= request.getParameter("hw") %>'/>

<html>
<head>
    <title><fmt:message key="page.title"/></title>
    <link rel="stylesheet" type="text/css" href="main.css"/>
</head>
<body>

<tiles:insertDefinition name="header.message"/>

<c:choose>
    <c:when test="${empty requestScope['error']}">
        <c:if test="${not empty parentMsg}">
            <jsp:useBean id="parentMsg" scope="request" type="info.xonix.zlo.search.model.Message"/>
            В ответ на:
            <a href="msg?site=${forumIntId}&num=${parentMsg.num}">${parentMsg.title}</a> --
            <util:nick_and_host msg="${parentMsg}"/> --
            <fmt:formatDate value="${parentMsg.date}" pattern="d/MM/yyyy HH:mm:ss"/>
        </c:if>
        <div align="center">
            <big>
                <c:if test="${not empty msg.topic}">[${msg.topic}]</c:if>
                <jsp:setProperty name="hl" property="text" value="${msg.title}"/>
                <c:out value="${hl.highlightedText}" escapeXml="false"/>
                <a href="<%= adapter.prepareMessageUrl(msg.getNum()) %>">?</a>

                    <%--paul7 link--%>
                <%--<c:if test="${msg.site.name == 'zlo'}">
                    <a href="http://zlo.paul7.net/${msg.num}">paul7</a>
                </c:if>--%><%--dead for now--%>
            </big>
            <br/>Сообщение было послано: <util:nick_and_host msg="${msg}"/>
            <br/>Дата:
            <fmt:formatDate value="${msg.date}" pattern="EEEE, MMMM d HH:mm:ss yyyy"/>
        </div>
        <br/>

        <div id="body">
            <jsp:setProperty name="hl" property="text" value="${msg.body}"/>
            <c:out value="${hl.highlightedText}" escapeXml="false"/>
        </div>
    </c:when>
    <c:otherwise>
        <div class="error">
            <c:out value="${requestScope['error']}"/>
        </div>
    </c:otherwise>
</c:choose>
</body>
<tiles:insertDefinition name="ga"/>
</html>
