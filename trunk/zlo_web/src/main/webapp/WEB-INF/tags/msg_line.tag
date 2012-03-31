<%@ include file="/WEB-INF/jsp/import_taglibs.jsp" %>

<%@ attribute name="msg" required="true" type="info.xonix.zlo.search.model.Message" %>

<a href="msg?site=${forumIntId}&num=${msg.num}">${msg.title}</a> --
<util:nick_and_host msg="${msg}"/> --
<fmt:formatDate value="${msg.date}" pattern="d/MM/yyyy HH:mm:ss"/>
