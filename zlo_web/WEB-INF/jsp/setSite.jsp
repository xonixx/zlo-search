<%@ page import="info.xonix.zlo.web.servlets.BaseServlet" %>

<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<% request.setAttribute("site", BaseServlet.getSite(request)); %>
<jsp:useBean id="site" type="info.xonix.zlo.search.dao.Site" scope="request" />
