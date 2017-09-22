package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.DesmojTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ExportReport {
    DependecyGraph graph;
    DataChart chart;

    public ExportReport(DesmojTest model) {
        HashMap<String, Integer[]> testData = new HashMap<>();
        testData.put("Test1", new Integer[]{0, 1, 2, 3});
        graph = new DependecyGraph(model.allMicroservices, 0);
        chart = new DataChart("dummy", null);

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
        } catch (IOException ex) {
            System.out.println("");
        }
    }
}
