<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<% request.setAttribute("site", BaseServlet.getSite(request)); %>
<jsp:useBean id="site" type="info.xonix.zlo.search.domainobj.Site" scope="request"/>
