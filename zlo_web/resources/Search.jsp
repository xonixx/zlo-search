<%@ page import="org.xonix.zlo.search.config.HtmlStrings" %>
<%--
  User: gubarkov
  Date: 14.08.2007
  Time: 16:46:12
--%>
<%@ include file="import.jsp" %>
<%@ include file="notDirectlyAccessible.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="backendBean" class="org.xonix.zlo.web.BackendBean" scope="session" />
<jsp:setProperty name="backendBean" property="*" /> <%-- all from request properties --%>

<jsp:useBean id="siteRoot" class="java.lang.String" scope="session" />

<jsp:useBean id="hl" class="org.xonix.zlo.web.FoundTextHighlighter" scope="session" />
<jsp:setProperty name="hl" property="highlightWords" value="${requestScope['hw']}"/>

<html>
    <head>
        <title><fmt:message key="page.title" /></title>
        <link rel="stylesheet" type="text/css" href="main.css" />
        <script type="text/javascript" src="script.js"></script>
    </head>
    <body>
        <div id="searchform">
            <table width="100%">
                <tr><td height="20px"></td></tr>
                <tr>
                    <td width="33%"></td>
                    <td>
                        <form action="search" method="get">
                            <fmt:message key="label.text" /> <input type="text" name="text" <c:if test="${not empty param['text']}">value="<c:out value="${param['text']}" />" </c:if>style="width:450px;" />
                            <fmt:message key="label.topic" /> <jsp:getProperty name="backendBean" property="topicSelector" />
                            <br/>
                            <fmt:message key="label.search" />
                            <input type="checkbox" name="inTitle" id="inTitle" <c:if test="${not empty param['inTitle']}">checked="checked"</c:if>/> <label for="inTitle"><fmt:message key="label.search.in.title" /></label>
                            <input type="checkbox" name="inBody" id="inBody" <c:if test="${not empty param['inBody']}">checked="checked"</c:if>/> <label for="inBody"><fmt:message key="label.search.in.body" /></label>
                            <br/>
                            <fmt:message key="label.search.messages" />
                            <input type="checkbox" name="reg" id="reg" <c:if test="${not empty param['reg']}">checked="checked"</c:if>/> <label for="reg"><fmt:message key="label.search.in.reg" /></label>
                            <input type="checkbox" name="hasUrl" id="hasUrl" <c:if test="${not empty param['hasUrl']}">checked="checked"</c:if>/> <label for="hasUrl"><fmt:message key="label.search.in.has.url" /></label>
                            <input type="checkbox" name="hasImg" id="hasImg" <c:if test="${not empty param['hasImg']}">checked="checked"</c:if>/> <label for="hasImg"><fmt:message key="label.search.in.has.img" /></label>
                            <br/>
                            <fmt:message key="label.nick" /> <input type="text" name="nick" <c:if test="${not empty param['nick']}">value="<c:out value="${param['nick']}" />" </c:if>style="width:200px;" />
                            <fmt:message key="label.host" /> <input type="text" name="host" <c:if test="${not empty param['host']}">value="<c:out value="${param['host']}" />" </c:if>style="width:200px;" />
                            <br/>
                            <input type="checkbox" name="dates" id="dates" onchange="changedDatesSelector();" <c:if test="${not empty param['dates']}">checked="checked"</c:if>/> <label for="dates"><fmt:message key="label.dates" /></label>
                            <fmt:message key="label.from.date" /> <input type="text" name="fd" id="fd" value="${sessionScope['fd']}" />
                            <fmt:message key="label.to.date" /> <input type="text" name="td" id="td" value="${sessionScope['td']}" />
                            <br/>
                            <fmt:message key="label.site" /> <jsp:getProperty name="backendBean" property="siteSelector" />
                            <fmt:message key="label.per.page" /> <jsp:getProperty name="backendBean" property="pageSizeSelector" />
                            <br/>
                            <input type="submit" name="submit" value="Search"/>
                        </form>
                    </td>
                    <td></td>
                </tr>
            </table>
        </div>
    <c:if test="${requestScope['debug'] == true}">
        <div id="debug">
            <pre>
                Query:          <c:out value="${sessionScope['searchResult'].query}" />
                isNewSearch:    <c:out value="${sessionScope['searchResult'].newSearch}" />
            </pre>
        </div>
    </c:if>
    <br/>
    <c:choose>
        <c:when test="${empty requestScope['error']}">
            <c:if test="${not empty requestScope['lastMsgs']}">
                <fmt:message key="label.last.saved.msg" /> <c:out value="${requestScope['lastMsgs'][0]}" /><br />
                <fmt:message key="label.last.indexed.msg" /> <c:out value="${requestScope['lastMsgs'][1]}" />
            </c:if>
            <c:if test="${not empty sessionScope['searchResult']}">
                <div class="searchResOuter">
                <display:table name="sessionScope.searchResult.paginatedList" id="msg" htmlId="resultTable"
                               decorator="org.xonix.zlo.web.decorators.SearchResultLineDecorator" requestURI="search"
                                class="searchRes">
                    <display:setProperty name="basic.msg.empty_list"><span class="pagebanner">Сообщения, соответствующие введенным критериям поиска не найдены. </span></display:setProperty>
                    <display:setProperty name="paging.banner.one_item_found"><span class="pagebanner">Найдено одно сообщение. </span></display:setProperty>
                    <display:setProperty name="paging.banner.all_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны все. </span></display:setProperty>
                    <display:setProperty name="paging.banner.some_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны с {2} по {3}. </span></display:setProperty>
                    <display:setProperty name="paging.banner.group_size">15</display:setProperty>
                    <display:setProperty name="paging.banner.full">
                        <span class="pagelinks">[<a href="{1}">Перв</a>/<a href="{2}">Пред</a>] {0} [<a href="{3}">След</a>/<a href="{4}">Последн</a>]</span>
                    </display:setProperty>
                    <display:setProperty name="paging.banner.first">
                        <span class="pagelinks">[Перв/Пред] {0} [<a href="{3}">След</a>/<a href="{4}">Последн</a>]</span>
                    </display:setProperty>
                    <display:setProperty name="paging.banner.last">
                        <span class="pagelinks">[<a href="{1}">Перв</a>/<a href="{2}">Пред</a>] {0} [След/Последн]</span>
                    </display:setProperty>

                    <display:column title="<%= HtmlStrings.HEADER_NUM.toString() %>"
                                    class="small" headerClass="head"
                                    style="text-align:center;width:1%;"><c:out value="${msg.hitId + 1}" /></display:column>
                    <display:column title="<%= HtmlStrings.HEADER_TITLE.toString() %>" headerClass="head" style="width:67%">
                        <a href="http://<c:out value="${siteRoot}" />/?read=<c:out value="${msg.num}" />">
                            <c:if test="${not empty msg.topic and msg.topic != 'без темы'}">
                                [<c:out value="${msg.topic}" />]
                            </c:if>
                            <jsp:setProperty name="hl" property="text" value="${msg.title}" />
                            <c:out value="${hl.highlightedText}" escapeXml="false" /></a>
                        <small>
                            <c:if test="${empty msg.body}">(-)</c:if>
                            <c:if test="${msg.hasUrl}">(url)</c:if>
                            <c:if test="${msg.hasImg}">(pic)</c:if>
                        </small>
                        <a class="search" href="msg?num=<c:out value="${msg.num}" /><c:if test="${not empty hl.wordsStr}">&hw=<c:out value="${hl.wordsStr}" /></c:if>"><fmt:message key="link.saved.msg" /></a>
                    </display:column>
                    <display:column title="<%= HtmlStrings.HEADER_NICK.toString() %>" headerClass="head">
                        <span class="nick">
                            <c:choose>
                                <c:when test="${not msg.reg}">
                                    <c:out value="${msg.nick}" />
                                </c:when>
                                <c:otherwise>
                                    <a href="http://<c:out value="${siteRoot}" />/?uinfo=<c:out value="${msg.nick}" />"><c:out value="${msg.nick}" escapeXml="false"/></a>
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <a class="search" href="search?nick=<c:out value="${msg.nick}" />">?</a>
                    </display:column>
                    <display:column title="<%= HtmlStrings.HEADER_HOST.toString() %>" class="small" headerClass="head">
                        <c:out value="${msg.host}" />
                        <a class="search" href="search?host=<c:out value="${msg.host}" />">?</a>
                    </display:column>
                    <display:column title="<%= HtmlStrings.HEADER_DATE.toString() %>" property="date"
                                    class="small nowrap" headerClass="head" />
                </display:table>
                </div>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="error">
                <c:out value="${requestScope['error']}" escapeXml="false" />
            </div>
        </c:otherwise>
    </c:choose>
    </body>
    <script type="text/javascript">
        changedDatesSelector();
    </script>
</html>
