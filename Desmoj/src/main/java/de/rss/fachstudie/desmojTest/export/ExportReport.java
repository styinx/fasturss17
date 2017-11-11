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
        DependecyGraph graph = new DependecyGraph(model, model.allMicroservices, 0);

        try {
            Files.write(Paths.get("./Report/js/graph.js"), graph.printGraph().getBytes());
            System.out.println("\nCreated graph report.");
        } catch (IOException ex) {
            System.out.println("\nCould not create graph report.");
        }
    }

    private void chartReport() {
        TreeMap<String, TreeMap<Double, Double>> activeInstances = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> taskQueueWork = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> usedCPU = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> responseTime = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> circuitBreaker = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> threadPool = new TreeMap<>();
        TreeMap<String, TreeMap<Double, Double>> threadQueue = new TreeMap<>();

        for(int id = 0; id < model.services.size(); id++) {
            String serviceName = model.services.get(id).get(0).getName();
            int instanceLimit = model.services.get(id).get(0).getInstances();

            if(InputParser.simulation.get("report").equals("minimalistic")) {
                instanceLimit = (model.services.get(id).get(0).getInstances() < 10) ?
                        model.services.get(id).get(0).getInstances() : 10;
            }

            for(int instance = 0; instance < instanceLimit; instance++) {
                
                Microservice ms = model.services.get(id).get(instance);
                String file = ms.getName() + "_" + instance + ".txt";
                
                activeInstances.put(ms.getName() + " #" + instance, this.getTimeSeriesWithKeys(resourcePath + "Threads_" + file));
                usedCPU.put(ms.getName() + " #" + instance, this.getTimeSeriesWithKeys(resourcePath + "CPU_" + file));
                responseTime.put(ms.getName() + " #" + instance, this.getTimeSeriesWithKeys(resourcePath + "ResponseTime_" + file));
                threadPool.put(ms.getName() + " #" + instance, this.getTimeSeriesWithKeys(resourcePath + "ThreadPool_" + file));
                threadQueue.put(ms.getName() + " #" + instance, this.getTimeSeriesWithKeys(resourcePath + "ThreadQueue_" + file));
            }
            circuitBreaker.put(serviceName, this.getTimeSeriesWithKeys(resourcePath + "CircuitBreaker_" + serviceName + ".txt"));
            taskQueueWork.put(serviceName, this.getTimeSeriesWithKeys(resourcePath + "TaskQueue_" + serviceName + ".txt"));
        }

        DataChart chart1 = new DataChart(model, "Active Microservice Threads", activeInstances);
        DataChart chart2 = new DataChart(model, "Task Queue per Service", taskQueueWork);
        DataChart chart3 = new DataChart(model, "Used CPU in percent", usedCPU);
        DataChart chart4 = new DataChart(model, "Thread Response Time", responseTime);
        DataChart chart5 = new DataChart(model, "Tasks refused by Circuit Breaker", circuitBreaker);
        DataChart chart6 = new DataChart(model, "Tasks refused by Thread Pool", threadPool);
        DataChart chart7 = new DataChart(model, "Tasks refused by Thread Queue", threadQueue);

        Table table1 = new Table("Active Microservice Threads", activeInstances);
        Table table2 = new Table("Task Queue per Service", taskQueueWork);
        Table table3 = new Table("Used CPU in percent", usedCPU);
        Table table4 = new Table("Thread Response Time", responseTime);
        Table table5 = new Table("Tasks refused by Circuit Breaker", circuitBreaker);
        Table table6 = new Table("Tasks refused by Thread Pool", threadPool);
        Table table7 = new Table("Tasks refused by Thread Queue", threadQueue);

        String divs = chart1.printDiv() + table1.printTable()
                + chart2.printDiv() + table2.printTable()
                + chart3.printDiv() + table3.printTable()
                + chart4.printDiv() + table4.printTable()
                + chart5.printDiv() + table5.printTable()
                + chart6.printDiv() + table6.printTable()
                + chart7.printDiv() + table7.printTable();

        String charts = chart1.printStockChart()
                + chart2.printStockChart()
                + chart3.printStockChart()
                + chart4.printStockChart()
                + chart5.printStockChart()
                + chart6.printStockChart()
                + chart7.printStockChart();

        String contents = divs + charts;

        try {
            Files.write(Paths.get("./Report/js/chart.js"), contents.getBytes());
            System.out.println("\nCreated chart report.");
        } catch (IOException ex) {
            System.out.println("\nCould not create chart report.");
        }
    }

    private TreeMap<Double, Double> getTimeSeriesWithKeys(String filename) {
        TreeMap<Double, Double> values = new TreeMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if(index > 0) {
                    String kvp[] = line.split("\\s+");
                    if(kvp.length > 1) {
                        values.put(Double.parseDouble(kvp[0]), Double.parseDouble(kvp[1]));
                    }
                }
                index++;
            }
        } catch (IOException ex) {
            System.out.println("Error while reading file: " + filename);
        }
        return values;
    }
}