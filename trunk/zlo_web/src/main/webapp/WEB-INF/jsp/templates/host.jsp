<%--
  User: Vovan
  Date: 26.04.2008
  Time: 22:24:57
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<tiles:useAttribute name="host"/>
<tiles:useAttribute name="nick"/>
<tiles:useAttribute name="brackets"/>

<jsp:useBean id="forumIntId" type="java.lang.Integer" scope="request"/>
<jsp:useBean id="adapter" type="info.xonix.zlo.search.logic.forum_adapters.ForumAdapter" scope="request"/>

<c:if test="${not empty host}">
    <c:if test="${brackets}">(</c:if><c:out value="${host}"/>
    <a class="search" href="search?site=${forumIntId}&host=${host}">?</a>
    <c:if test="${not empty nick}"><a href="search?site=${forumIntId}&nick=${nick}&host=${host}" class="search"
                                      title="поиск по нику и хосту">?nh</a></c:if>
    <a class="search" href="nickhost.jsp?site=${forumIntId}&w=h&t=${host}" title="ники этого хоста">n</a><c:if
        test="${brackets}">)</c:if>
</c:if>