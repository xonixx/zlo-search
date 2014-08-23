angular.module('charts', ['common', 'ngResource', 'mgcrea.ngStrap'])
    .factory('Chart', function ($resource) {
        return $resource('/a/charts/:action', {}, {
            submitTask: {method: 'POST', isArray: false, params: {action: 'submitTask'}}
        });
    });

function ChartsCtrl($scope, Chart) {
    $scope.task = {forumId: "zlo", dbNicks: "111", start: new Date(), end: new Date(), type: 'ByHour'};
    $scope.submitTask = function (task) {
        console.info(111, task);
        Chart.submitTask(task);
    }
}
