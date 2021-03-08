$(function () {
    Highcharts.setOptions({
        colors: ['rgb(33, 150, 243)'],
        chart: {
            style: {
                fontFamily: 'sans-serif',
                color: '#fff'
            }
        }
    });
    $('#chartContainer').highcharts({
        chart: {
            type: 'column',
            backgroundColor: 'rgb(52, 58, 64)'
        },
        title: {
            text: 'Bill Registry',
            style: {
                color: '#fff'
            }
        },
        xAxis: {
            tickWidth: 0,
            labels: {
                style: {
                    color: '#fff',
                }
            },
            categories: ['Mon', 'Tues', 'Wed', 'Thurs', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            gridLineWidth: .5,
            gridLineDashStyle: 'dash',
            gridLineColor: 'black',
            title: {
                text: '',
                style: {
                    color: '#fff'
                }
            },
            labels: {
                formatter: function() {
                    return '$'+Highcharts.numberFormat(this.value, 0, '', ',');
                },
                style: {
                    color: '#fff',
                }
            }
        },
        legend: {
            enabled: false,
        },
        credits: {
            enabled: false
        },
        tooltip: {
            valuePrefix: '$'
        },
        plotOptions: {
            column: {
                borderRadius: 2,
                pointPadding: 0,
                groupPadding: 0.1
            }
        },
        series: [{
            name: 'Revenue',
            data: [2200, 2800, 2300, 1700, 2000, 1200, 1400]
        }]
    });
});


