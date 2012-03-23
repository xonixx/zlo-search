<%@ attribute name="text" required="true" type="java.lang.String" %>
<%@ attribute name="admin" required="true" type="java.lang.Boolean" %>

<%@ include file="/WEB-INF/jsp/import_taglibs.jsp" %>
<c:choose>
    <c:when test="${not xonix:isObscene(text)}">${text}</c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${admin}">
                <span class="attention">${text}</span>
            </c:when>
            <c:otherwise/>
        </c:choose>
    </c:otherwise>
</c:choose>