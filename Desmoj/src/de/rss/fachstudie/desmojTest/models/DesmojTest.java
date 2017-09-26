package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.ErrorMonkeyEvent;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StartMicroserviceEvent;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import desmoj.core.simulator.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class to start the experiment. This class will load the input file and create a model out of it.
 * doInitialSchedules Starts the inital event.
 * init Gets called at the start of the experiment and loads all relevant experiment resources.
 */
public class DesmojTest extends Model {
    public TimeUnit timeUnit = TimeUnit.SECONDS;
    /* Define logging for each event, extend for queue events, maybe refactor TODO */
    public boolean showInitEvent = true;
    public boolean showStartEvent = true;
    public boolean showStopEvent = true;

    public HashMap<Integer,Queue<MicroserviceEntity>>   idleQueues;
    public HashMap<Integer,Queue<MessageObject>>        taskQueues;
    //public HashMap<Integer,StartMicroserviceEvent>      event;
    public HashMap<Integer, MicroserviceEntity>         allMicroservices;

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
        InitialMicroserviceEvent initialEvent = new InitialMicroserviceEvent(this , allMicroservices.get(0).getName(), true, 1, "Microservice", true, 0);
        initialEvent.schedule(new TimeSpan(0, timeUnit));

        //TODO rename to something like InitalEvent
        InitialMicroserviceEvent monkeyEvent = new InitialMicroserviceEvent(this, "ErrorMonkey", true, 1, "ErrorMonkey", false, 0);
        monkeyEvent.schedule(new TimeSpan(0, timeUnit));
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
     * Initialize static model components like distributions and queues.
     */
    @Override
    public void init() {
        MicroserviceEntity[] input = InputParser.createMicroserviceEntities("example_3.json");

        allMicroservices    = new HashMap<>();
        taskQueues          = new HashMap<>();
        idleQueues          = new HashMap<>();


        for(int i = 0; i < input.length; i++){
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, "Idle Queue: " + input[i].getName(), true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + input[i].getName(), true , true) ;

            //Queue for maxQueue returns refuse and should be used to turn Circuit breakers of with using a waiting queue 1 ( 0 for int max value)
            //Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + input[i].getName(), QueueBased.FIFO , 1, true , true);

            for(int y = 0; y < input[i].getInstances(); y ++ ){
                MicroserviceEntity msEntity = new MicroserviceEntity(this , input[i].getName(), true );
                msEntity.setName(input[i].getName());
                msEntity.setId(i);
                msEntity.setInstances(input[i].getInstances());
                msEntity.setThroughput(input[i].getThroughput());
                msEntity.setDependencies(input[i].getDependencies());
                idleQueue.insert(msEntity);
                allMicroservices.put(i, msEntity);
            }

            taskQueues.put(i, taskQueue);
            idleQueues.put(i , idleQueue);
        }
    }

    public static void main(String[] args) {
        DesmojTest model = new DesmojTest(null, "Simple microservice model", true, true);
        Experiment exp = new Experiment("Desmoj_Microservice_Experiment");

        model.connectToExperiment(exp);

        exp.setShowProgressBarAutoclose(true);
        exp.stop(new TimeInstant(1500, TimeUnit.SECONDS));
        exp.tracePeriod(new TimeInstant(0, TimeUnit.SECONDS), new TimeInstant(100, model.timeUnit));
        exp.debugPeriod(new TimeInstant(0, TimeUnit.SECONDS), new TimeInstant(50, model.timeUnit));

        exp.start();

        exp.report();
        exp.finish();

        ExportReport exportReport = new ExportReport(model);
    }
}
