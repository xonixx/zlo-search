<%@ include file="/WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>

<c:if test="${xonix:userAgentSmall(header['User-Agent'])=='Internet Explorer'}">
    <div style="font-weight:bold;background:gold;border:darkorange solid 1px;text-align:center;margin:5px 20%;padding:2px">
        Кстати,
        <a class="brwsr" href="http://www.google.com/chrome/index.html?hl=ru"><img src="pic/browsers/chrome-favicon.png"
                                                                                   alt="Chrome"/> Chrome</a>,
        <a class="brwsr" href="http://www.mozilla-europe.org/ru/firefox/"><img src="pic/browsers/ff-favicon.png"
                                                                               alt="Firefox"/> Firefox</a>,
        <a class="brwsr" href="http://ru.opera.com/"><img src="pic/browsers/opera-favicon.png" alt="Opera"/> Opera</a> -
        тоже отличные браузеры!
    </div>
</c:if>