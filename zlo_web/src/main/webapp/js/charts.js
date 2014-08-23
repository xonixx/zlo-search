angular.module('charts', ['common', 'ngResource', 'mgcrea.ngStrap'])
    .factory('Chart', function ($resource) {
        return $resource('/a/charts/:action/:id', {}, {
            submitTask: {method: 'POST', isArray: false, params: {action: 'submitTask'}},
            checkTask: {method: 'GET', isArray: false, params: {action: 'checkTask'}}
        });
    });

function ChartsCtrl($scope, Chart, $timeout) {
    $scope.task = {forumId: "zlo", dbNicks: "xonix", start: new Date(1388612189000), end: new Date(), type: 'ByHour'};
    $scope.submitTask = function (task) {
        Chart.submitTask(task, function (res) {
            $timeout(function () { checkTask(res.id) }, 1000)
        });
    };

    function checkTask(id) {
        Chart.checkTask({id:id}, function (res) {
            if (res.result || res.error) {
                $scope.task = res;
                if (res.result)
                    res.result = angular.fromJson(res.result)
            } else {
                $timeout(function () { checkTask(id) }, 2000)
            }
        })
    }
}
