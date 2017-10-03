package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.models.DesmojTest;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ExportReport {
    private DesmojTest model;

    public ExportReport(DesmojTest model) {
        this.model = model;
        this.graphReport();
        this.chartReport();

    }

    private void graphReport() {
        DependecyGraph graph = new DependecyGraph(model.allMicroservices, 0);;

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create graph report.");
        }
    }

    private void chartReport() {
        HashMap<String, Integer[]> testData1 = new HashMap<>();
        HashMap<String, Integer[]> testData2 = new HashMap<>();
        HashMap<String, Integer[]> testData3 = new HashMap<>();
        testData1.put("Idle", new Integer[]{0, 1, 2, 3});
        testData2.put("Task", new Integer[]{1, 2, 4, 8});
        testData3.put("CPU 1", new Integer[]{1, 3, 9, 27});
        testData3.put("CPU 2", new Integer[]{8, 16, 24, 20});

        DataChart chart1 = new DataChart("Idle", testData1);
        DataChart chart2 = new DataChart("Throughput", testData2);
        DataChart chart3 = new DataChart("Performance", testData3);

        String divs = chart1.printDiv() + chart2.printDiv() + chart3.printDiv();
        String charts = chart1.printChart() + chart2.printChart() + chart3.printChart();
        String contents = divs + charts;

        try {
            Files.write(Paths.get("./Report/js/chart.js"), contents.getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create chart report.");
        }
    }
}
