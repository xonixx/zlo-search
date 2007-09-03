<%--
  User: gubarkov
  Date: 30.08.2007
  Time: 21:10:26
--%>
<%@ include file="import.jsp"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:out value="${requestScope['aaa']}" />

<c:out value="${'привет мир'}" />  <br />
<c:out value="${'Без темы' == 'Без темы'}" />

<c:set var="a" value="${'Без темы'}" />
<c:out value="${a}   ${a == 'Без темы'}   ${a != 'Без темы'}" /> 