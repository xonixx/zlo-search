<%@ page import="info.xonix.zlo.search.daemon.Daemon" %>
<%@ page import="info.xonix.zlo.search.logic.SiteLogic" %>
<%@ page import="info.xonix.zlo.search.logic.ZloSearcher" %>
<%@ page import="info.xonix.zlo.search.model.Site" %>
<%@ page import="info.xonix.zlo.search.progs.OptimizeAllIndexes" %>
<%@ page import="info.xonix.zlo.web.utils.RequestUtils" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<%!
    SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
    ZloSearcher zloSearcher = AppSpringContext.get(ZloSearcher.class);
%>

<%
    if (!RequestUtils.isLocalIp(request)) {
        response.sendError(404);
        return;
    }
%>

<link rel="stylesheet" type="text/css" href="main.css"/>
<title>Admin area</title>

<tiles:insertDefinition name="header.admin"/>

<style type="text/css">
    .reportTbl {
        border: solid 1px black;
    }

    .reportTbl .odd {
        background-color: white;
    }

    .reportTbl .even {
        background-color: khaki;
    }

    .reportTbl th {
        text-align: left;
        background-color: gold;
    }
</style>

<div class="content">
    <form action="admin.jsp" method="post">
        <input type="submit" name="command" value="Optimize"/>
    </form>

    <%
        if ("POST".equals(request.getMethod())) {
            if ("Optimize".equals(request.getParameter("command"))) {
                new OptimizeAllIndexes().optimizeRestartDaemons();

                response.sendRedirect("admin.jsp");
                return;
            }
        }
    %>

    <%
        Runtime runtime = Runtime.getRuntime();

        float mb = 1024 * 1024f;

        Map<String, Float> memory = new LinkedHashMap<String, Float>();
        memory.put("Free", runtime.freeMemory() / mb);
        memory.put("Total", runtime.totalMemory() / mb);
        memory.put("Max", runtime.maxMemory() / mb);

        request.setAttribute("mb", mb);
        request.setAttribute("memory", memory);
    %>

    <display:table id="line" name="<%= memory.entrySet() %>">
        <display:caption>Memory usage</display:caption>
        <display:setProperty name="css.table">reportTbl</display:setProperty>

        <display:column property="key"/>
        <display:column property="value" format="{0,number,#.##}"/>
        <%--<display:column property="value" />--%>
    </display:table>

    <display:table id="d" name="<%= Daemon.getDaemons() %>">
        <display:caption>Daemons</display:caption>
        <display:column title="Type">
            <%= d.getClass().getSimpleName() %>
        </display:column>
        <display:column title="Site">${d.siteName}</display:column>
        <display:column title="State">${d.daemonState}</display:column>
        <display:column title="Last Exception">
            <c:if test="${not empty d.lastException}">
                ${d.lastException.message} (<fmt:formatDate value="${d.lastExceptionTime}" pattern="dd.MM.yy hh:mm"/>)
            </c:if>
        </display:column>
    </display:table>

    <display:table id="site" name="<%= siteLogic.getSites() %>">
        <c:set var="dis" value="<%= zloSearcher.getDoubleIndexSearcher((Site) site) %>"/>
        <display:caption>Sites</display:caption>

        <display:column title="#">${site_rowNum}</display:column>
        <display:column title="Name" property="name"/>
        <display:column title="Url"><a href="http://${site.siteUrl}/">${site.siteUrl}</a></display:column>
        <display:column title="Index">${dis.indexesDir}</display:column>
        <display:column title="Renew Date"><fmt:formatDate value="${dis.renewDate}"
                                                           pattern="dd.MM.yy hh:mm"/></display:column>
        <display:column title="Big index size"><fmt:formatNumber value="${dis.bigIndexSize / mb}"
                                                                 pattern="#.##"/></display:column>
        <display:column title="Small index size"><fmt:formatNumber value="${dis.smallIndexSize / mb}"
                                                                   pattern="#.##"/></display:column>


    </display:table>
</div>