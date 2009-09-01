<%@ page import="java.util.Enumeration" %>
<%--
  User: Vovan
  Date: 07.03.2008
  Time: 21:33:11
--%>
<%
    Enumeration _enum = request.getHeaderNames();
while(_enum.hasMoreElements()) {
	String name = (String)_enum.nextElement();
	Enumeration values = request.getHeaders(name);
	while (values.hasMoreElements()) {
		out.println(name + ": " + values.nextElement());
        out.println("<br/>");
    }
}
%>