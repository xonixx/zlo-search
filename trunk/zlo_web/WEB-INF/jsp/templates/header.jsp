<%--
  User: Vovan
  Date: 25.04.2008
  Time: 18:01:15
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<c:set var="activeScreen"><tiles:getAsString name="activeScreen" /></c:set>
<jsp:useBean id="siteRoot" class="java.lang.String" scope="request" />

<div id="header" align="center">
    <ul>
        <li><a href="search"
               <c:if test="${activeScreen == 'search'}">class="activeLink"</c:if>>Поиск</a></li>
        <li><a href="stats.jsp?site=${param['site']}"
               <c:if test="${activeScreen == 'stats'}">class="activeLink"</c:if>>Статистика</a></li>
        <li><a href="history.jsp"
                <c:if test="${activeScreen == 'history'}">class="activeLink"</c:if>>История</a></li>
        <li><a href="nickhost.jsp?site=${param['site']}"
                <c:if test="${activeScreen == 'nickhost'}">class="activeLink"</c:if>>Ники/Хосты</a></li>
        <li><a href="about.jsp"
                <c:if test="${activeScreen == 'about'}">class="activeLink"</c:if>>About</a></li>
        <li><a href="faq.jsp"
                <c:if test="${activeScreen == 'faq'}">class="activeLink"</c:if>>FAQ</a></li>
        <li><a href="http://${siteRoot}">${siteRoot}</a></li>
    </ul>
</div>