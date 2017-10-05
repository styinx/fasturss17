package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.InitialChaosMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import desmoj.core.advancedModellingFeatures.Res;
import desmoj.core.simulator.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class DesmojTest extends Model {
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private boolean showInitEvent   = true;
    private boolean showStartEvent  = false;
    private boolean showStopEvent   = false;
    private boolean showMonkeyEvent = true;

    public HashMap<Integer, Queue<MicroserviceEntity>>   idleQueues;
    public HashMap<Integer, Queue<MessageObject>>        taskQueues;
    public HashMap<Integer, MicroserviceEntity>          allMicroservices;

    public HashMap<Integer, Res> serviceCPU;

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
        InitialMicroserviceEvent generators[] = InputParser.generators;
        for(InitialMicroserviceEvent generator : generators) {
            InitialMicroserviceEvent initEvent = new InitialMicroserviceEvent(this,
                    "Inital Event: " + generator.getMicroservice(), showInitEvent, generator.getTime(),
                    getIdByName(generator.getMicroservice()));
            initEvent.schedule(new TimeSpan(0, timeUnit));
        }

        InitialChaosMonkeyEvent monkeys[] = InputParser.monkeys;
        for(InitialChaosMonkeyEvent monkey : monkeys) {
            InitialChaosMonkeyEvent initMonkey = new InitialChaosMonkeyEvent(this,
                    "ChaosMonkey Event: Kill" + monkey.getMicroservice(), showMonkeyEvent, monkey.getTime(),
                    getIdByName(monkey.getMicroservice()), monkey.getInstances());
            initMonkey.schedule(new TimeSpan(0, timeUnit));
        }
    }

    /**
     * Initialize static model components like distributions and queues.
     */
    @Override
    public void init() {
        allMicroservices    = new HashMap<>();
        taskQueues          = new HashMap<>();
        idleQueues          = new HashMap<>();

        serviceCPU          = new HashMap<>();


        MicroserviceEntity[] microservices = InputParser.microservices;
        for(int i = 0; i < microservices.length; i++){
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, "Idle Queue: " + microservices[i].getName(), true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + microservices[i].getName(), true , true) ;
            Res microserviceCPU = new Res(this, microservices[i].getName() + " CPU", microservices[i].getCPU(), true, true);

            serviceCPU.put(i, microserviceCPU);

            //Queue for maxQueue returns refuse and should be used to turn Circuit breakers of with using a waiting queue 1 ( 0 for int max value)
            //Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + microservices[i].getName(), QueueBased.FIFO , 1, true , true);

            for(int y = 0; y < microservices[i].getInstances(); y ++ ){
                MicroserviceEntity msEntity = new MicroserviceEntity(this , microservices[i].getName(), true );
                msEntity.setName(microservices[i].getName());
                msEntity.setId(i);
                msEntity.setInstances(microservices[i].getInstances());
                msEntity.setOperations(microservices[i].getOperations());
                msEntity.setThroughput(microservices[i].getThroughput());
                msEntity.setDependencies(microservices[i].getDependencies());
                idleQueue.insert(msEntity);
                allMicroservices.put(i, msEntity);
            }

            taskQueues.put(i, taskQueue);
            idleQueues.put(i , idleQueue);
        }
    }

    public static void main(String[] args) {
        InputParser parser = new InputParser("example_3.json");
        DesmojTest model = new DesmojTest(null, "Simple microservice model", true, true);
        Experiment exp = new Experiment("Desmoj_Microservice_Experiment");

        model.connectToExperiment(exp);

        exp.setShowProgressBarAutoclose(true);
        exp.stop(new TimeInstant(1500, model.getTimeUnit()));
        exp.tracePeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(100, model.getTimeUnit()));
        exp.debugPeriod(new TimeInstant(0, model.getTimeUnit()), new TimeInstant(50, model.getTimeUnit()));

        exp.start();

        exp.report();
        exp.finish();

        ExportReport exportReport = new ExportReport(model);
    }
}
