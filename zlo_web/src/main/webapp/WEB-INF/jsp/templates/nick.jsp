<%--
  User: Vovan
  Date: 26.04.2008
  Time: 19:50:59
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<tiles:useAttribute name="reg"/>
<tiles:useAttribute name="nick"/>
<tiles:useAttribute name="host"/>

<jsp:useBean id="forumIntId" type="java.lang.Integer" scope="request"/>
<jsp:useBean id="adapter" type="info.xonix.zlo.search.logic.forum_adapters.ForumAdapter" scope="request"/>

<c:if test="${not empty nick}">
    <c:set var="nickEscaped"><c:out value="${nick}"/></c:set>
    <%--<c:set var="nickUrlencodedForSite"><c:out value="${xonix:urlencode(nick, site.siteCharset)}"/></c:set>--%>
    <c:set var="nickUrlencoded"><c:out value="${xonix:urlencode(nick, null)}"/></c:set><%--default enc (UTF-8)--%>
    <span class="nick">
        <c:choose>
            <c:when test="${not reg}">${nickEscaped}</c:when>
            <c:otherwise>
                <%--<a href="http://${site.siteUrl}${site.uinfoQuery}${nickUrlencodedForSite}">${nickEscaped}</a>--%>
                <a href="<%= adapter.prepareUserProfileUrl(-1, (String) nick)%>">${nickEscaped}</a>
            </c:otherwise>
        </c:choose>
    </span>
    <a class="search" href="search?site=${forumIntId}&nick=${nickUrlencoded}">?</a>
    <c:if test="${not empty host}">
        <a href="search?site=${forumIntId}&host=${host}&nick=${nickUrlencoded}"
           class="search" title="поиск по нику и хосту">?nh</a>
    </c:if>
    <c:if test="${true}"><%--TODO: not site.noHost--%>
        <a class="search" href="nickhost.jsp?site=${forumIntId}&w=n&t=${nickUrlencoded}"
           title="хосты этого ника">h</a>
    </c:if>
</c:if>
