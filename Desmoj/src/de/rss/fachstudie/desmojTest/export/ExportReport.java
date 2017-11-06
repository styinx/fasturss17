package de.rss.fachstudie.desmojTest.export;

import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import de.rss.fachstudie.desmojTest.utils.InputParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ExportReport {
    private MainModelClass model;
    private String resourcePath = "Report/resources/";

    public ExportReport(MainModelClass model) {
        this.model = model;
        this.graphReport();
        this.chartReport();

    }

    private void graphReport() {
        DependecyGraph graph = new DependecyGraph(model.allMicroservices, 0);

        try {
            String content = "";
            if(InputParser.simulation.get("report").equals("minimalistic")) {
                content += "var graphMinimalistic = true;\n";
            }
            Files.write(Paths.get("./Report/js/graph.js"), (content + graph.printGraph()).getBytes());
            System.out.println("\nCreated graph report.");
        } catch (IOException ex) {
            System.out.println("\nCould not create graph report.");
        }
    }

    private void chartReport() {
        TreeMap<String, TreeMap<Integer, Double>> activeInstances = new TreeMap<>();
        TreeMap<String, TreeMap<Integer, Double>> usedCPU = new TreeMap<>();
        TreeMap<String, TreeMap<Integer, Double>> responseTime = new TreeMap<>();
        TreeMap<String, TreeMap<Integer, Double>> circuitBreaker = new TreeMap<>();
        TreeMap<String, TreeMap<Integer, Double>> taskQueueWork = new TreeMap<>();

        for(int id = 0; id < model.services.size(); id++) {
            String serviceName = model.services.get(id).get(0).getName();
            int instanceLimit = model.services.get(id).get(0).getInstances();

            if(InputParser.simulation.get("report").equals("minimalistic")) {
                instanceLimit = (model.services.get(id).get(0).getInstances() < 10) ?
                        model.services.get(id).get(0).getInstances() : 10;
            }

            for(int instance = 0; instance < instanceLimit; instance++) {
                Microservice ms = model.services.get(id).get(instance);
                activeInstances.put(ms.getName() + " #" + instance,
                        this.getTimeSeriesWithKeys(resourcePath + "Threads_" + ms.getName() + "_" + instance + ".txt"));
                usedCPU.put(ms.getName() + " #" + instance,
                        this.getTimeSeriesWithKeys(resourcePath + "CPU_" + ms.getName() + "_" + instance + ".txt"));
                responseTime.put(ms.getName() + " #" + instance,
                        this.getTimeSeriesWithKeys(resourcePath + "ResponseTime_" + ms.getName() + "_" + instance + ".txt"));
            }
            circuitBreaker.put(serviceName, this.getTimeSeriesWithKeys(resourcePath + "CircuitBreaker_" + serviceName + ".txt"));
            taskQueueWork.put(serviceName, this.getTimeSeriesWithKeys(resourcePath + "TaskQueue_" + serviceName + ".txt"));
        }

        DataChart chart1 = new DataChart(model, "Active Microservice Threads", activeInstances);
        DataChart chart2 = new DataChart(model, "Used CPU in percent", usedCPU);
        DataChart chart3 = new DataChart(model, "Thread Response Time", responseTime);
        DataChart chart4 = new DataChart(model, "Tasks refused by Circuit Breaker", circuitBreaker);
        DataChart chart5 = new DataChart(model, "Task Queue per Service", taskQueueWork);

        String divs = chart1.printDiv()
                + chart2.printDiv()
                + chart3.printDiv()
                + chart4.printDiv()
                + chart5.printDiv();

        String charts = chart1.printStockChart()
                + chart2.printStockChart()
                + chart3.printStockChart()
                + chart4.printStockChart()
                + chart5.printStockChart();

        String contents = divs + charts;

        try {
            Files.write(Paths.get("./Report/js/chart.js"), contents.getBytes());
            System.out.println("\nCreated chart report.");
        } catch (IOException ex) {
            System.out.println("\nCould not create chart report.");
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

    private TreeMap<Integer, Double> getTimeSeriesWithKeys(String filename) {
        TreeMap<Integer, Double> values = new TreeMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if(index > 0) {
                    String kvp[] = line.split("\\s+");
                    if(kvp.length > 1) {
                        Double value = Double.parseDouble(kvp[0]);
                        values.put(value.intValue(), Double.parseDouble(kvp[1]));
                    }
                }
                index++;
            }
        } catch (IOException ex) {
            System.out.println("Error while reading file: " + filename);
        }
//        Double[] result = new Double[values.size()];
//        result = values.toArray(result);
        return values;
    }

    private Double[] getResponsTime(HashMap<Integer, Double> data) {
        Double[] result = new Double[data.size()];
        result = data.values().toArray(result);
        return result;
    }
}
