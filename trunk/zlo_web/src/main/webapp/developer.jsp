<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" type="text/css" href="main.css"/>

<c:set var="title">Разработчику</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.developer"/>

<div class="content">
    <h3>${title}</h3>

    <ul>
        <li><strong><a href="about.jsp">О системе</a></strong> - информация о системе</li>
        <li><strong><a href="ws.jsp">Веб-сервис</a></strong> - веб-сервис для бордопоиска</li>
        <li><strong><a href="xmlfp/xmlfp.jsp">XMLFP</a></strong> - XML Forum Protocol</li>
    </ul>

    <%@ include file="/WEB-INF/jsp/version-footer.jsp" %>
</div>

<tiles:insertDefinition name="ga"/>