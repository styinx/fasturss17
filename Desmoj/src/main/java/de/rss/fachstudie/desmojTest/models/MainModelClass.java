package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.Microservice;
import de.rss.fachstudie.desmojTest.events.InitialChaosMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialEvent;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.resources.CPU;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import de.rss.fachstudie.desmojTest.utils.InputValidator;
import desmoj.core.simulator.*;
import desmoj.core.statistic.TimeSeries;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class MainModelClass extends Model {
    private TimeUnit timeUnit       = TimeUnit.SECONDS;
    private double simulationTime   = 0;
    private String report           = "";
    private int datapoints          = -1;
    private int seed                = 0;
    private String resourcePath     = "Report/resources/";
    private boolean showInitEvent   = true;
    private boolean showStartEvent  = true;
    private boolean showStopEvent   = true;
    private boolean showMonkeyEvent = true;

    // Queues
    public HashMap<Integer, Queue<Microservice>>    services;
    public HashMap<Integer, Queue<MessageObject>>   taskQueues;
    public HashMap<Integer, Microservice>           allMicroservices;

    // Resources
    public HashMap<Integer, HashMap<Integer, CPU>> serviceCPU;

    // Statistics
    public HashMap<Integer, HashMap<Integer, TimeSeries>> threadStatistics;
    public HashMap<Integer, HashMap<Integer, TimeSeries>> cpuStatistics;
    public HashMap<Integer, HashMap<Integer, TimeSeries>> responseStatisitcs;
    public HashMap<Integer, TimeSeries> circuitBreakerStatistics;
    public HashMap<Integer, HashMap<Integer, TimeSeries>> threadPoolStatistics;
    public HashMap<Integer, HashMap<Integer, TimeSeries>> threadQueueStatistics;
    public HashMap<Integer, TimeSeries> taskQueueStatistics;

    public void  setSimulationTime(double simTime) {
        if(simTime > 0)
            simulationTime = simTime;
        else
            simulationTime = 1000;
    };

    public double getSimulationTime() {
        return simulationTime;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setDatapoints(int points) {
        if(points > 0)
            this.datapoints = points;
        if(points > simulationTime || points == -1) {
            points = (int)simulationTime;
        }
    }

    public int getDatapoints() {
        return this.datapoints;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public boolean getShowInitEvent() {
        return showInitEvent;
    }

    public boolean getShowStartEvent() {
        return showStartEvent;
    }

    public boolean getShowStopEvent() {
        return showStopEvent;
    }

    public boolean getShowMonkeyEvent() {
        return showMonkeyEvent;
    }

    /**
     * Helper Function to get the id of a microservice instance by the name.
     * @param name the name of the service to get the name from
     * @return id of the corresponding microservice if successful, otherwise -1
     */
    public int getIdByName(String name){

        for(int i = 0; i < allMicroservices.size() ; i ++){
            if(name.equals(allMicroservices.get(i).getName())){
                return allMicroservices.get(i).getId();
            }
        }
        return -1;
    }

    public MainModelClass(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

    /**
     * Required method which returns a description for the model.
     * @return the description of the model
     */
    @Override
    public String description() {
        return "This model is a test of Desmoj to investigate the suitability of Desmoj for the simulation of microservice architectures.";
    }

    /**
     * Place all events on the internal event list of the simulator which are necessary to start the simulation.
     */
    @Override
    public void doInitialSchedules() {

        // Fire off all generators for scheduling
        InitialEvent generators[] = InputParser.generators;
        for(InitialEvent generator : generators) {
            InitialEvent initEvent = new InitialEvent(this,"", showInitEvent, generator.getTime(),
                    getIdByName(generator.getMicroservice()), generator.getOperation());
            initEvent.schedule(new TimeSpan(0, timeUnit));
        }

        // Fire off all monkeys for scheduling
        InitialChaosMonkeyEvent monkeys[] = InputParser.monkeys;
        for(InitialChaosMonkeyEvent monkey : monkeys) {
            InitialChaosMonkeyEvent initMonkey = new InitialChaosMonkeyEvent(this, "", showMonkeyEvent,
                    monkey.getTime(), getIdByName(monkey.getMicroservice()), monkey.getInstances());
            initMonkey.schedule(new TimeSpan(0, timeUnit));
        }
    }

    /**
     * Initialize static model components like distributions and queues.
     */
    @Override
    public void init() {
        // Resources
        serviceCPU          = new HashMap<>();

        // Queues
        allMicroservices    = new HashMap<>();
        taskQueues          = new HashMap<>();
        services            = new HashMap<>();

        // Resources
        serviceCPU          = new HashMap<>();

        // Statistics
        threadStatistics            = new HashMap<>();
        cpuStatistics               = new HashMap<>();
        responseStatisitcs          = new HashMap<>();
        circuitBreakerStatistics    = new HashMap<>();
        threadPoolStatistics        = new HashMap<>();
        threadQueueStatistics       = new HashMap<>();
        taskQueueStatistics         = new HashMap<>();

        // Create folder for statistics file
        File resPath = new File("./Report/resources");
        if(!resPath.exists()) {
            resPath.mkdir();
        }

        // Load JSON
        Microservice[] microservices = InputParser.microservices;
        for(int id = 0; id < microservices.length; id++){

            String serviceName = microservices[id].getName();

            // Queues
            Queue<Microservice> idleQueue = new Queue<Microservice>(this, "Idle Queue: " + serviceName, true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + serviceName, true , true) ;

            // Resources
            HashMap<Integer, CPU> cpu = new HashMap<>();
            //Res microserviceCPU = new Res(this, serviceName + " CPU", microservices[id].getCPU(), true, true);

            // Statistics
            HashMap<Integer, TimeSeries> threadStats = new HashMap<>();
            HashMap<Integer, TimeSeries> cpuStats = new HashMap<>();
            HashMap<Integer, TimeSeries> responseStats = new HashMap<>();
            TimeSeries circuitBreakerStats = new TimeSeries(this, "Tasks refused by Circuit Breaker: " + serviceName,
                    resourcePath + "CircuitBreaker_" + serviceName + ".txt",
                    new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);
            HashMap<Integer, TimeSeries> threadPoolStats = new HashMap<>();
            HashMap<Integer, TimeSeries> threadQueueStats = new HashMap<>();

            TimeSeries taskQueueWork = new TimeSeries(this, "Task Queue: " + serviceName,
                    resourcePath + "TaskQueue_" + serviceName + ".txt",
                    new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);


            for(int instance = 0; instance < microservices[id].getInstances(); instance++){
                Microservice msEntity = new Microservice(this , microservices[id].getName(), true );
                msEntity.setId(id);
                msEntity.setSid(instance);
                msEntity.setName(microservices[id].getName());
                msEntity.setPatterns(microservices[id].getPatterns());
                msEntity.setCapacity(microservices[id].getCapacity());
                msEntity.setInstances(microservices[id].getInstances());
                msEntity.setOperations(microservices[id].getOperations());
                idleQueue.insert(msEntity);
                allMicroservices.put(id, msEntity);

                // Resources
                CPU msCPU = new CPU(this, "", false, id, msEntity.getCapacity());
                cpu.put(instance, msCPU);

                // Statistics
                TimeSeries activeInstances = new TimeSeries(this, "Active Threads: " + serviceName + " #" + instance,
                        resourcePath + "Threads_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);

                TimeSeries activeCPU = new TimeSeries(this, "Used CPU: " + serviceName + " #" + instance,
                        resourcePath + "CPU_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);

                TimeSeries responseTime = new TimeSeries(this, "Response Time: " + serviceName + " #" + instance,
                        resourcePath + "ResponseTime_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);

                TimeSeries threadPool = new TimeSeries(this, "Tasks refused by Thread Pool: " + serviceName,
                        resourcePath + "ThreadPool_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);

                TimeSeries threadQueue = new TimeSeries(this, "Tasks refused by Thread Queue: " + serviceName,
                        resourcePath + "ThreadQueue_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), false, false);

                threadStats.put(instance, activeInstances);
                cpuStats.put(instance, activeCPU);
                responseStats.put(instance, responseTime);
                threadPoolStats.put(instance, threadPool);
                threadQueueStats.put(instance, threadQueue);
            }
            // Queues
            taskQueues.put(id, taskQueue);
            services.put(id, idleQueue);

            // Resources
            serviceCPU.put(id, cpu);

            // Statistics
            threadStatistics.put(id, threadStats);
            cpuStatistics.put(id, cpuStats);
            responseStatisitcs.put(id, responseStats);
            circuitBreakerStatistics.put(id, circuitBreakerStats);
            threadPoolStatistics.put(id, threadPoolStats);
            threadQueueStatistics.put(id, threadQueueStats);
            taskQueueStatistics.put(id, taskQueueWork);
            HashMap<String, String> a = new HashMap<>();
        }
    }

    private String timeFormat(long nanosecs) {
        long tempSec = nanosecs / (1000*1000*1000);
        long ms = (nanosecs / (1000*1000)) % 1000;
        long sec = tempSec % 60;
        long min = (tempSec / 60) % 60;
        long hour = (tempSec / (60*60)) % 24;
        long day = (tempSec / (24*60*60)) % 24;

        if(day > 0)
            return String.format("%dd %dh %dm %ds %dms", day, hour, min, sec, ms);
        else if(hour > 0)
            return String.format("%dh %dm %ds %dms", hour, min, sec, ms);
        else if(min > 0)
            return String.format("%dm %ds %dms", min, sec, ms);
        else if(sec > 0)
            return String.format("%ds %dms", sec, ms);
        return String.format("%dms", ms);
    }

    public static void main(String[] args) {
        String arch = "";

        /* Command Line parser uncomment to call from command line */
//        Options options = new Options();
//
//        Option input = new Option("a", "arch", true, "input file path");
//        input.setRequired(true);
//        options.addOption(input);
//
////        Option output = new Option("r", "report", false, "create report");
////        output.setRequired(false);
////        options.addOption(output);
//
//        CommandLineParser cmdparser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd;
//
//        try {
//            cmd = cmdparser.parse(options, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp("Simulator", options);
//            System.exit(1);
//            return;
//        }
//
//        arch = (cmd.getOptionValue("arch").equals("")) ? "example_simple.json" : cmd.getOptionValue("arch");
        if(arch == "")
            arch = "example_simple.json";

        InputParser parser = new InputParser(arch);
        InputValidator validator = new InputValidator();

        if(validator.valideInput(parser)){
            long startTime = System.nanoTime();

            MainModelClass model = new MainModelClass(null, InputParser.simulation.get("model"), true, true);
            model.setSimulationTime(Double.parseDouble(InputParser.simulation.get("duration")));
            model.setReport(InputParser.simulation.get("report"));
            model.setDatapoints(Integer.parseInt(InputParser.simulation.get("datapoints")));
            model.setSeed(Integer.parseInt(InputParser.simulation.get("seed")));

            Experiment exp = new Experiment(InputParser.simulation.get("experiment"));
            model.connectToExperiment(exp);
            exp.setSeedGenerator(model.getSeed());
            exp.setShowProgressBarAutoclose(true);
            exp.stop(new TimeInstant(model.getSimulationTime(), model.getTimeUnit()));
            exp.tracePeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(50, model.getTimeUnit()));
            exp.debugPeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(50, model.getTimeUnit()));

            long setupTime = System.nanoTime() - startTime;
            long tempTime = System.nanoTime();

            exp.start();

            long experimentTime = System.nanoTime() - tempTime;
            tempTime = System.nanoTime();

            exp.report();
            exp.finish();

            ExportReport exportReport = new ExportReport(model);

            long reportTime = System.nanoTime() - tempTime;
            long executionTime = System.nanoTime() - startTime;

            System.out.println("\n*** Simulator ***");
            System.out.println("Simulation of Architecture:\t" + arch);
            System.out.println("Executed Experiment:\t\t" + InputParser.simulation.get("experiment"));
            System.out.println("Setup took:\t\t\t\t\t" + model.timeFormat(setupTime));
            System.out.println("Experiment took:\t\t\t" + model.timeFormat(experimentTime));
            System.out.println("Report took:\t\t\t\t" + model.timeFormat(reportTime));
            System.out.println("Execution took:\t\t\t\t" + model.timeFormat(executionTime));
        } else {
            System.out.println("Your inserted input was not valide. Please check correctness of you JSON file.");
        }
    }
}
