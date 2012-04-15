<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ page import="info.xonix.zlo.search.daemon.Daemon" %>
<%@ page import="info.xonix.zlo.search.logic.SearchLogic" %>
<%@ page import="info.xonix.zlo.search.logic.SearchLogicImpl" %>
<%@ page import="info.xonix.zlo.search.progs.OptimizeAllIndexes" %>
<%@ page import="info.xonix.zlo.search.utils.SysUtils" %>
<%@ page import="info.xonix.zlo.web.logic.AdminLogic" %>
<%@ page import="info.xonix.zlo.search.index.IndexManager" %>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/restrictAccess.jsp" %>

<link rel="stylesheet" type="text/css" href="main.css"/>
<link rel="stylesheet" type="text/css" href="admin.css"/>

<title>Admin area</title>

<tiles:insertDefinition name="header.admin"/>

<div class="content">
    <%
        request.setAttribute("mb", AdminLogic.MEGABYTE);
    %>

    <form action="admin.jsp" method="post">
        <%--<input type="submit" name="command" value="Optimize"/>--%>
        <input type="submit" name="command" value="GC"/>
    </form>
    
    <a href="detectspam.jsp">Spam?</a>
    <a href="db.jsp">DB</a>

    <%
        if ("POST".equals(request.getMethod())) {
            final String command = request.getParameter("command");
            if ("Optimize".equals(command)) {
                new OptimizeAllIndexes().optimizeRestartDaemons();

                response.sendRedirect("admin.jsp");
                return;
            } else if ("GC".equals(command)) {
                SysUtils.gc();
                response.sendRedirect("admin.jsp");
                return;
            }
        }
    %>

    <table>
        <tr>
            <th>Memory usage</th>
            <th>Versions</th>
        </tr>
        <tr>
            <td align="left" valign="top">
                <display:table id="line" name="<%= AdminLogic.memoryReport().entrySet() %>">
                    <display:setProperty name="css.table">reportTbl</display:setProperty>

                    <display:column property="key"/>
                    <display:column property="value" format="{0,number,#.##}"/>
                </display:table>
            </td>
            <td>
                <display:table name="<%= AdminLogic.versionsReport(application).entrySet()%>" class="reportTbl">
                    <display:column property="key"/>
                    <display:column property="value"/>
                </display:table>
            </td>
        </tr>
    </table>

    <display:table id="d" name="<%= Daemon.getDaemons() %>">
        <display:caption>Daemons</display:caption>
        <display:column title="Forum">${d.forumId}</display:column>
        <display:column title="Type">
            <%= d.getClass().getSimpleName() %>
        </display:column>
        <display:column title="State">${d.daemonState}</display:column>
        <display:column title="Last Exception">
            <c:if test="${not empty d.lastException}">
                ${d.lastException.message} (<fmt:formatDate value="${d.lastExceptionTime}" pattern="dd.MM.yy hh:mm"/>)
            </c:if>
        </display:column>
    </display:table>

    <display:table id="forumId" name="<%= GetForum.ids() %>">
        <c:set var="adapter" value="<%= GetForum.adapter((String) forumId) %>"/>
        <c:set var="indexManager" value="<%= IndexManager.get((String) forumId) %>"/>

        <display:caption>Sites</display:caption>

        <display:column title="#">${forumId_rowNum}</display:column>
        <display:column title="Name">${forumId}</display:column>
        <display:column title="Url"><a href="${adapter.forumUrl}">${adapter.forumUrl}</a></display:column>
        <display:column title="Index">${indexManager.indexDir}</display:column>
        <display:column title="Index Size">
            <fmt:formatNumber value="${indexManager.indexSize / mb}" pattern="#.##"/>
        </display:column>
    </display:table>
</div>