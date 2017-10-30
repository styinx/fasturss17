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
                "legend : {enabled: true}," +
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
        if(series.keySet().size() > 0)
            options = options.substring(0, options.length() - 1) + "]";
        else
            options += "]";
    }

    public String printDiv() {
        return "document.getElementById('chart-container').innerHTML += \"<div id='" + chartId + "' class='stat-chart'></div>\";\n";
    }

    public String printChart() {
        return "Highcharts.stockChart('" + chartId + "', {" + options + "});\n";
    }

    public String printStockChart() {
        return "Highcharts.stockChart('" + chartId + "', {" + options + "});\n";
    }
}
