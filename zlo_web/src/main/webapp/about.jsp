<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" type="text/css" href="css/main.css"/>

<title>О системе</title>

<tiles:insertDefinition name="header.developer"/>

<div class="content">
    <h3>О системе</h3>
    Поисковик использует следующие технологии:
    <ul>
        <li>Сервер <a href="http://tomcat.apache.org/">Apache Tomcat</a>, используются сервлеты, JSP, JSTL</li>
        <li><a href="http://lucene.apache.org/">Lucene</a> - библиотека индексирования</li>
        <li><a href="http://www.springsource.org/">Spring Framework</a></li>
        <li>Реализация веб-сервиса на <a href="http://cxf.apache.org/">Apache CXF</a></li>
        <li>База данных <a href="http://www.mysql.com/">MySQL</a> для хранения сообщений</li>
    </ul>
    <%@ include file="WEB-INF/jsp/contact.jsp" %>
    <%@ include file="/WEB-INF/jsp/version-footer.jsp" %>
</div>

<tiles:insertDefinition name="ga"/>
