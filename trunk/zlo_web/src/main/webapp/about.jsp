<%@ page import="info.xonix.zlo.search.VersionManager" %>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" type="text/css" href="main.css"/>

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

    Просьба пожелания по улучшению системы, а также сообщения о найденных багах направлять на электронный адрес
    (он же jabber)
    <script type="text/javascript">document.write("<a href='mai" + "lto:xoni" + "xx@gmai" + "l.com'>");
    document.write("xoni" + "xx@gmai" + "l.com</a>");</script><%--,
 или на ICQ 250-123-253--%>
    или <a href="http://zlo.rt.mipt.ru/?persmsgform=xonix">в приват</a>.
    <hr/>
    <small>Версия: <i><%= VersionManager.getApplicationVersion() %></i></small>
</div>

<tiles:insertDefinition name="ga"/>
