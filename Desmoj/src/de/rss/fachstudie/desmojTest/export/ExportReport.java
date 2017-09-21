package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.MSSimulator;

public class ExportReport {
    // save some values from data collectors (is part of the desmoj model [http://desmoj.sourceforge.net/basic_features.html])
    // and write them into the report
    public ExportReport(MSSimulator model) {
        // write data output html file
        // containing graph dependecies and some graphs

        String html = "";

        DependecyGraph graph = new DependecyGraph(model.allMicroservices, 0);
        graph.printGraph(graph.getRoot());

        DataChart chart = new DataChart("dummy");
        html += chart.printChart();
    }
}
