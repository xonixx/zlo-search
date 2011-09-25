function changedDatesSelector() {
    var datesSelector = document.getElementById("dates");
    document.getElementById("fd").disabled =
            document.getElementById("td").disabled = !datesSelector.checked;
    return true;
}

function initAutocomplete() {
    $('#text').autocomplete({
        source: '/ac',
        minLength: 2
    });
}

$(function () {
    changedDatesSelector();
    initAutocomplete();
    dbInit();
});