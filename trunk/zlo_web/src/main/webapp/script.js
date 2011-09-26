function changedDatesSelector() {
    var datesSelector = document.getElementById("dates");
    document.getElementById("fd").disabled =
        document.getElementById("td").disabled = !datesSelector.checked;
    return true;
}

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
    changedDatesSelector();
    initAutocomplete();
    dbInit();
});