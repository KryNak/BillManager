function nextChartNo(){
    const pathname = window.location.pathname;
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("chartNo", +urlParams.get("chartNo") + 1);
    window.location.href = `${pathname}?${urlParams.toString()}`
}

function prevChartNo(){
    const pathname = window.location.pathname;
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("chartNo", +urlParams.get("chartNo") - 1);
    window.location.href = `${pathname}?${urlParams.toString()}`;
}

function proceedOnChange(){
    const periodDict = {"1":"d", "2":"w", "3":"M", "4":"y"};
    const el = document.getElementById("period-select");
    let timePeriod;

    switch(el.value){
        case '1':
            timePeriod = periodDict['1'];
            break;
        case '2':
            timePeriod = periodDict['2'];
            break;
        case '3':
            timePeriod = periodDict['3'];
            break;
        case '4':
            timePeriod = periodDict['4'];
            break;
        default:
            throw new Error("Illegal Argument");
    }

    let searchParams = new URLSearchParams(window.location.search);
    searchParams.set('timePeriod', timePeriod);

    window.location.href = `${location.pathname}?${searchParams.toString()}`;
}

$('document').ready(function(){
    const periodDict = {"d":"1", "w":"2", "M":"3", "y":"4"};
    $('#period-select').val(periodDict[new URLSearchParams(location.search).get('timePeriod')]);
});
