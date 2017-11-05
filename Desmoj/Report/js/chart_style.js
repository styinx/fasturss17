'use strict';
function pad(s, size)
{
    while (s.length < size) s = "0" + s;
    return s;
}

function colors(count)
{
    var fallback = ['#0077CC', '#DDDD00', '#CC00CC', '#009900', '#AA0000', '#00CCCC',
                    '#000000', '#FF0000', '#00FF00', '#0000FF', '#FFFFFF', '#0099FF'];
    var cols = [];
    var from = 0xFF0000;
    var to = 0x00FF00;
    if(from > to)
    {
        to = [from, from = to][0];
    }
    var step = Math.round((to - from)/count);

    for(var x = from; x < to; x += step)
    {
        var color = pad(x.toString(16), 6);
        cols.push("#" + color);
    }
    return cols;
}

function timeFormat(seconds)
{
    var date = new Date(1970, 0, 1);
    date.setSeconds(seconds);

    var h = pad(date.getHours());
    var m = pad(date.getMinutes());
    var s = pad(date.getSeconds());

    var format = ((h != "00") ? h + "h" : "") + m + "m" + s + "s";

    return format;
}

(function(factory) {
    if (typeof module === 'object' && module.exports) {
        module.exports = factory;
    } else {
        factory(Highcharts);
    }
}(function(Highcharts) {
    (function(Highcharts) {

        Highcharts.theme = {
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
                    var s = 'Time: <b>' + timeFormat(this.x) + '</b>';
                    for(var p in this.points) {
                        s += '<br/><span style="color:' + this.points[p].color + '">\u25CF</span> ' + this.points[p].series.name + ': ' + Math.floor(this.points[p].y * 100)/100;
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
                        return timeFormat(this.value);
                    },
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
