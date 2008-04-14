<%@ page import="info.xonix.zlo.search.dao.Site" %>
<%--
  User: Vovan
  Date: 10.04.2008
  Time: 18:26:19
--%>
<%@ include file="WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="session" />
<jsp:setProperty name="backendBean" property="*" />

<%
    Site site;
    try {
        site = Site.getSite(Integer.parseInt(request.getParameter("site")));
    } catch (NumberFormatException e) {
        site = Site.getSite(0);
    }
%>

<c:set var="siteNum" value="<%= site.getNum() %>" />
<c:set var="siteUrl" value="<%= site.getSITE_URL() %>" />
<sql:setDataSource dataSource="<%= site.getDataSource() %>" />

<c:set var="isHost" value="${param['w'] == 'h'}" />
<c:set var="isNick" value="${param['w'] == 'n'}" />
<c:set var="text" value="${param['t']}" />

<c:set var="isAllSelected" value="${ (isHost or isNick) and not empty text }" />

<c:if test="${isAllSelected}">
    <c:choose>
        <c:when test="${isHost}">
            <sql:query var="res">
                select nick, cnt, reg from nickhost
                where host=?
                order by cnt desc;
                <sql:param>${text}</sql:param>
            </sql:query>
            <sql:query var="totalNum">
                select sum(cnt) cnt from nickhost
                where host=?
                <sql:param>${text}</sql:param>
            </sql:query>
        </c:when>
        <c:otherwise>
            <sql:query var="res">
                select host, cnt, reg from nickhost
                where nick=?
                order by cnt desc;
                <sql:param>${text}</sql:param>
            </sql:query>
            <sql:query var="totalNum">
                select sum(cnt) cnt from nickhost
                where nick=?
                <sql:param>${text}</sql:param>
            </sql:query>
        </c:otherwise>
    </c:choose>
</c:if>

<c:set var="title">
    Все ${isHost ? 'ники хоста' : 'хосты ника'} ${text} на сайте ${siteUrl}
</c:set>
<title>${title}</title>

<c:if test="${!isNick and !isHost}">
    <c:set var="isNick" value="${true}" />
</c:if>

<div align="center">
    <h3>${title}</h3>

    <form action="nickhost1.jsp" method="get">
        Сайт: <jsp:getProperty name="backendBean" property="siteSelector" /><br/>
        <input type="radio" name="w" value="n" id="nick" <c:if test="${isNick}" >checked="checked"</c:if>><label for="nick">Все хосты ника</label>
        <input type="radio" name="w" value="h" id="host" <c:if test="${isHost}" >checked="checked"</c:if>><label for="host">Все ники хоста</label><br/>
        <input type="text" name="t" style="width:250px;" <c:if test="${not empty text}"> value="${text}"</c:if> /><br/>
        <input type="submit" value="Показать!" />
    </form>

    <c:if test="${isAllSelected}">
        <c:set var="totalCnt" value="${totalNum.rows[0].cnt}" />
        Всего сообщений: ${totalCnt == null ? 0 : totalCnt} <a href="search?site=${siteNum}&${isHost ? 'host' : 'nick'}=${text}" class="search">?</a>

        <c:if test="${totalCnt > 0}">
        <display:table name="${res.rows}" id="row" htmlId="resultTable">
            <display:column headerClass="head" title="${isHost ? 'Ник' : 'Хост'}" class="center">
                <c:set var="nick"><c:out value="${row.nick}" escapeXml="false" /></c:set>
                <c:choose>
                    <c:when test="${isHost}">
                        <span class="nick">
                            <c:choose>
                                <c:when test="${row.reg}">
                                    <a href="http://${siteUrl}/?uinfo=${nick}">${nick}</a>
                                </c:when>
                                <c:otherwise>${nick}</c:otherwise>
                            </c:choose>
                        </span>
                        <a href="search?site=${siteNum}&nick=${nick}" class="search" title="поиск этого ника">?</a>
                        <a href="search?site=${siteNum}&host=${text}&nick=${nick}" class="search" title="поиск по нику и хосту">?nh</a>
                        <a href="nickhost1.jsp?site=${siteNum}&w=n&t=${nick}" class="search" title="хосты этого ника">h</a>
                    </c:when>
                    <c:otherwise>
                        ${row.host}
                        <a href="search?site=${siteNum}&host=${row.host}" class="search" title="поиск этого хоста">?</a>
                        <a href="search?site=${siteNum}&nick=${text}&host=${row.host}" class="search" title="поиск по нику и хосту">?nh</a>
                        <a href="nickhost1.jsp?site=${siteNum}&w=h&t=${row.host}" class="search" title="ники этого хоста">n</a>
                    </c:otherwise>
                </c:choose>
            </display:column>
            <display:column headerClass="head" title="Число сообщений" property="cnt" class="center" />
        </display:table>
        </c:if>
    </c:if>
</div>
<script type="text/javascript">
    document.getElementsByName("t")[0].focus();
</script>
<jsp:include page="WEB-INF/include/_ga.jsp" flush="true" />