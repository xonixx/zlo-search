<%@ page import="info.xonix.zlo.search.db.DbAccessor, info.xonix.zlo.search.dao.Site, java.util.TreeMap" %>
<%@ include file="WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<c:set var="showAll" value="${ param['showMeAllPlease'] != null  }" />
<c:set var="numberToShow" value="${1000}" />

<sql:setDataSource dataSource="<%= DbAccessor.getInstance("search_log").getDataSource() %>" />

<c:choose>
    <c:when test="${showAll}">
        <sql:query var="res">
            SELECT * FROM request_log
            order by req_date DESC
            LIMIT ?;
            <sql:param value="${numberToShow}" />
        </sql:query>
        <sql:query var="totalNum">
            SELECT count(*) AS count FROM request_log
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            SELECT * FROM request_log
            WHERE host not in ('127.0.0.1', '194.85.80.242', '193.125.143.185')
            order by req_date DESC
            LIMIT ?;
            <sql:param value="${numberToShow}" />
        </sql:query>
        <sql:query var="totalNum">
            SELECT count(*) AS count FROM request_log
            WHERE host not in ('127.0.0.1', '194.85.80.242', '193.125.143.185')
        </sql:query>
    </c:otherwise>
</c:choose>

<title>История запросов</title>

<div align="center">
    <h3>История запросов</h3>
    <small>(всего запросов: <c:out value="${totalNum.rows[0].count}" />, показаны последние: <c:out value="${numberToShow}" />)</small>

    <% int i=0; %>
    <display:table name="${res.rows}" id="row" htmlId="resultTable" decorator="info.xonix.zlo.web.decorators.HistoryTableDecorator">
        <display:column title="№" headerClass="head">
            <a href="search?<c:out value="${row.req_query_str}"/>" class="search">
                <%= ++i %>
            </a>
        </display:column>
        <display:column property="searchText" title="Текст" headerClass="head" />
        <display:column property="searchNick" title="Ник поиска" headerClass="head" />
        <display:column property="searchHost" title="Хост поиска" headerClass="head" />
        <display:column title="Сайт" headerClass="head">
            <% Site site = Site.getSite((Integer)((TreeMap)row).get("site")); %>
            <a href="http://<%= site.getSITE_URL() %>">
                <%= site.getName() %></a>
        </display:column>
        <display:column property="req_date" title="Дата" headerClass="head" class="small" />
        <c:if test="${showAll}">
            <display:column property="host" title="Хост" class="small" headerClass="head" />
            <display:column property="user_agent" title="User-Agent" class="small" headerClass="head" />
        </c:if>
        <display:column property="userAgentSmall" title="Браузер" class="small center" headerClass="head" />
    </display:table>
</div>

<jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />