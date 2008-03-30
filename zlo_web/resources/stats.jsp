<%@ page import="org.xonix.zlo.search.dao.Site" %>
<%--
  User: Vovan
  Date: 20.01.2008
  Time: 4:08:47
--%>
<%@ include file="WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<jsp:useBean id="backendBean" class="org.xonix.zlo.web.BackendBean" scope="session" />
<jsp:setProperty name="backendBean" property="*" /> <%-- all from request properties --%>

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
<c:set var="period" value="${param['period'] == '2' ? 10 : param['period'] == '3' ? 30 : 2}" />

<c:choose>
    <c:when test="${byNick}">
        <sql:query var="res">
            select nick, reg, COUNT(*) cnt from messages
            where msgDate > NOW() - INTERVAL ? DAY
            group by nick
            order by cnt desc;
            <sql:param>${period}</sql:param>
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            select host, COUNT(*) cnt from messages
            where msgDate > NOW() - INTERVAL ? DAY
            group by host
            order by cnt desc;
            <sql:param>${period}</sql:param>
        </sql:query>
    </c:otherwise>
</c:choose>

<c:set var="title">
    Статистика сайта <%= site.getSITE_URL() %> по <c:choose><c:when test="${byNick}">никам</c:when><c:otherwise>хостам</c:otherwise></c:choose>
    за последние ${period} суток
</c:set>
<title>${title}</title>

<div align="center">
<h3>${title}</h3>

<form action="stats.jsp" method="get">
    Сайт: <jsp:getProperty name="backendBean" property="siteSelector" /><br/>
    По:
    <input type="radio" name="type" value="nick" id="tn" <c:if test="${byNick}">checked="checked"</c:if> /><label for="tn">нику</label>
    <input type="radio" name="type" value="host" id="th" <c:if test="${!byNick}">checked="checked"</c:if>/><label for="th">хосту</label>
    за последние:
    <select name="period">
        <option value="1" <c:if test="${period == 2}">selected="selected"</c:if>>2-е суток</option>
        <option value="2" <c:if test="${period == 10}">selected="selected"</c:if>>10 суток</option>
        <option value="3" <c:if test="${period == 30}">selected="selected"</c:if>>30 суток</option>
    </select>
    <input type="hidden" name="site" value="<%= site.getNum() %>" />
    <input type="submit" value="Показать!" />
</form>
</div>

<table border="1" align="center">
    <tr><th>№</th><th><c:choose><c:when test="${byNick}">Ник</c:when><c:otherwise>Хост</c:otherwise></c:choose></th><th>Число сообщений</th></tr>
    <% Integer i=0; %>
    <c:forEach var="row" items="${res.rows}">
        <% i++; %>
        <tr>
            <td><%= i %></td>
        <td><c:choose>
                <c:when test="${byNick}">
                    <span class="nick">
                        <c:choose>
                            <c:when test="${not row.reg}"><c:out value="${row.nick}" escapeXml="false" /></c:when>
                            <c:otherwise>
                                <a href="http://<%= site.getSITE_URL() %>/?uinfo=<c:out value="${row.nick}" escapeXml="false" />"><c:out value="${row.nick}" escapeXml="false"/></a>
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <a href="search?site=<%= site.getNum() %>&nick=<c:out value="${row.nick}" escapeXml="false" />" class="search">?</a>
                </c:when>
                <c:otherwise>
                    ${row.host} <a href="search?site=<%= site.getNum() %>&host=${row.host}" class="search">?</a>
                </c:otherwise>
            </c:choose></td>
        <td>${row.cnt}</td>
        </tr>
    </c:forEach>
</table>

<jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />
