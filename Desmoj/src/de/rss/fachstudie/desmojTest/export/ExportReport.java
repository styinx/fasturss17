package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.simulator.TimeInstant;

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
    private MainModelClass model;

    public ExportReport(MainModelClass model) {
        this.model = model;
        this.graphReport();
        this.chartReport();

    }

    private void graphReport() {
        DependecyGraph graph = new DependecyGraph(model.allMicroservices, 0);

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
            System.out.println("Created graph report.");
        } catch (IOException ex) {
            System.out.println("Could not create graph report.");
        }
    }

    private void chartReport() {
        HashMap<String, Double[]> activeInstances = new HashMap<>();
        HashMap<String, Double[]> usedCPU = new HashMap<>();
        HashMap<String, Double[]> responseTime = new HashMap<>();

        for(int id = 0; id < model.idleQueues.size(); id++) {
            for(int instance = 0; instance < model.idleQueues.get(id).get(0).getInstances(); instance++) {
                MicroserviceEntity ms = model.idleQueues.get(id).get(instance);
                activeInstances.put(ms.getName() + " #" + instance,
                        this.getTimeSeries("Report/resources/Threads_" + ms.getName() + "_" + instance + ".txt"));
                usedCPU.put(ms.getName() + " #" + instance,
                        this.getTimeSeries("Report/resources/CPU_" + ms.getName() + "_" + instance + ".txt"));
                responseTime.put(ms.getName() + " #" + instance,
                        this.getResponsTime(ms.getResponseTime()));
            }
        }

        DataChart chart1 = new DataChart("Active Microservice Threads", activeInstances);
        DataChart chart2 = new DataChart("Used CPU", usedCPU);
        DataChart chart3 = new DataChart("Response Time per Instance", responseTime);

        String divs = chart1.printDiv() + chart2.printDiv() + chart3.printDiv();
        String charts = chart1.printStockChart() + chart2.printStockChart() + chart3.printStockChart();
        String contents = divs + charts;

        try {
            Files.write(Paths.get("./Report/js/chart.js"), contents.getBytes());
            System.out.println("Created chart report.");
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

    private Double[] getResponsTime(HashMap<Integer, Double> data) {
        Double[] result = new Double[data.size()];
        result = data.values().toArray(result);
        return result;
    }
}
