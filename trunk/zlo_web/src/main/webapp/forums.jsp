<%@ page import="info.xonix.zlo.search.domainobj.Site" %>
<%@ page import="info.xonix.zlo.search.logic.AppLogic" %>
<%@ page import="info.xonix.zlo.search.logic.SiteLogic" %>
<%--
  User: Vovan
  Date: 01.06.2008
  Time: 1:26:28
--%>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css"/>

<%!
    SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
    AppLogic appLogic = AppSpringContext.get(AppLogic.class);
%>

<title>Индексируемые форумы</title>

<tiles:insertDefinition name="header.forums"/>

<div align="center" class="content">
    <h3>Индексируемые форумы</h3>

    <c:set var="sites" value="<%= siteLogic.getSites() %>"/>

    <display:table id="site" htmlId="resultTable" name="${sites}">
        <display:column title="Ссылка">
            <c:set var="url" value="http://${site.siteUrl}/"/>
            <a href="${url}">${url}</a>
        </display:column>
        <display:column title="Описание" property="siteDescription"/>
        <display:column title="Сообщений">
            <%= appLogic.getLastIndexedNumber((Site) site) %>
        </display:column>

        <display:column title="Сервисы">
            <a href="search?site=${site.siteNumber}" class="search">(Поиск)</a>
            <a href="stats.jsp?site=${site.siteNumber}" class="search">(Статистика)</a>
            <a href="nickhost.jsp?site=${site.siteNumber}" class="search">(Ники/Хосты)</a>
        </display:column>

    </display:table>
</div>

<tiles:insertDefinition name="ga"/>
