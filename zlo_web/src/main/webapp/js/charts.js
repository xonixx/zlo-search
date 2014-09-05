function leave_only_fields(obj, fields) {
    var r = {};
    for (var k in obj) {
        if (k.substr(0,1) == '$' || $.inArray(k, fields) != -1 || k == 'id' || k == 'version') {
            r[k] = obj[k];
        }
    }
    return r;
}

angular.module('charts', ['common', 'ngResource', 'mgcrea.ngStrap', 'highcharts-ng'])
    .factory('Chart', function ($resource) {
        return $resource('/a/charts/:action/:id', {}, {
            submitTask: {method: 'POST', isArray: false, params: {action: 'submitTask'}},
            checkTask: {method: 'POST', isArray: false, params: {action: 'checkTask'}}
        });
    });

function ChartsCtrl($scope, Chart, $timeout, dateFilter, $location) {
    var params = $location.search();
    if (params.params) {
        var task;

        try {
            task = angular.fromJson(params.params);
        } catch (e) {
            task = null;
        }
        if (task) {
            if (task.start) task.start = new Date(task.start);
            if (task.end) task.end = new Date(task.end);
            $scope.task = task;
//            console.info(222,task)
            checkTask(task);
        }
    }
    $scope.task = {forumId: "zlo", dbNicks: "xonix", start: new Date(1388612189000), end: new Date(1409953114753), type: 'ByHour'};
    $scope.submitTask = function (task) {
        task = leave_only_fields(task, ['forumId', 'dbNicks', 'start', 'end', 'type']);

        Chart.submitTask(task, function (res) {
            $scope.chartConfig = null;
            $scope.checking = true;

            $timeout(function () {
                $location.search('params', res.descriptor);
                checkTask(task);
            }, 1000)
        });
    };

    function checkTask(task) {
        task = leave_only_fields(task, ['forumId', 'dbNicks', 'start', 'end', 'type']);
//        console.info(111,task)

        $scope.checking = true;
        Chart.checkTask(task, function (res) {
            if (res.result || res.error) {
                $scope.checking = false;
                $scope.task = res;
                if (res.result) {
                    res.result = angular.fromJson(res.result);
                    prepareChart(res)
                }
            } else {
                $timeout(function () {
                    checkTask(task)
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

        var chartCommon = {
            title: {
                text: title
            },
            size: {
                height: 500
            }
        };
        if (type == 'ByHour' || type == 'ByWeekDay') {
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
                xAxis: {
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
        } else { // interval
            $scope.chartConfig = {
                options: {
                    chart: {
                        zoomType: 'x'
                    },
                    plotOptions: {
                        area: {
                            fillColor: {
                                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                                stops: [
                                    [0, Highcharts.getOptions().colors[0]],
                                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                ]
                            },
                            marker: {
                                radius: 2
                            },
                            lineWidth: 1,
                            states: {
                                hover: {
                                    lineWidth: 1
                                }
                            },
                            threshold: null
                        }
                    }
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                        'Click and drag in the plot area to zoom in' :
                        'Pinch the chart to zoom in'
                },
                xAxis: {
                    type: 'datetime',
                    minRange: 10 * 24 * 3600000 // 10 days
                },
                yAxis: {
                    min: 0,
                    title: { text: 'Число сообщений' }
                },
                legend: {
                    enabled: false
                },
                series: [
                    {
                        type: 'area',
                        name: chartTask.dbNicks,
                        pointInterval: 24 * 3600 * 1000
                    }
                ]
            };

            angular.extend($scope.chartConfig.series[0], {
                pointStart: _toDate(chartTask.result.start),
                data: chartTask.result.data
            });
        }

        angular.extend($scope.chartConfig, chartCommon);
        console.info($scope.chartConfig)
    }

    function _toDate(str) { // yyyy-MM-dd
        if (str && /\d\d\d\d-\d\d-\d\d/.test(str)) {
            var parts = str.split('-');
            return Date.UTC(parts[0], parts[1] - 1, parts[2])
        }
        return null;
    }
}
