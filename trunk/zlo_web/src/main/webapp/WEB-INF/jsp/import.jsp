<%@ page import="info.xonix.zlo.search.spring.AppSpringContext" %>
<%@ page import="info.xonix.zlo.web.servlets.BaseServlet" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="xonix" uri="http://xonix.info" %>

<fmt:setBundle basename="info.xonix.zlo.search.config.config"/>
<fmt:setLocale value="ru_RU" scope="request"/>

<sql:setDataSource dataSource='<%= AppSpringContext.get("dataSource") %>'/>
