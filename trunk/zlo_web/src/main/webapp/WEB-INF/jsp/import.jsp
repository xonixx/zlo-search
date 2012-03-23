<%@ page import="info.xonix.zlo.search.spring.AppSpringContext" %>
<%@ page import="info.xonix.zlo.web.servlets.BaseServlet" %>

<%@ include file="import_taglibs.jsp" %>

<fmt:setBundle basename="info.xonix.zlo.search.config.config"/>
<fmt:setLocale value="ru_RU" scope="request"/>

<sql:setDataSource dataSource='<%= AppSpringContext.get("dataSource") %>'/>
