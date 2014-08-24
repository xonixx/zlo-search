<%@ page import="info.xonix.zlo.web.utils.RequestUtils" %>
<%--
  User: Vovan
  Date: 25.04.2008
  Time: 18:01:15
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:set var="activeScreen"><tiles:getAsString name="activeScreen"/></c:set>
<jsp:useBean id="siteRoot" class="java.lang.String" scope="request"/>

<c:set var="site">
    <c:choose>
        <c:when test="${not empty param['site']}">${param['site']}</c:when>
        <c:otherwise>${cookie['site'].value}</c:otherwise>
    </c:choose>
</c:set>

<c:set var="baseUrl" value="${pageContext.request.contextPath}/"/>

<div id="header" align="center">
    <ul>
        <li><a href="${baseUrl}search"
               <c:if test="${activeScreen == 'search'}">class="activeLink"</c:if>>Поиск</a></li>
        <li><a href="${baseUrl}stats.jsp?site=${site}"
               <c:if test="${activeScreen == 'stats'}">class="activeLink"</c:if>>Статистика</a></li>
        <li><a href="${baseUrl}nickhost.jsp?site=${site}"
               <c:if test="${activeScreen == 'nickhost'}">class="activeLink"</c:if>>Ники/Хосты</a></li>
        <li><a href="${baseUrl}a/charts/index<%--?site=${site}--%>"
               <c:if test="${activeScreen == 'charts'}">class="activeLink"</c:if>>Графики</a></li>
        <li><a href="${baseUrl}history.jsp"
               <c:if test="${activeScreen == 'history'}">class="activeLink"</c:if>>История запросов</a></li>
        <li><a href="${baseUrl}forums.jsp"
               <c:if test="${activeScreen == 'forums'}">class="activeLink"</c:if>>Форумы</a></li>
        <li><a href="${baseUrl}faq.jsp"
               <c:if test="${activeScreen == 'faq'}">class="activeLink"</c:if>>FAQ</a></li>

        <li><a href="${baseUrl}developer.jsp"
               <c:if test="${activeScreen == 'developer'}">class="activeLink"</c:if>>Разработчику</a></li>

        <li><a href="${baseUrl}contact.jsp"
               <c:if test="${activeScreen == 'contact'}">class="activeLink"</c:if>>Обратная связь</a></li>

        <c:if test="<%= RequestUtils.isPowerUser(request) %>">
            <span class="adminLinks">
                <%--admin menu items--%>
                <%--<li><a href="${baseUrl}detectspam.jsp"
                       <c:if test="${activeScreen == 'detectspam'}">class="activeLink"</c:if>>Spam</a></li>--%>
                <li><a href="${baseUrl}admin.jsp"
                       <c:if test="${activeScreen == 'admin'}">class="activeLink"</c:if>>Admin</a></li>
            </span>
        </c:if>
    </ul>
</div>