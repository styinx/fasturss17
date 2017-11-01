'use strict';
(function(factory) {
    if (typeof module === 'object' && module.exports) {
        module.exports = factory;
    } else {
        factory(Highcharts);
    }
}(function(Highcharts) {
    (function(Highcharts) {

        Highcharts.theme = {
            colors: ['#0077CC', '#DDDD00', '#CC00CC', '#009900', '#AA0000', '#00CCCC', '#000000',
                '#FF0000', '#00FF00', '#0000FF', '#FFFFFF', '#0099FF'
            ],
            chart: {
                backgroundColor: null,
                style: {
                    fontFamily: 'Verdana, sans-serif'
                }
            },
            title: {
                style: {
                    fontSize: '16px',
                    fontWeight: 'bold',
                    color: '#000000',
                    textDecoration: 'underline'
                }
            },
            tooltip: {
                formatter: function () {
                    var s = '<b>' + this.x + '</b>';
                    for(var p in this.points) {
                        s += '<br/><span style="color:' + this.points[p].color + '">\u25CF</span> ' + this.points[p].series.name + ': ' + this.points[p].y;
                    }
                    return s;
                },
                shared: true,
                borderWidth: 1,
                shadow: false,
                backgroundColor: 'rgba(50,50,50,0.8)',
                style : {
                    border: '1px solid white',
                    color: '#FFFFFF'
                }
            },
            rangeSelector: {
                selected: 4,
                inputEnabled: false,
                buttonTheme: {
                    visibility: 'hidden'
                },
                labelStyle: {
                    visibility: 'hidden'
                }
            },
            legend: {
                itemStyle: {
                    fontWeight: 'bold',
                    fontSize: '13px',
                    color: 'rgba(0,0,0,0.8)'
                },
                itemHoverStyle: {
                    color: 'rgba(0,150,0,0.8)'
                },
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top'
            },
            xAxis: {
                gridLineWidth: 1,
                /*alternateGridColor: 'rgba(50,50,50,0.8)',*/
                minorGridLineColor: 'rgba(50,50,50,0.2)',
                gridLineColor: 'rgba(50,50,50,0.8)',
                labels: {
                    style: {
                        fontSize: '12px',
                        color: 'rgba(50,50,50,0.8)'
                    },
                    formatter: function () {
                        return this.value;
                    }
                }
            },
            yAxis: {
                opposite: false,
                minorTickInterval: 'auto',
                gridLineWidth: 1,
                /*alternateGridColor: 'rgba(50,50,50,0.8)',*/
                minorGridLineColor: 'rgba(50,50,50,0.2)',
                gridLineColor: 'rgba(50,50,50,0.8)',
                title: {
                    style: {
                        textTransform: 'uppercase'
                    }
                },
                labels: {
                    style: {
                        fontSize: '12px',
                        color: 'rgba(50,50,50,0.8)'
                    }
                }
            },
            plotOptions: {
                candlestick: {
                    lineColor: 'rgba(50,50,50,0.8)'
                }
            },
            credits: {
                enabled: false
            }
        };
        Highcharts.setOptions(Highcharts.theme);

    }(Highcharts));
}));
