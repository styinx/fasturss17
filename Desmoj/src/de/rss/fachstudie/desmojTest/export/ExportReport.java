package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.DesmojTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ExportReport {
    private DesmojTest model;
    private DependecyGraph graph;
    private DataChart chart;

    public ExportReport(DesmojTest model) {
        this.model = model;
        this.graphReport();
        this.chartReport();

    }

    private void graphReport() {
        graph = new DependecyGraph(model.allMicroservices, 0);;

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create graph report.");
        }
    }

    private void chartReport() {
        HashMap<String, Integer[]> testData = new HashMap<>();
        testData.put("Test1", new Integer[]{0, 1, 2, 3});
        chart = new DataChart("dummy", testData);

        try {
            Files.write(Paths.get("./Report/js/chart.js"), chart.printChart().getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create chart report.");
        }
    }
}
