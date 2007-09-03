<%--
  User: gubarkov
  Date: 14.08.2007
  Time: 16:46:12
--%>
<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<jsp:useBean id="backendBeen" class="org.xonix.zlo.web.BackendBeen" scope="session" />
<jsp:setProperty name="backendBeen" property="*" /> <%-- all from request --%>

<c:if test="${not empty requestScope['site']}">
    <jsp:setProperty name="backendBeen" property="site" value="${requestScope['site']}" />
</c:if>

<html>
    <head>
        <title>Search Files</title>
        <link rel="stylesheet" type="text/css" href="main.css" />
    </head>
    <body>
        <div id="searchform">
            <table width="100%">
                <tr><td height="20px"></td></tr>
                <tr>
                    <td width="33%"></td>
                    <td>
                        <form action="search" method="get">
                            Title: <input type="text" name="title" <c:if test="${not empty param['title']}">value="<c:out value="${param['title']}" />" </c:if>style="width:450px;" />
                            Topic: <jsp:getProperty name="backendBeen" property="topicSelector" />
                            <br/>
                            Text: <input type="text" name="body" <c:if test="${not empty param['body']}">value="<c:out value="${param['body']}" />" </c:if>style="width:450px;" /><br/>
                            Nick: <input type="text" name="nick" <c:if test="${not empty param['nick']}">value="<c:out value="${param['nick']}" />" </c:if>style="width:200px;" />
                            Host: <input type="text" name="host" <c:if test="${not empty param['host']}">value="<c:out value="${param['host']}" />" </c:if>style="width:200px;" />
                            <br/>
                            Site: <jsp:getProperty name="backendBeen" property="siteSelector" />
                            <input type="submit" value="Search"/>
                        </form>
                    </td>
                    <td></td>
                </tr>
            </table>
        </div>
    <c:if test="${requestScope['debug'] == true}">
        <br/>
        <div id="debug">
            Query: <c:out value="${requestScope['searchResult'].query}" />
        </div>
        <br/>
    </c:if>

    <c:choose>
        <c:when test="${empty requestScope['error']}">
            <c:if test="${not empty requestScope['searchResult']}">
                <display:table name="searchResult.msgs" id="msg" htmlId="resultTable"
                               decorator="org.xonix.zlo.web.decorators.SearchResultLineDecorator">
                    <display:column title="Num"><c:out value="${msg_rowNum}" /></display:column>
                    <display:column title="Title">
                        <a href="http://<c:out value="${requestScope['siteRoot']}" />/?read=<c:out value="${msg.num}" />">
                            <c:if test="${not empty msg.topic and msg.topic != 'Без темы'}">
                                [<c:out value="${msg.topic}" />]
                            </c:if>
                            <c:out value="${msg.title}" />
                        </a>
                    </display:column>
                    <display:column title="Nick">
                        <c:choose>
                            <c:when test="${not msg.reg}">
                                <c:out value="${msg.nick}" />
                            </c:when>
                            <c:otherwise>
                                <a href="http://<c:out value="${requestScope['siteRoot']}" />/?uinfo=<c:out value="${msg.nick}" />">
                                    <c:out value="${msg.nick}" />
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </display:column>
                    <display:column title="Host" property="host" />
                    <display:column title="Date" property="date" />
                </display:table>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="error">
                <c:out value="${requestScope['error']}" />
            </div>
        </c:otherwise>
    </c:choose>
    </body>
</html>
