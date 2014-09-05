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

    <script type="text/javascript" src="/lib/highcharts/highcharts${minSuffix}.js"></script>
    <script type="text/javascript" src="/lib/highcharts-ng/highcharts-ng${minSuffix}.js"></script>

    <script type="text/javascript" src="/js/common.js?${version}"></script>
    <script type="text/javascript" src="/js/charts.js?${version}"></script>
    <style>
        td label {
            display: block;
            font-size: .8em;
            font-weight: bold;
        }
    </style>
</head>

<body ng-app="charts" ng-controller="ChartsCtrl">
<tiles:insertDefinition name="header.charts"/>

<form name="chartsForm" novalidate="" class="text-center" custom-submit="submitTask(task)">
    <table style="margin: 10px auto 0">
        <tr>
            <td>
                <label for="forumId">Форум:</label>
                <select id="forumId" ng-model="task.forumId" required="">
                    <c:forEach var="fd" items="<%= GetForum.descriptors() %>">
                        <option value="${fd.forumId}">${fd.forumAdapter.forumTitle}</option>
                    </c:forEach>
                </select></td>
            <td>
                <label for="nick">Ник:</label>
                <input type="text" id="nick" name="nick" ng-model="task.dbNicks" required="">
            </td>
            <td>
                <label for="start">Дата (от):</label>
                <input type="text" name="start" id="start"
                       ng-model="task.start" required="" bs-datepicker="" data-date-format="dd.MM.yyyy"/></td>
            <td>
                <label for="end">Дата (до):</label>
                <input type="text" name="end" id="end"
                       ng-model="task.end" required="" bs-datepicker="" data-date-format="dd.MM.yyyy"/></td>
            <td>
                <label for="taskType">График:</label>
                <select id="taskType" ng-model="task.type" required="">
                    <c:forEach var="t" items="<%= ChartType.values() %>">
                        <option value="${t.name()}">${t.title}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
    </table>

    <button class="submitBtn1">Построить график!</button>

    <br/>
    <%--{{ task }}--%>

    <div ng-if="task.error" class="error">{{ task.error }}</div>

    <div ng-if="chartConfig">
        <highchart id="chart" config="chartConfig"></highchart>
    </div>

    <div ng-show="checking">
        Пожалуйста подождите, пока строится график.
    </div>
</form>

</body>
