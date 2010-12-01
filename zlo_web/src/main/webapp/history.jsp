<%@ page import="info.xonix.zlo.search.domainobj.Site" %>
<%@ page import="info.xonix.zlo.search.logic.SiteLogic" %>
<%@ page import="info.xonix.zlo.web.utils.RequestUtils" %>
<%@ page import="java.util.Map" %>

<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css"/>

<%!
    SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
%>

<%--<c:set var="localIps"><fmt:message key="localIps"/></c:set>--%>
<c:set var="isPowerUser" value='<%= RequestUtils.isPowerUser(request) %>'/>
<c:set var="showAll" value="${ param['all'] != null and isPowerUser }"/>

<c:set var="lastHours" value="6"/>
<c:if test="${isPowerUser and not empty param['n']}">
    <c:set var="lastHours" value="${param['n']}"/>
</c:if>

<sql:query var="totalNum">
    SELECT MAX(id) last FROM request_log
</sql:query>

<c:set var="reqDateWhereClause">
    WHERE req_date BETWEEN (NOW() - INTERVAL ? HOUR) AND NOW()
</c:set>

<c:choose>
    <c:when test="${showAll}">
        <sql:query var="res">
            SELECT * FROM request_log USE INDEX (req_date_idx)
            ${reqDateWhereClause}
            order by id DESC
            <sql:param value="${lastHours}"/>
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            SELECT * FROM request_log USE INDEX (req_date_idx)
            ${reqDateWhereClause}
            AND is_admin_req <> 1
            AND is_rss_req = 0
            order by id DESC
            <sql:param value="${lastHours}"/>
        </sql:query>
    </c:otherwise>
</c:choose>

<title>История запросов</title>

<tiles:insertDefinition name="header.history"/>

<div align="center" class="content">
    <h3>История запросов
        <c:if test="${isPowerUser}">
            <c:choose>
                <c:when test="${!showAll}"><a href="history.jsp?all" class="search">(подробно)</a></c:when>
                <c:otherwise><a href="history.jsp" class="search">(кратко)</a></c:otherwise>
            </c:choose>
        </c:if>
    </h3>
    <small>(всего запросов: ${totalNum.rows[0].last}, показано ${res.rowCount} ${
            xonix:plural(res.rowCount, 'запрос', 'запроса', 'запросов')}, за последние ${lastHours} ${
            xonix:plural(lastHours, 'час', 'часа', 'часов')})
    </small>

    <display:table name="${res.rows}" id="row" htmlId="resultTable"
                   decorator="info.xonix.zlo.web.decorators.HistoryTableDecorator">
        <display:column title="№">
            <a href="search?<c:out value="${row.req_query_str}"/>" class="search">${row_rowNum}</a>
        </display:column>
        <display:column property="searchText" title="Текст"/>
        <display:column property="searchNick" title="Ник поиска"/>
        <display:column property="searchHost" title="Хост поиска"/>
        <display:column title="Сайт">
            <% Site site = siteLogic.getSite((Integer) ((Map) row).get("site")); %>
            <c:if test="<%= site != null %>">
                <a href="http://<%= site.getSiteUrl() %>">
                    <%= site.getName() %>
                </a>
            </c:if>
        </display:column>
        <display:column property="reqDate" title="Дата" class="small"/>
        <c:if test="${showAll}">
            <display:column property="host" title="Хост" class="small"/>
            <display:column property="user_agent" title="User-Agent" class="small"/>
        </c:if>
        <display:column property="userAgentSmall" title="Браузер" class="small center"/>
    </display:table>
</div>

<tiles:insertDefinition name="ga"/>