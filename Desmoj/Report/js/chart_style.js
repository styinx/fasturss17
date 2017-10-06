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
            colors: ['#7cb5ec', '#f7a35c', '#90ee7e', '#7798BF', '#aaeeee', '#ff0066', '#eeaaee',
                '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'
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
                    fontWeight: 'bold'
                }
            },
            tooltip: {
                borderWidth: 1,
                shadow: false,
                backgroundColor: 'rgba(50,50,50,0.8)',
                style : {
                    border: '1px solid white',
                    color: '#FFFFFF'
                }
            },
            legend: {
                itemStyle: {
                    fontWeight: 'bold',
                    fontSize: '13px'
                },
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top'
            },
            xAxis: {
                gridLineWidth: 1,
                labels: {
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            yAxis: {
                minorTickInterval: 'auto',
                title: {
                    style: {
                        textTransform: 'uppercase'
                    }
                },
                labels: {
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            plotOptions: {
                candlestick: {
                    lineColor: '#404048'
                }
            },

            background2: '#F0F0EA'

        };
        Highcharts.setOptions(Highcharts.theme);

    }(Highcharts));
}));