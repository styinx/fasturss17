document.getElementById('chart-container').innerHTML += "<div id='Idle'></div>";
document.getElementById('chart-container').innerHTML += "<div id='Throughput'></div>";
document.getElementById('chart-container').innerHTML += "<div id='Performance'></div>";
Highcharts.chart('Idle', {title : { text : 'Idle'},credits : {enabled: false},series : [{name : 'Idle',data : [0, 1, 2, 3,]}]});
Highcharts.chart('Throughput', {title : { text : 'Throughput'},credits : {enabled: false},series : [{name : 'Task',data : [1, 2, 4, 8,]}]});
Highcharts.chart('Performance', {title : { text : 'Performance'},credits : {enabled: false},series : [{name : 'CPU 1',data : [1, 3, 9, 27,]},{name : 'CPU 2',data : [8, 16, 24, 20,]}]});
