package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.MainModelClass;

import java.util.List;
import java.util.TreeMap;

public class DataChart {
    private String chartId = "";
    private String options = "";

    /**
     * A Chart with y values
     * @param model     Desmoj model
     * @param chartId   id of the chart
     * @param series    data that will be plotted
     * @param dummy     variable to distinct from second constructor
     */
    public DataChart(MainModelClass model, String chartId, TreeMap<String, Double[]> series, boolean dummy) {
        this.chartId = chartId;
        this.options =
                "title : {text : '" + chartId + "'}, " +
                        "legend : {enabled: true}, " +
                        "xAxis: {max:" + model.getSimulationTime() + "}, " +
                        "colors : colors(" + series.keySet().size() + "), " +
                        "series : " +
                        "[";
        int index = 0;
        for(String key : series.keySet()) {
            options += "{"
                    + "name : '" + key + "', "
                    + "index : " + index + ", "
                    + "data : [ ";

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

    /**
     * A Chart with x and y values
     * @param model     Desmoj model
     * @param chartId   id of the chart
     * @param series    data that will be plotted
     */
    public DataChart(MainModelClass model, String chartId, TreeMap<String, TreeMap<Integer, Double>> series) {
        this.chartId = chartId;
        this.options =
                "title : {text : '" + chartId + "'}, " +
                        "legend : {enabled: true}, " +
                        "xAxis: {min: 0, max:" + model.getSimulationTime() + "}, " +
                        "colors : colors(" + series.keySet().size() + "), " +
                        "series : " +
                        "[ ";
        int index = 0;
        for(String key : series.keySet()) {
            options += "{"
                    + "name : '" + key + "', "
                    + "index : " + index + ", "
                    + "data : [ ";

            TreeMap<Integer, Double> map = series.get(key);
            int last = 0;
            for(int i = 0; i < model.getSimulationTime(); i += Math.round(model.getSimulationTime()/model.getDatapoints())) {
                if(map.get(i) != null) {
                    last = i;
                    options += "[" + i + ", " + Math.round(map.get(i) * 100.0) / 100.0 + "], ";
                }
                else {
                    if(map.get(last) == null)
                        options += "[" + i + ", " + 0 + "], ";
                    else
                        options += "[" + i + ", " + Math.round(map.get(last) * 100.0) / 100.0 + "], ";
                }
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

    /**
     * A standart chart plots lines for each series
     * @return js code for the chart
     */
    public String printChart() {
        return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
    }

    /**
     * A stock chart is able to compare multiple values and has a scrollbar
     * @return js code for the chart
     */
    public String printStockChart() {
        return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
    }
}
