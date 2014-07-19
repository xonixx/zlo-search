<%@ page import="info.xonix.zlo.search.logic.MessageLogic" %>
<%@ page import="info.xonix.zlo.search.model.Message" %>
<%@ page import="java.util.Date" %>
<%--
  User: gubarkov
  Date: 14.08.2007
  Time: 16:46:12
--%>
<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="/WEB-INF/jsp/setSite.jsp" %>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>
<jsp:setProperty name="backendBean" property="*"/>
<%-- all from request properties --%>

<jsp:useBean id="hl" class="info.xonix.zlo.search.FoundTextHighlighter" scope="request"/>
<jsp:setProperty name="hl" property="hlClass" value="hl0"/>
<jsp:setProperty name="hl" property="highlightWords" value="${requestScope['hw']}"/>

<c:set var="isError" value="${not empty requestScope['error']}"/>
<c:set var="isSearchResultPresent" value="${not empty requestScope['searchResult']}"/>
<c:set var="rssUrl" value='<%= String.format("search?rss&%s", request.getQueryString()) %>'/>
<c:set var="rssLinkHtml"><a href="${rssUrl}" title="RSS для этого запроса">
    <img src="feed-icon-14x14.png" alt="RSS для этого запроса"/></a>
</c:set>

<!-- ПРЕВЕД -->
<html ng-app="search">
<head>
    <title><fmt:message key="page.title"/></title>
    <%@ include file="/WEB-INF/jsp/commonJsCss.jsp" %>
    <c:if test="${not isError and isSearchResultPresent}">
        <link rel="alternate" type="application/rss+xml" title="RSS" href="${rssUrl}">
    </c:if>

    <%--jQuery--%>
    <link rel="stylesheet" type="text/css" href="lib/jq/themes/base/jquery.ui.core.css"/>
    <link rel="stylesheet" type="text/css" href="lib/jq/themes/base/jquery.ui.theme.css"/>
    <link rel="stylesheet" type="text/css" href="lib/jq/themes/base/jquery.ui.autocomplete.css"/>

    <script type="text/javascript" src="lib/jq/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="lib/jq/jquery.ui.core.min.js"></script>
    <script type="text/javascript" src="lib/jq/jquery.ui.widget.min.js"></script>
    <script type="text/javascript" src="lib/jq/jquery.ui.position.min.js"></script>
    <script type="text/javascript" src="lib/jq/jquery.ui.autocomplete.min.js"></script>
    <%--jQuery ends--%>

    <script type="text/javascript" src="lib/angular/angular${minSuffix}.js"></script>

    <script type="text/javascript" src="js/script.js?${version}"></script>
    <script type="text/javascript" src="pic/lulz/db.js"></script>
</head>
<body>
<tiles:insertDefinition name="header.search"/>
<tiles:insertDefinition name="suggest.anotherBrowser"/>

