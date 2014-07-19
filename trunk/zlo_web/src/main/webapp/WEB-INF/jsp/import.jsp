<%@ page import="info.xonix.zlo.search.spring.AppSpringContext" %>
<%@ page import="info.xonix.zlo.web.servlets.BaseServlet" %>
<%@ page import="info.xonix.zlo.search.VersionManager" %>

<%@ include file="import_taglibs.jsp" %>

<fmt:setBundle basename="info.xonix.zlo.web.i18n.messages"/>
<fmt:setLocale value="ru_RU" scope="request"/>

<sql:setDataSource dataSource='<%= AppSpringContext.get("dataSource") %>'/>
<c:set var="minSuffix" value='<%= "1".equals(System.getenv("development")) ? "" : ".min" %>'/>
<c:set var="version" value='<%= VersionManager.getApplicationVersion() %>'/>
