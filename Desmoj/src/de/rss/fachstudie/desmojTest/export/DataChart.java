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
    }

    public String printChart() {
        return "<script type='text/javascript'>Highcharts.chart(" + chartId + ", {" + options + "});</script>";
    }
}