<div id="searchform" align="center" style="padding-top:5px;">
    <form action="search" method="get" id="searchFrm">
        <table cellpadding="0" cellspacing="0">
            <tr>
                <td>
                    <input type="radio" name="st" id="st1" value="all"
                           <c:if test="${param['st'] == 'all'}">checked="checked"</c:if> />
                    <label for="st1"><fmt:message key="label.search.all"/></label>
                    <input type="radio" name="st" id="st3" value="adv"
                           <c:if test="${param['st'] == 'adv'}">checked="checked"</c:if> />
                    <label for="st3"><fmt:message key="label.search.advanced"/></label>
                    <input type="radio" name="st" id="st2" value="exct"
                           <c:if test="${param['st'] == 'exct'}">checked="checked"</c:if> />
                    <label for="st2"><fmt:message key="label.search.exact.phrase"/></label>
                    <br/>
                    <fmt:message key="label.text"/>
                    <input type="text" name="text" id="text"
                           <c:if test="${not empty param['text']}">value='<c:out value="${param['text']}" />'
                    </c:if> style="width:450px;"/>
                </td>
                <td valign="bottom" style="padding-left:10px;">
                    <fmt:message key="label.topic"/>
                    <jsp:getProperty name="backendBean" property="topicSelector"/>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <fmt:message key="label.search"/>
                    <input type="checkbox" name="inTitle" id="inTitle"
                           <c:if test="${not empty param['inTitle']}">checked="checked"</c:if>/>
                    <label for="inTitle"><fmt:message key="label.search.in.title"/></label>

                    <input type="checkbox" name="inBody" id="inBody"
                           <c:if test="${not empty param['inBody']}">checked="checked"</c:if>/>
                    <label for="inBody"><fmt:message key="label.search.in.body"/></label>
                    <br/>
                    <fmt:message key="label.search.messages"/>
                    <input type="checkbox" name="reg" id="reg"
                           <c:if test="${not empty param['reg']}">checked="checked"</c:if>/>
                    <label for="reg"><fmt:message key="label.search.in.reg"/></label>

                    <input type="checkbox" name="hasUrl" id="hasUrl"
                           <c:if test="${not empty param['hasUrl']}">checked="checked"</c:if>/>
                    <label for="hasUrl"><fmt:message key="label.search.in.has.url"/></label>

                    <input type="checkbox" name="hasImg" id="hasImg"
                           <c:if test="${not empty param['hasImg']}">checked="checked"</c:if>/>
                    <label for="hasImg"><fmt:message key="label.search.in.has.img"/></label>

                    <c:if test="<%= adapter.supportsParents() %>">
                        <input type="checkbox" name="isRoot" id="isRoot"
                               <c:if test="${not empty param['isRoot']}">checked="checked"</c:if>/>
                        <label for="isRoot"><fmt:message key="label.search.in.is.root"/></label>
                    </c:if>
                    <br/>
                    <fmt:message key="label.nick"/> <input type="text" name="nick"
                                                           <c:if test="${not empty param['nick']}">value="<c:out value="${param['nick']}" />"
                                                           </c:if>style="width:200px;"/>
                    <fmt:message key="label.host"/> <input type="text" name="host"
                                                           <c:if test="${not empty param['host']}">value="<c:out value="${param['host']}" />"
                                                           </c:if>style="width:200px;"/>
                    <br/>
                    <input type="checkbox" name="dates" id="dates" ng-model="searchByDates"
                           ng-init="searchByDates=${not empty param['dates']}"/>
                    <label for="dates"><fmt:message key="label.dates"/></label>

                    <fmt:message key="label.from.date"/> <input type="text" name="fd" id="fd" ng-disabled="!searchByDates"
                                                                value="<c:out value="${requestScope['fd']}" />"/>
                    <fmt:message key="label.to.date"/> <input type="text" name="td" id="td" ng-disabled="!searchByDates"
                                                              value="<c:out value="${requestScope['td']}" />"/>
                    <br/>
                    <fmt:message key="label.forum"/>
                    <jsp:getProperty name="backendBean" property="siteSelector"/>
                    <fmt:message key="label.per.page"/>
                    <jsp:getProperty name="backendBean" property="pageSizeSelector"/>

                    <fmt:message key="label.sort"/>
                    <jsp:getProperty name="backendBean" property="sortSelector"/>
                    <br/>
                    <input type="submit" id="submitBtn" name="submitBtn" value="<fmt:message key="button.search"/>"/>
                    <br/>
                    <c:if test="${not isError and not empty requestScope['lastMsgs']}">
                        <c:set var="lastSavedN" value="${requestScope['lastMsgs'][0]}"/>
                        <c:set var="lastIndexedN" value="${requestScope['lastMsgs'][1]}"/>
                        <table cellspacing="5" class="small">
                            <tr>
                                <td><fmt:message key="label.last.saved.msg"/></td>
                                <td>${lastSavedN}</td>
                                <td>(<fmt:formatDate value="${requestScope['lastMsgs_dates'][0]}"
                                                     pattern="dd/MM/yyyy HH:mm"/>)
                                </td>
                            </tr>
                            <tr>
                                <td><fmt:message key="label.last.indexed.msg"/></td>
                                <td <c:if test="${lastSavedN - lastIndexedN > 1000}">style="color: red"</c:if>>
                                ${lastIndexedN}</td>
                                <td>(<fmt:formatDate value="${requestScope['lastMsgs_dates'][1]}"
                                                     pattern="dd/MM/yyyy HH:mm"/>)
                                </td>
                            </tr>
                        </table>
                    </c:if>
                </td>
            </tr>
        </table>
    </form>
</div>
<c:if test="${requestScope['debug'] == true}">
    <div id="debug">
            <pre>
                Query:          ${requestScope['searchResult'].query}
                isNewSearch:    ${requestScope['searchResult'].newSearch}
            </pre>
    </div>
