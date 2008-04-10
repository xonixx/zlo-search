<%@ page import="info.xonix.zlo.search.dao.Site" %>
<%--
  User: Vovan
  Date: 10.04.2008
  Time: 18:26:19
--%>
<%@ include file="WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<%
    Site site;
    try {
        site = Site.getSite(Integer.parseInt(request.getParameter("site")));
    } catch (NumberFormatException e) {
        site = Site.getSite(0);
    }
%>

<sql:setDataSource dataSource="<%= site.getDataSource() %>" />

<form action="stats.jsp" method="get">
    Сайт: <jsp:getProperty name="backendBean" property="siteSelector" /><br/>

</form>