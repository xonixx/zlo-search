$(function () {
    var $statsFrm = $('#stats-form');
    $statsFrm.find('input,select').change(function () {
        $statsFrm.submit();
    })
});