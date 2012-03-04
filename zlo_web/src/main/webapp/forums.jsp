<%@ page import="info.xonix.zlo.search.config.forums.ForumDescriptor" %>
<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ page import="info.xonix.zlo.search.logic.AppLogic" %>
<%--
  User: Vovan
  Date: 01.06.2008
  Time: 1:26:28
--%>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<link rel="stylesheet" type="text/css" href="main.css"/>

<%!
    AppLogic appLogic = AppSpringContext.get(AppLogic.class);
%>

<title>Индексируемые форумы</title>

<tiles:insertDefinition name="header.forums"/>

<div align="center" class="content">
    <h3>Индексируемые форумы</h3>

    <c:set var="forumDescriptors" value="<%= GetForum.descriptors() %>"/>

    <display:table id="descriptor" htmlId="resultTable" name="${forumDescriptors}">
        <c:set var="adaptor" value="${descriptor.forumAdapter}" />

        <display:column title="Ссылка">
            <c:set var="siteUrl" value="${adaptor.forumUrl}"/>
            <c:if test="${
                not f:endsWith(siteUrl, '.cgi') and
                not f:endsWith(siteUrl, '.exe')
            }">
                <c:set var="siteUrl" value="${siteUrl}"/>
            </c:if>
            <c:set var="url" value="${siteUrl}"/>
            <a href="${url}">${url}</a>
        </display:column>
        <display:column title="Описание">
            <c:choose>
                <c:when test="${not descriptor.forumParams.performIndexing}">
                    <strike><c:out value="${adaptor.forumTitle}"/></strike>
                </c:when>
                <c:otherwise><c:out value="${adaptor.forumTitle}"/></c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="Сообщений">
            <%= appLogic.getLastIndexedNumber(((ForumDescriptor) descriptor).getForumId()) %>
        </display:column>

        <display:column title="Сервисы">
            <a href="search?site=${descriptor.forumIntId}" class="search">(Поиск)</a>
            <a href="stats.jsp?site=${descriptor.forumIntId}" class="search">(Статистика)</a>
            <a href="nickhost.jsp?site=${descriptor.forumIntId}" class="search">(Ники/Хосты)</a>
        </display:column>

    </display:table>
</div>

<tiles:insertDefinition name="ga"/>
