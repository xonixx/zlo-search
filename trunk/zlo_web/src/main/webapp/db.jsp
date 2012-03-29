<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/restrictAccess.jsp" %>

<link rel="stylesheet" type="text/css" href="main.css"/>
<link rel="stylesheet" type="text/css" href="db.css"/>

<tiles:insertDefinition name="header.admin"/>

<c:set var="sql">${param['sql']}</c:set>

<div class="content" style="text-align: center">
    <form action="db.jsp" method="post">
        <textarea rows="5" cols="70" name="sql">${sql}</textarea>
        <br>
        <input type="submit" value="GO">
    </form>

    <c:if test="${not empty sql}">
        <c:set var="sqlLc">${f:trim(f:toLowerCase(sql))}</c:set>
        <c:choose>
            <c:when test="${
                f:startsWith(sqlLc, 'insert') or
                f:startsWith(sqlLc, 'update') or
                f:startsWith(sqlLc, 'delete')
            }">
                <sql:update var="cnt">${sql}</sql:update>
                Updated: ${cnt} rows
            </c:when>
            <c:otherwise>
                <sql:query var="res">${sql}</sql:query>
                <display:table name="${res.rows}">
                    <display:caption>Total: ${res.rowCount} results</display:caption>
                </display:table>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>
