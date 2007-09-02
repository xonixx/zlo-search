<%--
  User: gubarkov
  Date: 14.08.2007
  Time: 16:46:12
--%>
<%@ include file="import.jsp" %>
<%--<% response.setCharacterEncoding("UTF-8"); %>--%>
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
                            Title: <input type="text" name="title" value="<c:out value="${param['title']}" />" style="width:450px;" /><br/>
                            Text: <input type="text" name="body" value="<c:out value="${param['body']}" />" style="width:450px;" /><br/>
                            Nick: <input type="text" name="nick" value="<c:out value="${param['nick']}" />" style="width:200px;" />
                            Host: <input type="text" name="host" value="<c:out value="${param['host']}" />" style="width:200px;" />
                            <br/>
                            <input type="submit" value="Search"/>
                        </form>
                    </td>
                    <td></td>
                </tr>
            </table>
        </div>
<%--        <div id="results">
            <jsp:useBean id="sb" class="org.xonix.zlo.web.SearchBean" />
            <c:if test="${param['text'] ne '' and param['text'] ne null}">
                <jsp:setProperty name="sb" property="*" />
                <c:forEach var="result_line" items="${sb.searchResult}" varStatus="status">
                    <c:out value="${status.index}" />
                        &nbsp;&nbsp;<c:out value="${result_line.compName}" escapeXml="false" />
                        &nbsp;&nbsp;<c:out value="${result_line.fileName}" escapeXml="false" />
                        &nbsp;&nbsp;<c:out value="${result_line.fileSize}" escapeXml="false" />
                        <br/>
                </c:forEach>
            </c:if>
        </div>--%>
<%--    <c:out value="${requestScope['text']}" />
    <c:out value="${requestScope['searchResult']}" />--%>

    <c:if test="${requestScope['debug'] == true}">
        <br/>
        <div id="debug">
            Query: <c:out value="${requestScope['searchResult'].query}" />
        </div>
        <br/>
    </c:if>

    <c:choose>
        <c:when test="${empty requestScope['error']}">
            <display:table name="searchResult.msgs" id="row" htmlId="resultTable"
                           decorator="org.xonix.zlo.web.decorators.SearchResultLineDecorator">
                <display:column title="Num"><c:out value="${row_rowNum}" /></display:column>
                <display:column title="Title" property="title" />
                <display:column title="Nick">
                    <c:choose>
                        <c:when test="${not row.reg}">
                            <c:out value="${row.nick}" />
                        </c:when>
                        <c:otherwise>
                            <a href="http://<c:out value="${requestScope['siteRoot']}" />/?uinfo=<c:out value="${row.nick}" />">
                                <c:out value="${row.nick}" />
                            </a>
                        </c:otherwise>
                    </c:choose>
                </display:column>
                <display:column title="Date" property="date" />
            </display:table>
        </c:when>
        <c:otherwise>
            <div class="error">
                <c:out value="${requestScope['error']}" />
            </div>
        </c:otherwise>
    </c:choose>
    </body>
</html>
