<%--
  User: Vovan
  Date: 10.04.2008
  Time: 18:26:19
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css"/>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>
<jsp:setProperty name="backendBean" property="*"/>

<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<c:set var="siteNum" value="${site.siteNumber}"/>
<c:set var="siteUrl" value="${site.siteUrl}"/>

<c:set var="isHost" value="${param['w'] == 'h'}"/>
<c:set var="isNick" value="${param['w'] == 'n'}"/>
<c:set var="text" value="${param['t']}"/>

<c:set var="isAllSelected" value="${ (isHost or isNick) and not empty text }"/>

<c:set var="nickhostTbl">${site.name}_nickhost</c:set>

<c:if test="${isAllSelected}">
    <c:choose>
        <c:when test="${isHost}">
            <sql:query var="res">
                select nick, cnt, reg from ${nickhostTbl}
                where host=?
                order by cnt desc;
                <sql:param>${text}</sql:param>
            </sql:query>
            <sql:query var="totalNum">
                select sum(cnt) cnt from ${nickhostTbl}
                where host=?
                <sql:param>${text}</sql:param>
            </sql:query>
        </c:when>
        <c:otherwise>
            <sql:query var="res">
                select host, cnt, reg from ${nickhostTbl}
                where nick=?
                order by cnt desc;
                <sql:param>${text}</sql:param>
            </sql:query>
            <sql:query var="totalNum">
                select sum(cnt) cnt from ${nickhostTbl}
                where nick=?
                <sql:param>${text}</sql:param>
            </sql:query>
        </c:otherwise>
    </c:choose>
</c:if>

<c:set var="title">
    Все ${isHost ? 'ники хоста' : 'хосты ника'} <c:out value="${text}"/> на сайте ${siteUrl}
</c:set>
<title>${title}</title>

<c:if test="${!isNick and !isHost}">
    <c:set var="isNick" value="${true}"/>
</c:if>

<tiles:insertDefinition name="header.nickhost"/>

<div align="center">
    <h3>${title}</h3>

    <form action="nickhost.jsp" method="get">
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        <input type="radio" name="w" value="n" id="nick" <c:if test="${isNick}">checked="checked"</c:if>><label
            for="nick">Все хосты ника</label>
        <input type="radio" name="w" value="h" id="host" <c:if test="${isHost}">checked="checked"</c:if>><label
            for="host">Все ники хоста</label><br/>
        <input type="text" name="t" style="width:250px;" <c:if test="${not empty text}"> value="<c:out value="${text}"/>"</c:if> /><br/>
        <input type="submit" value="Показать!"/>
    </form>

    <c:if test="${isAllSelected}">
        <c:set var="totalCnt" value="${totalNum.rows[0].cnt}"/>
        Всего сообщений: ${totalCnt == null ? 0 : totalCnt} <a
            href="search?site=${siteNum}&${isHost ? 'host' : 'nick'}=<c:out value="${text}" />" class="search">?</a>

        <c:if test="${totalCnt > 0}">
            <display:table name="${res.rows}" id="row" htmlId="resultTable">
                <display:column title="${isHost ? 'Ник' : 'Хост'}" class="center">
                    <c:choose>
                        <c:when test="${isHost}">
                            <tiles:insertDefinition name="nick">
                                <tiles:putAttribute name="reg" value="${row.reg}"/>
                                <tiles:putAttribute name="nick" value="${row.nick}"/>
                                <tiles:putAttribute name="host" value="${text}"/>
                                <tiles:putAttribute name="site" value="${site}"/>
                            </tiles:insertDefinition>
                        </c:when>
                        <c:otherwise>
                            <tiles:insertDefinition name="host">
                                <tiles:putAttribute name="host" value="${row.host}"/>
                                <tiles:putAttribute name="nick" value="${text}"/>
                                <tiles:putAttribute name="site" value="${site}"/>
                            </tiles:insertDefinition>
                        </c:otherwise>
                    </c:choose>
                </display:column>
                <display:column title="Число сообщений" property="cnt" class="center"/>
            </display:table>
        </c:if>
    </c:if>
</div>
<script type="text/javascript">
    document.getElementsByName("t")[0].focus();
</script>

<tiles:insertDefinition name="ga"/>