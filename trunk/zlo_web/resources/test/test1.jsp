<%--
  User: gubarkov
  Date: 30.08.2007
  Time: 21:10:26
--%>
<%--<%@ include file="../WEB-INF/jsp/import.jsp"%>
<%@ page contentType="text/html; charset=windows-1251" %>

<c:out value="${requestScope['aaa']}" />

<c:out value="${'привет мир'}" />  <br />
<c:out value="${'Без темы' == 'Без темы'}" />

<c:set var="a" value="${'Без темы'}" />
<c:out value="${a}   ${a == 'Без темы'}   ${a != 'Без темы'}" />

<%!
enum Enum {
    ONE, TWO
}
%>

<c:set var="a" value="<%= Enum.ONE %>"/>
<c:if test="<%= pageContext.getAttribute("a") == Enum.ONE %>">
    ONE
</c:if>

<c:set var="b" value="<%= Enum.TWO %>" />

<c:if test="${not (a == b)}">
    NOT EQUAL    
</c:if>

<fmt:message key="header.title" />

<% try{ %>


<%
 throw new RuntimeException("asda");
%>

<% }catch(Exception e){ response.getOutputStream().print("Error"); response.flushBuffer(); }%>--%>
