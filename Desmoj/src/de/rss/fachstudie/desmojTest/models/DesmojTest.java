package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.events.InitialChaosMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StatisticCollectorEvent;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import desmoj.core.simulator.*;
import desmoj.core.statistic.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class DesmojTest extends Model {
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
    public HashMap<Integer, Integer> serviceCPU;

    // Statistics
    public HashMap<Integer, Count> serviceCounter;
    public HashMap<Integer, Tally> serviceTally;
    public HashMap<Integer, Accumulate> serviceAccumulate;
    public HashMap<Integer, Histogram> serviceHistogram;
    public HashMap<Integer, Regression> serviceRegression;
    public HashMap<Integer, HashMap<String, TimeSeries>> serviceTimeseries;

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
/*
    // May be needed for synchronous operations
    public Operation getPredecessor(String name) {
        for(int i = 0; i < allMicroservices.size(); ++i) {
            for(int j = 0; j < allMicroservices.get(i).getOperations().length; ++j) {
                if(allMicroservices.get(i).getOperations()[j].getName().equals(name)) {

                }
            }
        }
    }*/

    /**
     *
     * @param owner
     * @param modelName
     * @param showInReport
     * @param showInTrace
     */
    public DesmojTest(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
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
        // Fire all generators
        InitialMicroserviceEvent generators[] = InputParser.generators;
        for(InitialMicroserviceEvent generator : generators) {
            InitialMicroserviceEvent initEvent = new InitialMicroserviceEvent(this,
                    "<b><u>Inital Event:</u></b> " + generator.getMicroservice(), showInitEvent, generator.getTime(),
                    getIdByName(generator.getMicroservice()));
            initEvent.schedule(new TimeSpan(0, timeUnit));
        }

        InitialChaosMonkeyEvent monkeys[] = InputParser.monkeys;
        for(InitialChaosMonkeyEvent monkey : monkeys) {
            InitialChaosMonkeyEvent initMonkey = new InitialChaosMonkeyEvent(this,
                    "<b><u>Monkey Event:</u></b> Kill " + monkey.getMicroservice(), showMonkeyEvent, monkey.getTime(),
                    getIdByName(monkey.getMicroservice()), monkey.getInstances());
            initMonkey.schedule(new TimeSpan(0, timeUnit));
        }

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
        serviceCounter      = new HashMap<>();
        serviceTally        = new HashMap<>();
        serviceAccumulate   = new HashMap<>();
        serviceHistogram    = new HashMap<>();
        serviceRegression   = new HashMap<>();
        serviceTimeseries   = new HashMap<>();

        // Load JSON
        MicroserviceEntity[] microservices = InputParser.microservices;
        for(int i = 0; i < microservices.length; i++){
            String serviceName = microservices[i].getName();

            // Queues
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, "Idle Queue: " + serviceName, true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + serviceName, true , true) ;

            // Resources
            //Res microserviceCPU = new Res(this, serviceName + " CPU", microservices[i].getCPU(), true, true);

            // Statistics
            HashMap<String, TimeSeries> timeSeries = new HashMap<>();

            // Collect active instances
            TimeSeries activeInstances = new TimeSeries(this, "Active Instances: " + serviceName,
                    "Report/resources/Instances_" + serviceName + ".txt", new TimeInstant(0.0, timeUnit),
                    new TimeInstant(simulationTime, timeUnit), true, false);
            // Collect active CPU
            TimeSeries activeCPU = new TimeSeries(this, "Used CPU: " + serviceName,
                    "Report/resources/CPU_" + serviceName + ".txt", new TimeInstant(0.0, timeUnit),
                    new TimeInstant(simulationTime, timeUnit), true, false);
            timeSeries.put("Active Instances", activeInstances);
            timeSeries.put("Used CPU", activeCPU);

            //Queue for maxQueue returns refuse and should be used to turn Circuit breakers of with using a waiting queue 1 ( 0 for int max value)
            //Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + microservices[i].getName(), QueueBased.FIFO , 1, true , true);

            for(int y = 0; y < microservices[i].getInstances(); y ++ ){
                MicroserviceEntity msEntity = new MicroserviceEntity(this , microservices[i].getName(), true );
                msEntity.setName(microservices[i].getName());
                msEntity.setId(i);
                msEntity.setCPU(microservices[i].getCPU());
                msEntity.setInstances(microservices[i].getInstances());
                msEntity.setOperations(microservices[i].getOperations());
                idleQueue.insert(msEntity);
                allMicroservices.put(i, msEntity);
            }

            // Queues
            taskQueues.put(i, taskQueue);
            idleQueues.put(i, idleQueue);

            // Resources
            serviceCPU.put(i, microservices[i].getCPU());

            // Statistics
            serviceTimeseries.put(i, timeSeries);
        }
    }

    public static void main(String[] args) {
        InputParser parser = new InputParser("example_basic.json");
        DesmojTest model = new DesmojTest(null, InputParser.simulation.get("model"), true, true);
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
    }
}
