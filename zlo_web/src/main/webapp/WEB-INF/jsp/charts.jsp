<%@ page import="info.xonix.zlo.search.charts.ChartType" %>
<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ include file="import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<head>
    <title>Графики</title>

    <link rel="stylesheet" type="text/css" href="/lib/bootstrap/css/bootstrap${minSuffix}.css"/>
    <link rel="stylesheet" type="text/css" href="/lib/bootstrap/css/bootstrap-theme${minSuffix}.css"/>

    <%@ include file="/WEB-INF/jsp/commonJsCss.jsp" %>

    <%--jQuery--%>
    <link rel="stylesheet" type="text/css" href="/lib/jq/jquery-ui-1.11.0/jquery-ui${minSuffix}.css"/>

    <script type="text/javascript" src="/lib/jq/jquery-ui-1.11.0/jquery-ui${minSuffix}.js"></script>
    <%--jQuery ends--%>

    <script type="text/javascript" src="/lib/angular/angular${minSuffix}.js"></script>
    <script type="text/javascript" src="/lib/angular/angular-resource${minSuffix}.js"></script>
    <script type="text/javascript" src="/lib/angular-strap/angular-strap${minSuffix}.js"></script>
    <script type="text/javascript" src="/lib/angular-strap/angular-strap.tpl${minSuffix}.js"></script>

    <script type="text/javascript" src="/js/common.js?${version}"></script>
    <script type="text/javascript" src="/js/charts.js?${version}"></script>
</head>

<body ng-app="charts" ng-controller="ChartsCtrl">
<tiles:insertDefinition name="header.charts"/>

<form name="chartsForm" novalidate="" class="text-center" custom-submit="submitTask(task)">
    <select ng-model="task.forumId" required="">
        <c:forEach var="fd" items="<%= GetForum.descriptors() %>">
            <option value="${fd.forumId}">${fd.forumAdapter.forumTitle}</option>
        </c:forEach>
    </select>

    <input type="text" placeholder="Ник" name="nick" ng-model="task.dbNicks" required="">

    <input type="text" placeholder="Дата (от)" name="start" id="start"
           ng-model="task.start" required="" bs-datepicker="" data-date-format="dd.MM.yyyy"/>
    <input type="text" placeholder="Дата (до)" name="end" id="end"
           ng-model="task.end" required="" bs-datepicker="" data-date-format="dd.MM.yyyy"/>

    <select ng-model="task.type" required="">
        <c:forEach var="t" items="<%= ChartType.values() %>">
            <option value="${t.name()}">${t.title}</option>
        </c:forEach>
    </select>

    <br/>
    <button>Построить график!</button>
</form>

</body>
