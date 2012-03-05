<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ page import="info.xonix.zlo.search.logic.forum_adapters.ForumAdapter" %>
<%--
  User: gubarkov
  Date: 05.03.12
  Time: 23:50
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="import.jsp" %>

<%
    for (ForumDescriptor fd : GetForum.descriptors()) {
        ForumAdapter fa = fd.getForumAdapter();
        long messageId = fa.extractMessageIdFromMessageUrl(request.getParameter("text"));
        if (messageId > 0) {
            pageContext.setAttribute("messageId", messageId);
            pageContext.setAttribute("fd", fd);
%>
<c:set var="msgSavedUrl" value="msg?site=${fd.forumIntId}&num=${messageId}"/>
Посетить сохраненное сообщение: <a class="search" href="${msgSavedUrl}">№ ${messageId}</a><br/>
<%
            break;
        }
    }
%>
