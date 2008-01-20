<%@ page import="org.xonix.zlo.search.dao.Site" %>
<%--
  User: Vovan
  Date: 20.01.2008
  Time: 4:08:47
--%>
<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<%
    Site site;
    try {
        site = Site.getSite(Integer.parseInt(request.getParameter("site")));
    } catch (NumberFormatException e) {
        site = Site.getSite(0);
    }
%>

<sql:setDataSource dataSource="<%= site.getDataSource() %>" />

<c:set var="byNick" value="${empty param['type'] or param['type'] == 'nick'}" />

<c:choose>
    <c:when test="${byNick}">
        <sql:query var="res">
            select nick, COUNT(*) cnt from messages
            where msgDate > NOW() - INTERVAL 1 DAY
            group by nick
            order by cnt desc;
    </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            select host, COUNT(*) cnt from messages
            where msgDate > NOW() - INTERVAL 1 DAY
            group by host
            order by cnt desc;
        </sql:query>
    </c:otherwise>
</c:choose>

<c:set var="title">
    Статистика сайта <%= site.SITE_URL %>
</c:set>
<title><c:out value="${title}"/></title>

<h3><c:out value="${title}"/></h3>

<form action="stats.jsp" method="get">
    По:
    <input type="radio" name="type" value="nick" id="tn" <c:if test="${byNick}">checked="checked"</c:if> /><label for="tn">нику</label>
    <input type="radio" name="type" value="host" id="th" <c:if test="${!byNick}">checked="checked"</c:if>/><label for="th">хосту</label>
    <input type="submit" value="Показать!" />
</form>

<table border="1" align="center">
    <tr><th>№</th><th><c:choose><c:when test="${byNick}">Ник</c:when><c:otherwise>Хост</c:otherwise></c:choose></th><th>Число сообщений</th></tr>
    <% Integer i=0; %>
    <c:forEach var="row" items="${res.rows}">
        <% i++; %>
        <tr>
            <td><%= i %></td>
        <td><c:choose>
                <c:when test="${byNick}">
                    <c:out value="${row.nick}"/><a href="search?site=<%= site.getNum() %>&nick=<c:out value="${row.nick}"/>" class="search">?</a>
                </c:when>
                <c:otherwise>
                    <c:out value="${row.host}"/><a href="search?site=<%= site.getNum() %>&host=<c:out value="${row.host}"/>" class="search">?</a>
                </c:otherwise>
            </c:choose></td>
        <td><c:out value="${row.cnt}"/></td>
        </tr>
    </c:forEach>
</table>
