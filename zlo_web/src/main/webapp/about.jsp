<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<link rel="stylesheet" type="text/css" href="main.css"/>

<title>О системе</title>

<tiles:insertDefinition name="header.about"/>

<div class="content">
    <h3>О системе</h3>
    Поисковик использует следующие технологии:
    <ul>
        <li>Сервер Apache Tomcat, используются сервлеты, JSP, JSTL</li>
        <li>Lucene - библиотека индексирования</li>
        <li>База данных MySQL для хранения сообщений</li>
    </ul>

    Просьба пожелания по улучшению системы, а также сообщения о найденных багах направлять на электронный адрес
    <script type="text/javascript">document.write("<a href='mai" + "lto:xoni" + "xx@gmai" + "l.com'>");
    document.write("xoni" + "xx@gmai" + "l.com</a>");</script>
    , или на ICQ 250-123-253.
</div>

<tiles:insertDefinition name="ga"/>
