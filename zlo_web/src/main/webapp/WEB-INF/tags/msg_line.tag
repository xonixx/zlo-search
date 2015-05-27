<%@ include file="/WEB-INF/jsp/import_taglibs.jsp" %>

<%@ attribute name="msg" required="true" type="info.xonix.zlo.search.model.Message" %>
<%@ attribute name="nolink" required="false" type="java.lang.Boolean" %>

<c:if test="${nolink}">
    <b>${msg.title}</b>
</c:if>
<c:if test="${!nolink}">
    <a href="msg?site=${forumIntId}&num=${msg.num}">${msg.title}</a>
</c:if>
--
<util:nick_and_host msg="${msg}"/> --
<fmt:formatDate value="${msg.date}" pattern="d/MM/yyyy HH:mm:ss"/>
