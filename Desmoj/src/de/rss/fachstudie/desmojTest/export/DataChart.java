package de.rss.fachstudie.desmojTest.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataChart {
    private String chartId = "";
    private HashMap<String, Integer[]> series;
    private String options = "";

    public DataChart(String chartId, HashMap<String, Integer[]> series) {
        this.chartId = chartId;
        this.series = series;
        /*this.options =
                "title : { text : '" + chartId + "'}," +
                "series : " +
                "[";
        for(String key : series.keySet()) {
            options +=
                    "{" +
                        "name : " + key + "," +
                        "data : [";
            for(Integer value : series.get(key)) {
                options += value + ", ";
            }
            options = options.substring(0, options.length() - 1) + "]}";
        }
        options += "]";*/
        this.options = "series: [{name: 'Test1', data: [0, 1, 2, 3]}]";
    }

    public String printChart() {
        return "setTimeout(function(){document.getElementsByTagName('body')[0].innerHTML += \"<div id='" + chartId + "'></div>\";" +
                "Highcharts.chart(" + chartId + ", {" + options + "});}, 2000);";
    }
}
