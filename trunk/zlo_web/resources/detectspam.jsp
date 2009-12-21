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

<sql:query var="res">
    SELECT m.* from ${messagesTbl} m
    where m.num > (select max(num) from ${messagesTbl}) - ?
    and exists (select sum(cnt) from ${nickhostTbl} n where m.nick=n.nick group by n.nick having sum(cnt) < ?);
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

        не более сообщ.
        <select name="msgsMax"><%-- max messages for user --%>
            <option value="1" <c:if test="${msgsMax == 5}">selected="selected"</c:if>>5</option>
            <option value="2" <c:if test="${msgsMax == 10}">selected="selected"</c:if>>10</option>
        </select>

        <input type="checkbox" name="reg" id="reg" <c:if test="${not empty param['reg']}">checked="checked"</c:if>/>
        <label for="reg"><fmt:message key="label.search.in.reg"/></label>
        <input type="checkbox" name="hasUrl" id="hasUrl"
               <c:if test="${not empty param['hasUrl']}">checked="checked"</c:if>/> <label for="hasUrl"><fmt:message
            key="label.search.in.has.url"/></label>

        <input type="submit" value="Показать!"/>
    </form>

    <%--todo: maybe create component--%>
    <div class="searchResOuter">
        <display:table name="${res.rows}" id="msg" htmlId="resultTable" requestURI="detectspam.jsp" class="searchRes">
            <%--<c:set var="site" value="${msg.site}"/>--%>

            <%--<display:setProperty name="basic.msg.empty_list"><span class="pagebanner">Сообщения, соответствующие введенным критериям поиска не найдены. </span></display:setProperty>
            <display:setProperty name="paging.banner.one_item_found"><span
                    class="pagebanner">Найдено одно сообщение. ${rssLinkHtml}</span></display:setProperty>
            <display:setProperty name="paging.banner.all_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны все. ${rssLinkHtml}</span></display:setProperty>
            <display:setProperty name="paging.banner.some_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны с {2} по {3}. </span></display:setProperty>
            <display:setProperty name="paging.banner.group_size" value="15"/>
            <display:setProperty name="paging.banner.onepage" value=""/>
            <display:setProperty name="paging.banner.placement" value="both"/>
            <display:setProperty name="paging.banner.full">
                <span class="pagelinks">[<a href="{1}">Перв</a>/<a href="{2}">Пред</a>] {0} [<a href="{3}">След</a>/<a
                        href="{4}">Последн</a>] ${rssLinkHtml}</span>
            </display:setProperty>
            <display:setProperty name="paging.banner.first">
                <span class="pagelinks">[Перв/Пред] {0} [<a href="{3}">След</a>/<a
                        href="{4}">Последн</a>] ${rssLinkHtml}</span>
            </display:setProperty>
            <display:setProperty name="paging.banner.last">
                <span class="pagelinks">[<a href="{1}">Перв</a>/<a
                        href="{2}">Пред</a>] {0} [След/Последн] ${rssLinkHtml}</span>
            </display:setProperty>--%>

            <display:column title="№"
                            class="small" style="text-align:center;width:1%;">${msg_rowNum + 1}</display:column>
            <display:column title="Тема" style="width:67%">
                <a href="http://${site.SITE_URL}${site.READ_QUERY}${msg.num}">
                    <c:if test="${not empty msg.topic and msg.topic != 'без темы'}">[${msg.topic}]</c:if>
                        <%--<jsp:setProperty name="hl" property="text" value="${msg.title}"/>
                      <c:out value="${hl.highlightedText}" escapeXml="false"/></a>--%>
                    <c:out value="${msg.title}" escapeXml="false"/>
                </a>
                <small>
                    <c:if test="${empty msg.body}">(-)</c:if>
                    <c:if test="${msg.hasUrl}">(url)</c:if>
                    <c:if test="${msg.hasImg}">(pic)</c:if>
                </small>
                <a class="search"
                   href="msg?site=${msg.site.num}&num=${msg.num}<%--<c:if test="${not empty hl.wordsStr}">&hw=${hl.wordsStr}</c:if>--%>">
                    <fmt:message key="link.saved.msg"/></a>
            </display:column>
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
            <display:column title="Дата" property="date" class="small nowrap"/>
        </display:table>
    </div>
</div>
