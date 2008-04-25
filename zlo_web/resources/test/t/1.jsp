<%--
  User: Vovan
  Date: 25.04.2008
  Time: 17:18:42
--%>
<%@ include file="/WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<tiles:insertTemplate template="tmp.jsp">
    <tiles:putAttribute name="a" value="AAA" />
</tiles:insertTemplate>

<tiles:insertDefinition name="test" />