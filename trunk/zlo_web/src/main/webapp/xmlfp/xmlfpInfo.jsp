<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" type="text/css" href="../css/main.css"/>

<c:set var="version">v. 0.2</c:set>
<c:set var="title">XML Forum Protocol ${version}</c:set>
<title>${title}</title>

<tiles:insertDefinition name="header.developer"/>

<jsp:useBean id="backendBean" class="info.xonix.zlo.web.BackendBean" scope="request"/>

<style type="text/css">
    .form {
        background: #dfdfdf;
        width: 330px;
        padding: 4px;
        /*text-align: center;*/
    }
</style>

<div class="content">
    <h3>${title}</h3>

    <p>
        Форум, реализующий XMLFP, может быть легко добавлен в бордопоиск.
        <br/>
        Для реализации XMLFP форум должен предоставлять XMLFP Descriptor URL, наподобие
        <a href="xmlfp.jsp?xmlfp=descriptor&site=0">такого</a>.
        <br/>
        А также (в версии ${version}) уметь обрабатывать XMLFP-запросы на получение:
    </p>
    <ul>
        <li>XML-представления последнего номера сообщения в базе форума</li>
        <li>XML-представления конкретного сообщения</li>
        <li>XML-представления списка сообщений в диапазоне</li>
    </ul>
    <p>
        Для демонстрации примера реализации воспользуйтесь формами ниже.
    </p>
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

    <br/>
    <h3>XML Schemas</h3>
    <p>
        Генерируемые XML должны удовлетворять схемам:
    </p>
    <ul>
        <li><a href="xsd/descriptor.xsd">descriptor.xsd</a></li>
        <li><a href="xsd/message.xsd">message.xsd</a></li>
        <li><a href="xsd/messages.xsd">messages.xsd</a></li>
        <li><a href="xsd/author.xsd">author.xsd</a></li>
        <li><a href="xsd/lastMessageNumber.xsd">lastMessageNumber.xsd</a></li>
    </ul>
</div>