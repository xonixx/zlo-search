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
    <display:column title="N">
        <a href="http://ya.ru/?q=${row.n}">${row.n}</a>
    </display:column>
    <display:column property="s" title="S" />
    <display:column property="t" title="Double" />
    <display:column title="Array">
        <display:table name="${row.strArr}" />
        <c:if test="${not empty row.strArr}">
            <c:out value="${row.s}"/>
        </c:if>
        <display:table name="${row.strArr}" id="str">
            <display:setProperty name="basic.msg.empty_list"><span class="pagebanner">Сообщения, соответствующие введенным критериям поиска не найдены. </span></display:setProperty>
            <display:setProperty name="paging.banner.one_item_found"><span class="pagebanner">Найдено одно сообщение. </span></display:setProperty>
            <display:setProperty name="paging.banner.all_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны все. </span></display:setProperty>
            <display:setProperty name="paging.banner.some_items_found"><span class="pagebanner">Найдено сообщений: {0}, показаны с {2} по {3}. </span></display:setProperty>
            <display:column title="${row.s}">
                ${str}
            </display:column>
        </display:table>
    </display:column>
</display:table>
