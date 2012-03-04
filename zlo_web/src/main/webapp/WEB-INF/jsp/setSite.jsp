<%@ page import="info.xonix.zlo.search.config.forums.ForumDescriptor" %>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%
    final ForumDescriptor forumDescriptor = BaseServlet.getSite(request);
    request.setAttribute("descriptor", forumDescriptor);
    request.setAttribute("forumIntId", forumDescriptor.getForumIntId());
    request.setAttribute("forumId", forumDescriptor.getForumId());
    request.setAttribute("adapter", forumDescriptor.getForumAdapter());
%>
<jsp:useBean id="forumIntId" type="java.lang.Integer" scope="request"/>
<jsp:useBean id="forumId" type="java.lang.String" scope="request"/>
<jsp:useBean id="descriptor" type="info.xonix.zlo.search.config.forums.ForumDescriptor" scope="request"/>
<jsp:useBean id="adapter" type="info.xonix.zlo.search.logic.forum_adapters.ForumAdapter" scope="request"/>
