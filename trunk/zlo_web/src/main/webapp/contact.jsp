<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/commonJsCss.jsp" %>

<c:set var="title">Обратная связь</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.contact"/>

<div class="content">
    <h3>${title}</h3>

    <%@ include file="WEB-INF/jsp/contact.jsp" %>
</div>

<tiles:insertDefinition name="ga"/>
