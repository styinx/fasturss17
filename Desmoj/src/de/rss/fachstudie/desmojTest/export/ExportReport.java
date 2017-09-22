package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.DesmojTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExportReport {
    DependecyGraph graph;
    DataChart chart;

    public ExportReport(DesmojTest model) {
        graph = new DependecyGraph(model.allMicroservices, 0);
        chart = new DataChart("dummy");

        try {
            Files.write(Paths.get("./js/graph.js"), graph.printGraph().getBytes());
        } catch (IOException ex) {
            System.out.println("");
        }
    }
}
