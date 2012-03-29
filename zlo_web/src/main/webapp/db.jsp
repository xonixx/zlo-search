<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/restrictAccess.jsp" %>

<c:set var="sql">${param['sql']}</c:set>

<form action="db.jsp" method="post">
    <textarea rows="5" cols="50" name="sql">${sql}</textarea>
    <input type="submit" value="GO">
</form>

<c:if test="${not empty sql}">
    <sql:query var="res">${sql}</sql:query>
    <display:table name="${res.rows}"/>
</c:if>

