<%@ page import="java.util.ArrayList, info.xonix.zlo.web.servlets.test.t.Obj" %>
<%--
  User: Vovan
  Date: 30.04.2008
  Time: 23:08:18
--%>
<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<%! 
    private ArrayList<Obj> getArray(){
        ArrayList<Obj> res = new ArrayList<Obj>();

        for (int i = 0; i < 10; i++) {
            Obj o = new Obj();
            o.setN(i);
            o.setS("a" + i);
            res.add(o);
        }
        return res;
    }
%>

<% request.setAttribute("lyah", getArray()); %>

<display:table name="${lyah}" id="row" decorator="info.xonix.zlo.web.servlets.test.t.Decor">
    <display:column property="n" title="N" />
    <display:column property="s" title="S" />
    <display:column property="t" title="Double" />
</display:table>
