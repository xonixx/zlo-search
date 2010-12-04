<%--
  User: Vovan
  Date: 26.04.2008
  Time: 19:50:59
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<tiles:useAttribute name="reg"/>
<tiles:useAttribute name="nick"/>
<tiles:useAttribute name="host"/>
<tiles:useAttribute name="site"/>

<c:if test="${not empty nick}">
    <c:set var="nickEscaped"><c:out value="${nick}"/></c:set>
    <c:set var="nickUrlencoded"><c:out value="${xonix:urlencode(nick)}"/></c:set>
    <span class="nick">
        <c:choose>
            <c:when test="${not reg}">${nickEscaped}</c:when>
            <c:otherwise>
                <a href="http://${site.siteUrl}/?uinfo=${nickUrlencoded}">${nickEscaped}</a>
            </c:otherwise>
        </c:choose>
    </span>
    <a class="search" href="search?site=${site.siteNumber}&nick=${nickUrlencoded}">?</a>
    <c:if test="${not empty host}">
        <a href="search?site=${site.siteNumber}&host=${host}&nick=${nickUrlencoded}"
           class="search" title="поиск по нику и хосту">?nh</a>
    </c:if>
    <c:if test="${not site.noHost}">
        <a class="search" href="nickhost.jsp?site=${site.siteNumber}&w=n&t=${nickUrlencoded}"
           title="хосты этого ника">h</a>
    </c:if>
</c:if>
