<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" type="text/css" href="../main.css"/>

<c:set var="title">XML Forum Protocol</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.developer"/>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>

<style type="text/css">
    .form {
        background: #dfdfdf;
        width: 300px;
    }
</style>

<div class="content">
    <h3>${title}</h3>

    <form action="xmlfp.jsp" method="get" class="form">
        <input type="hidden" name="xmlfp" value="descriptor"/>
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        <input type="submit" value="Получить XMLFP дескриптор"/>
    </form>

    <form action="xmlfp.jsp" method="get" class="form">
        <input type="hidden" name="xmlfp" value="message"/>
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        Номер сообщения: <input type="text" value="${param['num']}" name="num"/><br/>

        <input type="submit" value="Получить XML"/>
    </form>

    <form action="xmlfp.jsp" method="get" class="form">
        <input type="hidden" name="xmlfp" value="messages"/>
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        Диапазон
        от:<input size="9" type="text" value="${param['from']}" name="from"/>
        до:<input size="9" type="text" value="${param['to']}" name="to"/><br/>

        <input type="submit" value="Получить XML"/>
    </form>

    <form action="xmlfp.jsp" method="get" class="form">
        <input type="hidden" name="xmlfp" value="lastMessageNumber"/>
        Сайт:
        <jsp:getProperty name="backendBean" property="siteSelector"/>
        <br/>
        <input type="submit" value="Получить номер последнего сообщения"/>
    </form>

    <h3>XML Schemas</h3>
    <ul>
        <li><a href="xsd/descriptor.xsd">descriptor.xsd</a></li>
        <li><a href="xsd/message.xsd">message.xsd</a></li>
        <li><a href="xsd/messages.xsd">messages.xsd</a></li>
        <li><a href="xsd/author.xsd">author.xsd</a></li>
        <li><a href="xsd/lastMessageNumber.xsd">lastMessageNumber.xsd</a></li>
    </ul>
</div>