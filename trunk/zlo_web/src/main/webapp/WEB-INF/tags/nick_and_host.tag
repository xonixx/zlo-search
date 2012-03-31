<%@ include file="/WEB-INF/jsp/import_taglibs.jsp" %>

<%@ attribute name="msg" required="true" type="info.xonix.zlo.search.model.Message" %>

<tiles:insertDefinition name="nick">
    <tiles:putAttribute name="reg" value="${msg.reg}"/>
    <tiles:putAttribute name="nick" value="${msg.nick}"/>
    <tiles:putAttribute name="userId" value="${msg.userId}"/>
</tiles:insertDefinition>
<tiles:insertDefinition name="host">
    <tiles:putAttribute name="host" value="${msg.host}"/>
    <tiles:putAttribute name="brackets" value="${true}"/>
</tiles:insertDefinition>