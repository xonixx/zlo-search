<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="info.xonix.zlo.search.config.forums.ForumDescriptor" %>
<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ page import="info.xonix.zlo.search.logic.forum_adapters.ForumAdapter" %>
<%@ page import="info.xonix.zlo.web.utils.RequestUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="info.xonix.zlo.search.utils.obscene.ObsceneAnalyzer" %>
<%@ page import="info.xonix.zlo.web.utils.html.HtmlSelectBuilder" %>

<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="WEB-INF/jsp/commonJsCss.jsp" %>

<%!
    private static final ObsceneAnalyzer obsceneAnalyzer = AppSpringContext.get(ObsceneAnalyzer.class);
%>

<c:set var="isPowerUser" value='<%= RequestUtils.isPowerUser(request) %>'/>
<c:set var="showAll" value="${ param['all'] == '1' and isPowerUser }"/>
<c:set var="limit" value="${1000}"/>

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
            LIMIT ${limit}
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
            LIMIT ${limit}
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
                <c:when test="${!showAll}"><a href="history.jsp?all=1" class="search">(подробно)</a></c:when>
                <c:otherwise><a href="history.jsp" class="search">(кратко)</a></c:otherwise>
            </c:choose>
        </c:if>
    </h3>
    <small>(всего запросов: ${totalNum.rows[0].last}, показано ${res.rowCount}${' '}
        ${xonix:plural(res.rowCount, 'запрос', 'запроса', 'запросов')}, за последние
        <c:if test="${isPowerUser}">
            <form id="form-hours" class="inline-form" method="GET">
                <input type="hidden" name="all" value="${param['all']}">
                <%= new HtmlSelectBuilder()
                        .name("n")
                        .value(request.getParameter("n"))
                        .args("onchange=\"$('#form-hours').submit()\"")
                        .addOption(6, "6 часов")
                        .addOption(12, "12 часов")
                        .addOption(24, "24 часа")
                        .addOption(48, "2 дня")
                        .addOption(120, "5 дней")
                        .build()
                %></form>)
        </c:if>
        <c:if test="${!isPowerUser}">
            ${lastHours}${' '} ${xonix:plural(lastHours, 'час', 'часа', 'часов')})
        </c:if>
    </small>

    <jsp:useBean id="decoratedRow" class="info.xonix.zlo.web.decorators.HistoryTableDecorator" scope="page"/>
    <c:set target="${decoratedRow}" property="admin" value="${isPowerUser}"/>

    <display:table name="${res.rows}" id="row" htmlId="resultTable" decorator="decoratedRow">
        <display:setProperty name="basic.msg.empty_list"><%-- don't display empty msg --%></display:setProperty>

        <display:column title="№">
            <c:choose>
                <c:when test='<%= obsceneAnalyzer.allSafe(
                    (String)((Map)row).get("req_text"),
                    (String)((Map)row).get("req_nick"),
                    (String)((Map)row).get("req_host"))
                    %>'>
                    <a href="search?<c:out value="${row.req_query_str}"/>" class="search">${row_rowNum}</a>
                </c:when>
                <c:otherwise>${row_rowNum}</c:otherwise>
            </c:choose>
        </display:column>
        <display:column property="searchText" title="Текст"/>
        <display:column property="searchNick" title="Ник поиска"/>
        <display:column property="searchHost" title="Хост поиска"/>
        <display:column title="Форум">
            <% ForumDescriptor descriptor = GetForum.descriptor((Integer) ((Map) row).get("site")); %>
            <% ForumAdapter adapter = descriptor.getForumAdapter(); %>
            <% if (adapter != null) { %>
            <a href="<%= adapter.getForumUrl() %>">
                <%= descriptor.getForumId() %>
            </a>
            <% } %>
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