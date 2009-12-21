<%--
  User: Vovan
  Date: 21.12.2009
  Time: 2:08:16
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css"/>

<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>
<jsp:setProperty name="backendBean" property="*"/>
<%-- all from request properties --%>

<c:set var="siteNum" value="${site.num}"/>
<c:set var="siteUrl" value="${site.SITE_URL}"/>
<sql:setDataSource dataSource="${site.dataSource}"/>

<c:set var="nickhostTbl">${site.name}_nickhost</c:set>
<c:set var="messagesTbl">${site.name}_messages</c:set>

<%--params--%>
<c:set var="checkLastNum"
       value="${param['checkLastNum'] == '2' ? 10000 : param['checkLastNum'] == '3' ? 20000 : 5000}"/>
<c:set var="msgsMax"
       value="${param['msgsMax'] == '2' ? 10 : 5}"/>
<c:set var="unreg" value="${not empty param['unreg']}" />
<%--<c:set var="hasUrl" value="${not empty param['hasUrl']}" />--%>


<%--maybe need optimize this--%>
<sql:query var="res">
    SELECT
        m.*,
        (select sum(cnt) from ${nickhostTbl} n where m.nick=n.nick group by n.nick) AS count
    FROM ${messagesTbl} m
    WHERE m.num > (select max(num) FROM ${messagesTbl}) - ?
    <c:if test="${unreg}">
        AND m.reg = 0
    </c:if>
    AND EXISTS (select sum(cnt) from ${nickhostTbl} n where m.nick=n.nick group by n.nick having sum(cnt) < ?)
    ORDER BY m.num DESC;
    <sql:param value="${checkLastNum}"/>
    <sql:param value="${msgsMax}"/>
</sql:query>


<c:set var="title">
    Поиск возможного спама
</c:set>

<title>${title}</title>

<div align="center">
    <h3>${title}</h3>

    <form action="detectspam.jsp" method="get">
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        Посдедние сообщ.:
        <select name="checkLastNum"><%-- last messages to search spam --%>
            <option value="1" <c:if test="${checkLastNum == 5000}">selected="selected"</c:if>>5'000</option>
            <option value="2" <c:if test="${checkLastNum == 10000}">selected="selected"</c:if>>10'000</option>
            <option value="3" <c:if test="${checkLastNum == 20000}">selected="selected"</c:if>>20'000</option>
        </select>

        не более сообщ.:
        <select name="msgsMax"><%-- max messages for user --%>
            <option value="1" <c:if test="${msgsMax == 5}">selected="selected"</c:if>>5</option>
            <option value="2" <c:if test="${msgsMax == 10}">selected="selected"</c:if>>10</option>
        </select>

        <input type="checkbox" name="unreg" id="unreg" <c:if test="${unreg}">checked="checked"</c:if>/>
        <label for="unreg">от unreg</label>

        <%--<input type="checkbox" name="hasUrl" id="hasUrl"
               <c:if test="${not empty param['hasUrl']}">checked="checked"</c:if>/>
        <label for="hasUrl"><fmt:message key="label.search.in.has.url"/></label>--%>

        <input type="submit" value="Показать!"/>
    </form>

    <%--todo: maybe create component--%>
    <div class="searchResOuter">
        <display:table name="${res.rows}" id="msg" htmlId="resultTable" requestURI="detectspam.jsp" class="searchRes">
            <display:column title="№"
                            class="small" style="text-align:center;width:1%;">${msg_rowNum}</display:column>
            <display:column title="Тема" style="width:67%">
                <a href="http://${site.SITE_URL}${site.READ_QUERY}${msg.num}">
                    <c:if test="${not empty msg.topic and msg.topic != 'без темы'}">[${msg.topic}]</c:if>
                    <c:out value="${msg.title}" escapeXml="false"/>
                </a>
                <small>
                    <c:if test="${empty msg.body}">(-)</c:if>
                    <c:if test="${msg.hasUrl}">(url)</c:if>
                    <c:if test="${msg.hasImg}">(pic)</c:if>
                </small>
                <a class="search"
                   href="msg?site=${site.num}&num=${msg.num}">
                    <fmt:message key="link.saved.msg"/></a>
            </display:column>
            <display:column title="Всего&nbsp;сообщ." property="count" style="text-align:center"/>
            <display:column title="Ник">
                <tiles:insertDefinition name="nick">
                    <tiles:putAttribute name="reg" value="${msg.reg}"/>
                    <tiles:putAttribute name="nick" value="${msg.nick}"/>
                    <tiles:putAttribute name="site" value="${site}"/>
                </tiles:insertDefinition>
            </display:column>
            <display:column title="Хост" class="small">
                <tiles:insertDefinition name="host">
                    <tiles:putAttribute name="host" value="${msg.host}"/>
                    <tiles:putAttribute name="site" value="${site}"/>
                </tiles:insertDefinition>
            </display:column>
            <display:column title="Дата" property="msgDate" format="{0,date,dd/MM/yyyy HH:mm}" class="small nowrap"/>
        </display:table>
    </div>
</div>