</c:if>
<c:choose>
    <c:when test="${not isError}">
        <c:if test="${isSearchResultPresent}">
            <c:set var="paginatedList" value="${requestScope['paginatedList']}"/>
            <c:set var="hasHosts" value="${paginatedList.hasHosts}"/>
            <c:set var="hasNicks" value="${paginatedList.hasNicks}"/>

            <div class="searchResOuter">
                <%@ include file="savedMsgLnk.jsp" %>

                <display:table name="${paginatedList}" id="msg" htmlId="resultTable"
                               decorator="info.xonix.zlo.web.decorators.SearchResultLineDecorator" requestURI="search"
                               class="searchRes">
                    <c:if test="${msg != null}">
                        <c:set var="siteRootUrl" value="${adapter.forumUrl}"/>

                        <c:if test="${not msg.ok}">
                            <c:set target="${msg}" property="title" value="${msg.status}"/>
                            <c:set target="${msg}" property="nick" value=""/>
                            <c:set target="${msg}" property="host" value=""/>
                            <c:set target="${msg}" property="date" value="<%=new Date(0)%>"/>
                        </c:if>
                    </c:if>

                    <display:setProperty name="basic.msg.empty_list"><span class="pagebanner">Сообщения, соответствующие введенным критериям поиска не найдены. </span></display:setProperty>
                    <display:setProperty name="paging.banner.one_item_found"><span class="pagebanner">Найдено одно сообщение. ${rssLinkHtml}</span></display:setProperty>
                    <display:setProperty name="paging.banner.all_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны все. ${rssLinkHtml}</span></display:setProperty>
                    <display:setProperty name="paging.banner.some_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны с {2} по {3}. </span></display:setProperty>
                    <display:setProperty name="paging.banner.group_size" value="15"/>
                    <display:setProperty name="paging.banner.onepage" value=""/>
                    <display:setProperty name="paging.banner.placement" value="both"/>
                    <display:setProperty name="paging.banner.full">
                        <span class="pagelinks">[<a href="{1}">Перв</a>/<a href="{2}">Пред</a>] {0} [<a
                                href="{3}">След</a>/<a href="{4}">Последн</a>] ${rssLinkHtml}</span>
                    </display:setProperty>
                    <display:setProperty name="paging.banner.first">
                        <span class="pagelinks">[Перв/Пред] {0} [<a href="{3}">След</a>/<a
                                href="{4}">Последн</a>] ${rssLinkHtml}</span>
                    </display:setProperty>
                    <display:setProperty name="paging.banner.last">
                        <span class="pagelinks">[<a href="{1}">Перв</a>/<a
                                href="{2}">Пред</a>] {0} [След/Последн] ${rssLinkHtml}</span>
                    </display:setProperty>

                    <display:column title="№"
                                    class="small" style="text-align:center;width:1%;">${msg.hitId + 1}</display:column>
                    <display:column title="Тема" style="width:67%">
                        <a href="<%= adapter.prepareMessageUrl(((Message)msg).getNum()) %>">
                            <c:if test="${not empty msg.topic and msg.topic != 'без темы'}">[${msg.topic}]</c:if>
                            <jsp:setProperty name="hl" property="text" value="${msg.title}"/>
                            <c:out value="${hl.highlightedText}" escapeXml="false"/></a>
                        <small>
                            <c:if test="${empty msg.body}">(-)</c:if>
                            <c:if test="<%= MessageLogic.hasUrl((Message) msg)%>">(url)</c:if>
                            <c:if test="<%= MessageLogic.hasImg((Message) msg, forumId) %>">(pic)</c:if>
                        </small>
                        <a class="search"
                           href="msg?site=${descriptor.forumIntId}&num=${msg.num}<c:if test="${not empty hl.wordsStr}">&hw=${hl.wordsStr}</c:if>"><fmt:message
                                key="link.saved.msg"/></a>
                    </display:column>
                    <c:if test="${hasNicks}">
                        <display:column title="Ник">
                            <tiles:insertDefinition name="nick">
                                <tiles:putAttribute name="reg" value="${msg.reg}"/>
                                <tiles:putAttribute name="nick" value="${msg.nick}"/>
                                <tiles:putAttribute name="userId" value="${msg.userId}"/>
                            </tiles:insertDefinition>
                        </display:column>
                    </c:if>
                    <c:if test="${hasHosts}">
                        <display:column title="Хост" class="small">
                            <tiles:insertDefinition name="host">
                                <tiles:putAttribute name="host" value="${msg.host}"/>
                            </tiles:insertDefinition>
                        </display:column>
                    </c:if>
                    <display:column title="Дата" property="date" class="small nowrap"/>
                </display:table>
            </div>
        </c:if>
    </c:when>
    <c:otherwise>
        <div class="error">
            <c:out value="${requestScope['error']}" escapeXml="false"/>
        </div>
    </c:otherwise>
</c:choose>
</body>
<tiles:insertDefinition name="ga"/>
</html>
<!-- ПАКА -->
