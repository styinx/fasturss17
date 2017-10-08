package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExportReport {
    private DesmojTest model;

    public ExportReport(DesmojTest model) {
        this.model = model;
        this.graphReport();
        this.chartReport();

    }

    private void graphReport() {
        DependecyGraph graph = new DependecyGraph(model.allMicroservices, 0);

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create graph report.");
        }
    }

    private void chartReport() {
        HashMap<String, Double[]> testData1 = new HashMap<>();
        HashMap<String, Double[]> testData2 = new HashMap<>();
        HashMap<String, Double[]> testData3 = new HashMap<>();

        for(int i = 0; i < model.allMicroservices.size(); i++) {
            MicroserviceEntity ms = model.allMicroservices.get(i);
            testData1.put("Active Instances " + ms.getName(),
                    this.getTimeSeries("Report/resources/Instances_" + ms.getName() + ".txt"));
        }

        for(int i = 0; i < model.allMicroservices.size(); i++) {
            MicroserviceEntity ms = model.allMicroservices.get(i);
            testData2.put("Used CPU " + ms.getName(),
                    this.getTimeSeries("Report/resources/CPU_" + ms.getName() + ".txt"));
        }

        DataChart chart1 = new DataChart("Active Microservice Instances", testData1);
        DataChart chart2 = new DataChart("Used CPU", testData2);

        String divs = chart1.printDiv() + chart2.printDiv();
        String charts = chart1.printChart() + chart2.printChart();
        String contents = divs + charts;

        try {
            Files.write(Paths.get("./Report/js/chart.js"), contents.getBytes());
        } catch (IOException ex) {
            System.out.println("Could not create chart report.");
        }
    }

    private Double[] getTimeSeries(String filename) {
        List<Double> values = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if(index > 0) {
                    String kvp[] = line.split("\\s+");
                    if(kvp.length > 1) {
                        values.add(Double.parseDouble(kvp[1]));
                    }
                }
                index++;
            }
        } catch (IOException ex) {
            System.out.println("Error while reading file: " + filename);
        }
        Double[] result = new Double[values.size()];
        result = values.toArray(result);
        return result;
    }
}
