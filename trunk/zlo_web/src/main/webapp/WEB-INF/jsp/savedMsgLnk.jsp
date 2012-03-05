<%--
  User: gubarkov
  Date: 05.03.12
  Time: 23:50
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="import.jsp" %>

<c:set var="messageId" value='<%= adapter.extractMessageIdFromMessageUrl(request.getParameter("text")) %>'/>
<c:if test="${messageId > 0}">
    <c:set var="msgSavedUrl" value="msg?site=${descriptor.forumIntId}&num=${messageId}"/>
    Посетить сохраненное сообщение: <a class="search" href="${msgSavedUrl}">№ ${messageId}</a><br/>
</c:if>