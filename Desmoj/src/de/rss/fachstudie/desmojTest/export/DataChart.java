package de.rss.fachstudie.desmojTest.export;

public class DataChart {
    private String chartId = "";
    private String options = "";

    public DataChart(String chartId) {
        this.chartId = chartId;
        this.options =
                "title : { text : '" + chartId + "'}" +
                "series : " +
                "[" +
                        "{" +
                            "name : 'Test1'" +
                            "data : [0, 1, 2, 3, 4, 5]" +
                        "}," +
                        "{" +
                            "name : 'Test2'" +
                            "data : [0, 2, 4, 0, 2, 1]" +
                        "}" +
                "]";
    }

    public String printChart() {
        return "<div id='" + chartId + "'></div>" +
                "<script type='text/javascript'>Highcharts.chart('chart', {" + options + "});</script>";
    }
}
