package de.rss.fachstudie.desmojTest.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataChart {
    private String chartId = "";
    private TreeMap<String, Double[]> series;
    private String options = "";

    public DataChart(String chartId, TreeMap<String, Double[]> series) {
        this.chartId = chartId;
        this.series = series;
        this.options =
                "title : { text : '" + chartId + "'}, " +
                "legend : {enabled: true}, " +
                "colors : colors(" + series.keySet().size() + "), " +
                "series : " +
                "[";
        int index = 0;
        for(String key : series.keySet()) {
            options +=
                    "{" +
                        "name : '" + key + "', " +
                        "index : " + index + ", " +
                        "data : [";
            for(Double value : series.get(key)) {
                options += value.toString() + ", ";
            }
            options = options.substring(0, options.length() - 1) + "]}, ";
            index++;
        }
        if(series.keySet().size() > 0)
            options = options.substring(0, options.length() - 1) + "] ";
        else
            options += "] ";
    }

    public String printDiv() {
        return "document.getElementById('chart-container').innerHTML += \"<div id='" + chartId.replace(" ", "_") + "' class='stat-chart'></div>"
            + "<button onclick=\\\"toggleLines('" + chartId.replace(" ", "_") + "');\\\">Toggle Visibility</button>"
            + "<button onclick=\\\"unsmoothYAxis('" + chartId.replace(" ", "_") + "');\\\">Unsmooth YAxis</button>"
            + "<button onclick=\\\"smoothYAxis('" + chartId.replace(" ", "_") + "');\\\">Smooth YAxis</button>\"\n";
    }

    public String printChart() {
        return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
    }

    public String printStockChart() {
        return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
    }
}
