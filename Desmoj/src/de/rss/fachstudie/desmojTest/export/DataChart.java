package de.rss.fachstudie.desmojTest.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataChart {
    private String chartId = "";
    private HashMap<String, Double[]> series;
    private String options = "";

    public DataChart(String chartId, HashMap<String, Double[]> series) {
        this.chartId = chartId;
        this.series = series;
        this.options =
                "title : { text : '" + chartId + "'}," +
                "credits : {enabled: false}," +
                "series : " +
                "[";
        for(String key : series.keySet()) {
            options +=
                    "{" +
                        "name : '" + key + "'," +
                        "data : [";
            for(Double value : series.get(key)) {
                options += value.toString() + ", ";
            }
            options = options.substring(0, options.length() - 1) + "]},";
        }
        options = options.substring(0, options.length() - 1) + "]";
    }

    public String printDiv() {
        return "document.getElementById('chart-container').innerHTML += \"<div id='" + chartId + "'></div>\";\n";
    }

    public String printChart() {
        return "Highcharts.chart('" + chartId + "', {" + options + "});\n";
    }
}
