<%@ page import="info.xonix.zlo.search.spring.AppSpringContext" %>
<%@ page import="info.xonix.zlo.web.servlets.BaseServlet" %>

<%@ include file="import_taglibs.jsp" %>

<fmt:setBundle basename="info.xonix.zlo.web.i18n.messages"/>
<fmt:setLocale value="ru_RU" scope="request"/>

<sql:setDataSource dataSource='<%= AppSpringContext.get("dataSource") %>'/>
