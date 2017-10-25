function showLines(id)
{
    var chart = $("#" + id).highcharts();
    for(var series in chart.series)
    {
        chart.series[series].visible = true;
    }
}

function hideLines(id)
{
    var chart = $("#" + id).highcharts();
    for(var series in chart.series)
    {
        chart.series[series].visible = false;
    }
}

function showAll(classname)
{
    var chart_containers = document.getElementsByClassName(classname);
    for(var cc in chart_containers)
    {
        showLines(chart_containers[cc].id);
    }
}

function hideAll(classname)
{
    var chart_containers = document.getElementsByClassName(classname);
    for(var cc in chart_containers)
    {
        hideLines(chart_containers[cc].id);
    }
}