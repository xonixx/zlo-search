<%@ page import="info.xonix.zlo.web.controlsdata.ControlsData" %>
<%@ page import="info.xonix.zlo.search.utils.DateUtil" %>
<%--
  User: Vovan
  Date: 20.01.2008
  Time: 4:08:47
--%>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="WEB-INF/jsp/commonJsCss.jsp" %>
<script type="text/javascript" src="js/stats.js?${version}"></script>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>
<jsp:setProperty name="backendBean" property="*"/>
<%-- all from request properties --%>

<%@ include file="WEB-INF/jsp/setSite.jsp" %>

<c:set var="byNick" value="${empty param['type'] or param['type'] == 'nick'}"/>

<c:set var="periodsMap" value="<%= ControlsData.STATS_PERIODS_MAP %>"/>
<c:set var="period" value="${periodsMap[not empty param['period'] ? param['period'] : '']}"/>
<c:set var="periodDays" value="${period.days}"/>

<c:set var="messagesTbl">${forumId}_messages</c:set>

<c:set var="msgDateWhereClause">
    where msgDate BETWEEN (NOW() - INTERVAL ? DAY) AND NOW()
</c:set>

<c:choose>
    <c:when test="${byNick}">
        <sql:query var="res">
            select nick, user_id, reg, COUNT(*) cnt from ${messagesTbl}${' '}${msgDateWhereClause}
            group by nick
            order by cnt desc;
            <sql:param>${periodDays}</sql:param>
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            select host, COUNT(*) cnt from ${messagesTbl}${' '}${msgDateWhereClause}
            group by host
            order by cnt desc;
            <sql:param>${periodDays}</sql:param>
        </sql:query>
    </c:otherwise>
</c:choose>

<sql:query var="resTotal">
    select COUNT(*) cnt from ${messagesTbl}${' '}${msgDateWhereClause}
    <sql:param>${periodDays}</sql:param>
</sql:query>

<c:set var="title">
    Статистика форума «${adapter.forumTitle}» по <c:choose><c:when
        test="${byNick}">никам</c:when><c:otherwise>хостам</c:otherwise></c:choose>
    за последние ${period.label}
</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.stats"/>

<div align="center">
    <h3>${title}</h3>

    <form id="stats-form" action="stats.jsp" method="get">
        Форум:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        По:
        <input type="radio" name="type" value="nick" id="tn"
               <c:if test="${byNick}">checked="checked"</c:if> /><label for="tn">нику</label>
        <input type="radio" name="type" value="host" id="th" <c:if test="${!byNick}">checked="checked"</c:if>/><label
            for="th">хосту</label>
        за последние:
        <select name="period">
            <c:forEach var="p" items="${periodsMap}">
                <option value="${p.key}"
                        <c:if test="${periodDays == p.value.days}">selected="selected"</c:if>>${p.value.label}
                </option>
            </c:forEach>
        </select>
    </form>
    <small>Всего сообщений за этот период: ${resTotal.rows[0].cnt}</small>

    <%
        long end = DateUtil.currentDateNoTime().getTime();
        long start = end - 365L * 24 * 3600 * 1000;
    %>

    <display:table name="${res.rows}" id="row" htmlId="resultTable">
        <display:setProperty name="basic.msg.empty_list"><%-- don't display empty msg --%></display:setProperty>

        <display:column title="№" value="${row_rowNum}"/>
        <display:column title="${byNick ? 'Ник' : 'Хост'}">
            <c:choose>
                <c:when test="${byNick}">
                    <tiles:insertDefinition name="nick">
                        <tiles:putAttribute name="reg" value="${row.reg}"/>
                        <tiles:putAttribute name="nick" value="${row.nick}"/>
                        <tiles:putAttribute name="userId" value="${row.user_id}"/>
                    </tiles:insertDefinition>
                    <a class="search" title="График активности"
                            href='a/charts#?params={"forumId":"${forumId}", "type":"DayInterval", "dbNicks":"${row.nick}", "start":<%=start%>, "end": <%=end%>}'>a</a>
                </c:when>
                <c:otherwise>
                    <tiles:insertDefinition name="host">
                        <tiles:putAttribute name="host" value="${row.host}"/>
                    </tiles:insertDefinition>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="Число сообщений" value="${row.cnt}"/>
    </display:table>

</div>

<tiles:insertDefinition name="ga"/>
