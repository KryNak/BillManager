let pDate;

$('document').ready(function () {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const date = urlParams.get('date');

    $('#datepicker-id').val(date);
    $.get(location.pathname + '/sum?date=' + date, function(sum){
        if (sum === '') $('#counter').text(0);
        else $('#counter').text(sum.toFixed(2));
    });

    pDate = date;

    $('.act').parent().attr('class', 'active blue');
});

function getData(id) {
    let href = location.pathname + '/getOne/' + id;

    $.get(href, function (bill) {
        $('#put-title').val(bill.title);
        $('#put-price').val(bill.price);
        $('#put-id').val(bill.id);
    });
}

$(document).ready(function () {
    $('.modal').modal();
});

$(".button-collapse").sideNav();

$('.dropdown-button').dropdown({
        constrainWidth: false, // Does not change width of dropdown to that of the activator
        alignment: 'right', // Displays dropdown with edge aligned to the left of button
    }
);

$('.datepicker').pickadate({
    selectMonths: true, // Creates a dropdown to control month
    selectYears: 15, // Creates a dropdown of 15 years to control year,
    today: 'Today',
    clear: 'Clear',
    close: 'Ok',
    container: undefined, // ex. 'body' will append picker to body
    closeOnSelect: false,
    format: 'yyyy-mm-dd',
    onClose: function () {
        const date = $('#datepicker-id').val();
        const urlParams = new URLSearchParams(window.location.search);
        if(date !== "" && pDate !== date){
            location.href = `/api/transactions?date=${date}&pageNo=1&chartNo=${urlParams.get('chartNo')}`;
        }
    },
});

$('.datepicker').on('mousedown', function (event) {
    event.preventDefault();
});


function formatDate(date) {
    let year = date.getFullYear();
    let month = date.getMonth() + 1 >= 10 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1);
    let day = date.getDate() >= 10 ? date.getDate() : '0' + date.getDate();

    return `${year}-${month}-${day}`
}

function nextChartNo(){
    const pathname = window.location.pathname;
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("chartNo", +urlParams.get("chartNo") + 1);
    window.location.href = `${pathname}?${urlParams.toString()}#c2`
}

function prevChartNo(){
    const pathname = window.location.pathname;
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("chartNo", +urlParams.get("chartNo") - 1);
    window.location.href = `${pathname}?${urlParams.toString()}#c2`;
}

