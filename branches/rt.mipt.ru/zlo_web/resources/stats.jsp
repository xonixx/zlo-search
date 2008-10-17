<%--
  User: Vovan
  Date: 20.01.2008
  Time: 4:08:47
--%>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request" />
<jsp:setProperty name="backendBean" property="*" /> <%-- all from request properties --%>

<%@ include file="WEB-INF/jsp/setSite.jsp"%>
<sql:setDataSource dataSource="${site.dataSource}" />

<c:set var="byNick" value="${empty param['type'] or param['type'] == 'nick'}" />
<c:set var="period" value="${param['period'] == '2' ? 10 : param['period'] == '3' ? 30 : 2}" />

<c:set var="messagesTbl">${site.name}_messages</c:set>

<c:set var="msgDateWhereClause">
    where msgDate BETWEEN (NOW() - INTERVAL ? DAY) AND NOW()
</c:set>

<c:choose>
    <c:when test="${byNick}">
        <sql:query var="res">
            select nick, reg, COUNT(*) cnt from ${messagesTbl}
            ${msgDateWhereClause}
            group by nick
            order by cnt desc;
            <sql:param>${period}</sql:param>
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            select host, COUNT(*) cnt from ${messagesTbl}
            ${msgDateWhereClause}
            group by host
            order by cnt desc;
            <sql:param>${period}</sql:param>
        </sql:query>
    </c:otherwise>
</c:choose>

<sql:query var="resTotal">
    select COUNT(*) cnt from ${messagesTbl}
    ${msgDateWhereClause}
    <sql:param>${period}</sql:param>
</sql:query>

<c:set var="title">
    Статистика сайта ${site.SITE_URL} по <c:choose><c:when test="${byNick}">никам</c:when><c:otherwise>хостам</c:otherwise></c:choose>
    за последние ${period} суток
</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.stats" />

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
    <input type="submit" value="Показать!" />
</form>
    <small>Всего сообщений за этот период: ${resTotal.rows[0].cnt}</small>

<display:table name="${res.rows}" id="row" htmlId="resultTable">
    <display:column title="№" value="${row_rowNum}"/>
    <display:column title="${byNick ? 'Ник' : 'Хост'}">
        <c:choose>
            <c:when test="${byNick}">
                <tiles:insertDefinition name="nick">
                    <tiles:putAttribute name="reg" value="${row.reg}"/>
                    <tiles:putAttribute name="nick" value="${row.nick}"/>
                    <tiles:putAttribute name="site" value="${site}"/>
                </tiles:insertDefinition>
            </c:when>
            <c:otherwise>
                <tiles:insertDefinition name="host">
                    <tiles:putAttribute name="host" value="${row.host}"/>
                    <tiles:putAttribute name="site" value="${site}"/>
                </tiles:insertDefinition>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column title="Число сообщений" value="${row.cnt}" />
</display:table>

</div>    

<tiles:insertDefinition name="ga" />
