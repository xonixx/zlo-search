<%--
  User: Vovan
  Date: 25.04.2008
  Time: 4:29:49
--%>

<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:out value="${xonix:urlencode('<sb>')}"/>

<c:set var="a">"&qqq></c:set>

${a}<br/>
<c:set var="a"><c:out value="${a}"/></c:set>
${a}<br/>