angular.module('charts', ['common', 'ngResource', 'mgcrea.ngStrap', 'highcharts-ng'])
    .factory('Chart', function ($resource) {
        return $resource('/a/charts/:action/:id', {}, {
            submitTask: {method: 'POST', isArray: false, params: {action: 'submitTask'}},
            checkTask: {method: 'GET', isArray: false, params: {action: 'checkTask'}}
        });
    });

function ChartsCtrl($scope, Chart, $timeout, dateFilter, $location) {
    var params = $location.search();
    if (params.id) {
        checkTask(params.id);
    }
//    $scope.task = {forumId: "zlo", dbNicks: "xonix", start: new Date(1388612189000), end: new Date(), type: 'ByHour'};
    $scope.submitTask = function (task) {
        delete task.nicks;
        delete task.result;
        delete task.error;

        Chart.submitTask(task, function (res) {
            $scope.chartConfig = null;
            $scope.checking = true;

            $timeout(function () {
                $location.search('id', res.id);
                checkTask(res.id)
            }, 1000)
        });
    };

    function checkTask(id) {
        $scope.checking = true;
        Chart.checkTask({id: id}, function (res) {
            if (res.result || res.error) {
                $scope.checking = false;
                $scope.task = res;
                if (res.result) {
                    res.result = angular.fromJson(res.result);
                    prepareChart(res)
                }
            } else {
                $timeout(function () {
                    checkTask(id)
                }, 2000)
            }
        })
    }

    var df1 = function (inp) {
        return dateFilter(inp, 'dd.MM.yyyy')
    };

    function prepareChart(chartTask) {
        var type = chartTask.type;
        var title = 'Активность ' + (
            type == 'ByHour' ? 'по часам дня' :
                type == 'ByWeekDay' ? 'по дням недели' :
                    type == 'DayInterval' ? 'по дням' : '') + ' пользователя ' + chartTask.dbNicks +
            ' в интевале дат от ' + df1(chartTask.start) + ' до ' + df1(chartTask.end);

        var categories = [];
        var data = [];

        angular.forEach(chartTask.result, function (v, k) {
            data.push(v);
            categories.push(k);
        });

        $scope.chartConfig = {
            options: {
                chart: {
                    type: type == 'ByWeekDay' ? 'column' : 'line'
                }
            },
            title: {
                text: title
            },
            xAxis: {
//                type: 'datetime',
                categories: categories
            },
            yAxis: {
                min: 0,
                title: { text: 'Число сообщений' },
                plotLines: [
                    {
                        value: 0,
                        width: 1,
                        color: '#808080'
                    }
                ]
            },
            series: [
                {
                    name: chartTask.dbNicks,
                    data: data
                }
            ]
        };

//        console.info($scope.chartConfig)
    }
}
