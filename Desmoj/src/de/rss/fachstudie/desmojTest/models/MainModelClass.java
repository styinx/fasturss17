package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.events.InitialChaosMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StatisticCollectorEvent;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import de.rss.fachstudie.desmojTest.utils.InputValidator;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;

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
    private int datapoints          = 0;
    private boolean showInitEvent   = false;
    private boolean showStartEvent  = false;
    private boolean showStopEvent   = false;
    private boolean showMonkeyEvent = false;

    // Queues
    public HashMap<Integer, Queue<MicroserviceEntity>>   idleQueues;
    public HashMap<Integer, Queue<MessageObject>>        taskQueues;
    public HashMap<Integer, MicroserviceEntity>          allMicroservices;

    // Resources
    public HashMap<Integer, HashMap<Integer, Integer>> serviceCPU;
    //public HashMap<Integer, HashMap<Integer, Res>> serviceCPU;

    // Statistics
    public HashMap<Integer, HashMap<Integer, TimeSeries>> cpuStatistics;
    public HashMap<Integer, HashMap<Integer, TimeSeries>> threadStatistics;
    //public HashMap<Integer, HashMap<Integer, TimeSeries>> threadTime;

    public double getSimulationTime() {
        return simulationTime;
    }

    public double getDatapoints() {
        return datapoints;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean getShowInitEvent() {
        return showInitEvent;
    }

    public void setShowInitEvent(boolean showInitEvent) {
        this.showInitEvent = showInitEvent;
    }

    public boolean getShowStartEvent() {
        return showStartEvent;
    }

    public void setShowStartEvent(boolean showStartEvent) {
        this.showStartEvent = showStartEvent;
    }

    public boolean getShowStopEvent() {
        return showStopEvent;
    }

    public void setShowStopEvent(boolean showStopEvent) {
        this.showStopEvent = showStopEvent;
    }

    public boolean getShowMonkeyEvent() {
        return showMonkeyEvent;
    }

    public void setShowMonkeyEvent(boolean showMonkeyEvent) {
        this.showMonkeyEvent = showMonkeyEvent;
    }

    /**
     * Helper Function to get the id of a microservice instance by the name.
     * @param name
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

    /**
     * Chooses the service with most resources and space available
     * @param id
     * @return
     */
    public MicroserviceEntity getServiceEntity(int id) {
        double min = Double.POSITIVE_INFINITY;
        int instance = 0;
        for(int i = 0; i < idleQueues.get(id).size(); ++i) {
            if(serviceCPU.get(id).get(i) <= min) {
                min = serviceCPU.get(id).get(i);
                instance = i;
            }
        }
        return idleQueues.get(id).get(instance);
    }

    /**
     *
     * @param owner
     * @param modelName
     * @param showInReport
     * @param showInTrace
     */
    public MainModelClass(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

    /**
     * Required method which returns a description for the model.
     * @return
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
        InitialMicroserviceEvent generators[] = InputParser.generators;
        for(InitialMicroserviceEvent generator : generators) {
            InitialMicroserviceEvent initEvent = new InitialMicroserviceEvent(this,
                    "<b><u>Inital Event:</u></b> " + generator.getMicroservice(), showInitEvent, generator.getTime(),
                    getIdByName(generator.getMicroservice()), generator.getOperation());
            initEvent.schedule(new TimeSpan(0, timeUnit));
        }

        // Fire off all monkeys for scheduling
        InitialChaosMonkeyEvent monkeys[] = InputParser.monkeys;
        for(InitialChaosMonkeyEvent monkey : monkeys) {
            InitialChaosMonkeyEvent initMonkey = new InitialChaosMonkeyEvent(this,
                    "<b><u>Monkey Event:</u></b> Kill " + monkey.getMicroservice(), showMonkeyEvent, monkey.getTime(),
                    getIdByName(monkey.getMicroservice()), monkey.getInstances());
            initMonkey.schedule(new TimeSpan(0, timeUnit));
        }

        // Fire off statistics collection
        StatisticCollectorEvent collectorEvent = new StatisticCollectorEvent(this, "", false);
        collectorEvent.schedule(new TimeSpan(0, timeUnit));
    }

    /**
     * Initialize static model components like distributions and queues.
     */
    @Override
    public void init() {
        // Globals
        simulationTime = Double.parseDouble(InputParser.simulation.get("duration"));
        datapoints = Integer.parseInt(InputParser.simulation.get("datapoints"));

        // Queues
        allMicroservices    = new HashMap<>();
        taskQueues          = new HashMap<>();
        idleQueues          = new HashMap<>();

        // Resources
        serviceCPU          = new HashMap<>();

        // Statistics
        threadStatistics    = new HashMap<>();
        cpuStatistics       = new HashMap<>();

        // Load JSON
        MicroserviceEntity[] microservices = InputParser.microservices;
        for(int id = 0; id < microservices.length; id++){

            String serviceName = microservices[id].getName();

            // Queues
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, "Idle Queue: " + serviceName, true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + serviceName, true , true) ;

            // Resources
            HashMap<Integer, Integer> cpu = new HashMap<>();
            //Res microserviceCPU = new Res(this, serviceName + " CPU", microservices[id].getCPU(), true, true);

            // Statistics
            HashMap<Integer, TimeSeries> threadStats = new HashMap<>();
            HashMap<Integer, TimeSeries> cpuStats = new HashMap<>();

            //Queue for maxQueue returns refuse and should be used to turn Circuit breakers of with using a waiting queue 1 ( 0 for int max value)
            //Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + microservices[id].getName(), QueueBased.FIFO , 1, true , true);

            for(int instance = 0; instance < microservices[id].getInstances(); instance++){
                MicroserviceEntity msEntity = new MicroserviceEntity(this , microservices[id].getName(), true );
                msEntity.setName(microservices[id].getName());
                msEntity.setId(id);
                msEntity.setSid(instance);
                msEntity.setCPU(microservices[id].getCPU());
                msEntity.setInstances(microservices[id].getInstances());
                msEntity.setOperations(microservices[id].getOperations());
                idleQueue.insert(msEntity);
                allMicroservices.put(id, msEntity);

                // Resources
                cpu.put(instance, msEntity.getCPU());

                // Statistics
                TimeSeries activeInstances = new TimeSeries(this, "Active Threads: " + serviceName + " #" + instance,
                        "Report/resources/Threads_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), true, false);

                TimeSeries activeCPU = new TimeSeries(this, "Used CPU: " + serviceName + " #" + instance,
                        "Report/resources/CPU_" + serviceName + "_" + msEntity.getSid() + ".txt",
                        new TimeInstant(0.0, timeUnit), new TimeInstant(simulationTime, timeUnit), true, false);

                threadStats.put(instance, activeInstances);
                cpuStats.put(instance, activeCPU);
            }
            // Queues
            taskQueues.put(id, taskQueue);
            idleQueues.put(id, idleQueue);

            // Resources
            serviceCPU.put(id, cpu);

            // Statistics
            threadStatistics.put(id, threadStats);
            cpuStatistics.put(id, cpuStats);
        }
    }

    public static void main(String[] args) {

        InputParser parser = new InputParser("example_basic.json");
        InputValidator validator = new InputValidator();

        if(validator.valideInput(parser)){
            MainModelClass model = new MainModelClass(null, InputParser.simulation.get("model"), true, true);
            Experiment exp = new Experiment(InputParser.simulation.get("experiment"));

            model.connectToExperiment(exp);
            exp.setSeedGenerator(Integer.parseInt(InputParser.simulation.get("seed")));
            exp.setShowProgressBarAutoclose(true);
            exp.stop(new TimeInstant(Double.parseDouble(InputParser.simulation.get("duration")), model.getTimeUnit()));
            exp.tracePeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(250, model.getTimeUnit()));
            exp.debugPeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(50, model.getTimeUnit()));

            exp.start();

            exp.report();
            exp.finish();

            ExportReport exportReport = new ExportReport(model);
        } else {
            System.out.println("Your inserted input was not valide. Please check correctness of you JSON file.");
        }
    }
}
