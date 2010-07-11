<%--
  User: Vovan
  Date: 23.09.2007
  Time: 22:45:44
--%>

<%@ include file="../WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<display:table pagesize="2" name="res" id="row" requestURI="test2"
               decorator="info.xonix.zlo.web.test.servlets.TestLazy1Decorator">
    <%--    <display:column title="Row">
        <% TestLazy1.Val it = (TestLazy1.Val) row; %>
        <%= it.getField() %>    
    </display:column>--%>
    <display:column title="Row1"><%= ((TestLazy1.Val) row).getField() %>
    </display:column>
</display:table>
