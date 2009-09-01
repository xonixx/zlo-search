<%@ page contentType="text/html; charset=windows-1251" %>

<h3>XML Forum Protocol</h3>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request" />

<style type="text/css">
    .form { background: #eee; width: 300px; }
</style>

<form action="xmlfp.jsp" method="get" class="form">
    Сайт: <jsp:getProperty name="backendBean" property="siteSelector" /><br />
    Номер сообщения: <input type="text" value="${param['num']}" name="num" /><br />

    <input type="submit" value="Получить XML" />
</form>

<form action="xmlfp.jsp" method="get" class="form">
    <input type="hidden" name="lastMessageNumber" value="true" />
    Сайт: <jsp:getProperty name="backendBean" property="siteSelector" /><br />
    <input type="submit" value="Получить номер последнего сообщения" />
</form>

<h3>XML Schemas</h3>
<ul>
    <li><a href="xsd/message.xsd">message.xsd</a></li>
    <li><a href="xsd/author.xsd">author.xsd</a></li>
    <li><a href="xsd/lastMessageNumber.xsd">lastMessageNumber.xsd</a></li>
</ul>