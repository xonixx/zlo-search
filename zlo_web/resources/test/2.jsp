<%--
  User: Vovan
  Date: 25.04.2008
  Time: 4:29:49
--%>

<%@ include file="/WEB-INF/include/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<c:out value="${xx:urlencode('<sb>')}" />

<c:set var="a">"&qqq></c:set>

${a}<br/>
<c:set var="a"><c:out value="${a}" /></c:set>
${a}<br/>