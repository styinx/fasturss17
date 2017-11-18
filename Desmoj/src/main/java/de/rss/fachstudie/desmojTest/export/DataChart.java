package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.MainModelClass;

import java.util.TreeMap;

public class DataChart {
    private String chartId = "";
    private String options = "";
    private boolean empty = true;

    /**
     * A Chart with x and y values
     * @param model     Desmoj model
     * @param chartId   id of the chart
     * @param series    data that will be plotted
     */
    public DataChart(MainModelClass model, String chartId, TreeMap<String, TreeMap<Double, Double>> series) {
        this.chartId = chartId;
        this.options = "title : {text : '" + chartId + "'}, "
                + "legend: {enabled : true}, "
                + "xAxis: {min: 0, max:" + model.getSimulationTime() + "}, "
                + "colors : colors(" + series.keySet().size() + "), "
                + "series : "
                + "[ ";
        int index = 0;

        for (String mapkey : series.keySet()) {
            if (series.get(mapkey).size() > 1)
                empty = false;

            options += "{"
                    + "name : '" + mapkey + "', "
                    + "index : " + index + ", "
                    + "dataGrouping: {enabled: false}, "
                    + "data : [ ";

            TreeMap<Double, Double> map = series.get(mapkey);

            if (map.keySet().size() == 0)
                map.put(model.getSimulationTime(), 0.0);

            for(double x : map.keySet()) {
                double key = Math.round(x * 100.0) / 100.0;

                options += "[" + key + ", " + Math.round(map.get(x) * 100.0) / 100.0 + "], ";
            }
            options = options.substring(0, options.length() - 2) + "]}, ";
            index++;
        }
        if(series.keySet().size() > 0)
            options = options.substring(0, options.length() - 2) + "]";
        else
            options += "]";
    }

    public String printDiv() {
        if (!empty)
            return "document.getElementById('chart-container').innerHTML += \"<div id='" + chartId.replace(" ", "_") + "' class='stat-chart'></div>"
                    + "<button onclick=\\\"toggleLines('" + chartId.replace(" ", "_") + "');\\\">Toggle Visibility</button>"
                    + "<button onclick=\\\"unsmoothYAxis('" + chartId.replace(" ", "_") + "');\\\">Unsmooth YAxis</button>"
                    + "<button onclick=\\\"smoothYAxis('" + chartId.replace(" ", "_") + "');\\\">Smooth YAxis</button>\"\n";
        return "";
    }

    /**
     * A standart chart plots lines for each series
     * @return js code for the chart
     */
    public String printChart() {
        if (!empty)
            return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
        return "";
    }

    /**
     * A stock chart is able to compare multiple values and has a scrollbar
     * @return js code for the chart
     */
    public String printStockChart() {
        if (!empty)
            return "Highcharts.stockChart('" + chartId.replace(" ", "_") + "', {" + options + "});\n";
        return "";
    }
}
