function initAutocomplete() {
    $('#text').autocomplete({
        source: function (search, cb) {
            $.ajax('/ac', {
                dataType: 'json',
                data: {
                    site: $('#site').val(),
                    term: search.term
                },
                success: function (res) {
                    cb(res);
                },
                error: function () {
                    cb([]);
                }
            });
        },
        minLength: 2
    });
}

$(function () {
    document.getElementsByName("text")[0].focus();
    document.getElementsByName("site")[0].onchange = function () {
        document.getElementsByName("topic")[0].selectedIndex = 0;
        document.getElementById("searchFrm").submit();
    };

    initAutocomplete();
    dbInit();
});

angular.module('search', ['mgcrea.ngStrap']);

function toDate(str) { // dd.MM.yyyy
    if (str && /\d\d\.\d\d\.\d\d\d\d/.test(str)) {
        var parts = str.split('.');
        return new Date(parts[2] + '-' + parts[1] + '-' + parts[0])
    }
    return null;
}

function SearchCtrl($scope) {
    $scope.fromDate = toDate(window.params.fd);
    $scope.toDate = toDate(window.params.td);
}