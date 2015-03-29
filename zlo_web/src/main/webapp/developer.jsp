<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/commonJsCss.jsp" %>

<c:set var="title">Разработчику</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.developer"/>

<div class="content">
    <h3>${title}</h3>

    <ul>
        <li><strong><a href="about.jsp">О системе</a></strong> - информация о системе</li>
        <li><strong><a href="ws.jsp">Веб-сервис Бордопоиска</a></strong></li>
        <li><strong><a href="xmlfp/xmlfp.jsp">XMLFP</a></strong> - XML Forum Protocol</li>
        <li><strong><a href="https://github.com/xonixx/zlo-search">Проект Бордопоиска на GitHub</a></strong></li>
    </ul>

    <%@ include file="/WEB-INF/jsp/version-footer.jsp" %>
</div>

<tiles:insertDefinition name="ga"/>