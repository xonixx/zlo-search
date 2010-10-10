<%--
  User: Vovan
  Date: 26.04.2008
  Time: 22:24:57
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<tiles:useAttribute name="host"/>
<tiles:useAttribute name="nick"/>
<tiles:useAttribute name="site"/>
<tiles:useAttribute name="brackets"/>

<c:if test="${not empty host}">
    <c:if test="${brackets}">(</c:if><c:out value="${host}"/>
    <a class="search" href="search?site=${site.siteNumber}&host=${host}">?</a>
    <c:if test="${not empty nick}"><a href="search?site=${site.siteNumber}&nick=${nick}&host=${host}" class="search"
                                      title="поиск по нику и хосту">?nh</a></c:if>
    <a class="search" href="nickhost.jsp?site=${site.siteNumber}&w=h&t=${host}" title="ники этого хоста">n</a><c:if
        test="${brackets}">)</c:if>
</c:if>