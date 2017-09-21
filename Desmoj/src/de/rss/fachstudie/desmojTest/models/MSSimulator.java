package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StartMicroserviceEvent;
import de.rss.fachstudie.desmojTest.export.DataChart;
import de.rss.fachstudie.desmojTest.export.DependecyGraph;
import de.rss.fachstudie.desmojTest.export.ExportReport;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import desmoj.core.simulator.*;

import java.sql.Time;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MSSimulator extends Model {
    public HashMap<Integer,Queue<MicroserviceEntity>>   idleQueues;
    public HashMap<Integer,Queue<MessageObject>>        taskQueues;
    public HashMap<Integer,StartMicroserviceEvent>      event;
    public HashMap<Integer, MicroserviceEntity>         allMicroservices ;

    public MSSimulator(Model owner, String modelName, boolean showInReport, boolean showInTrace) {
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
        InitialMicroserviceEvent initialEvent = new InitialMicroserviceEvent(this , allMicroservices.get(0).getName() , true, 2);
        initialEvent.schedule(new TimeSpan(0, TimeUnit.SECONDS));
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
        MicroserviceEntity[] input = InputParser.createMicroserviceEntities("example_2.json");

        allMicroservices    = new HashMap<Integer, MicroserviceEntity>();
        event               = new HashMap<Integer, StartMicroserviceEvent>();
        taskQueues          = new HashMap<Integer, Queue<MessageObject>>();
        idleQueues          = new HashMap<Integer, Queue<MicroserviceEntity>>();


        for(int i = 0; i < input.length; i++){
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, "Idle Queue: " + input[i].getName(), true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, "Task Queue: " + input[i].getName(), true , true) ;

            for(int y = 0; y < input[i].getInstances(); y ++ ){
                MicroserviceEntity msEntity = new MicroserviceEntity(this , input[i].getName(), true );
                msEntity.setName(input[i].getName());
                msEntity.setId(i);
                msEntity.setInstances(input[i].getInstances());
                msEntity.setThroughput(input[i].getThroughput());
                msEntity.setNextMicroservice(input[i].getNextMicroservice());
                msEntity.setDependencies(input[i].getDependencies());
                idleQueue.insert(msEntity);
                allMicroservices.put(i, msEntity);
            }

            StartMicroserviceEvent startEvent = new StartMicroserviceEvent(this, "Start Event: " + input[i].getName(), true, i);
            event.put(i,startEvent);
            taskQueues.put(i, taskQueue);
            idleQueues.put(i , idleQueue);
        }
    }

    public static void main(String[] args) {
        MSSimulator model = new MSSimulator(null, "Simple microservice model", true, true);
        Experiment exp = new Experiment("Desmoj_Microservice_Experiment");

        model.connectToExperiment(exp);

        exp.setShowProgressBarAutoclose(true);
        exp.stop(new TimeInstant(1500, TimeUnit.SECONDS));
        exp.tracePeriod(new TimeInstant(0, TimeUnit.SECONDS), new TimeInstant(100, TimeUnit.SECONDS));
        exp.debugPeriod(new TimeInstant(0, TimeUnit.SECONDS), new TimeInstant(50, TimeUnit.SECONDS));

        exp.start();

        exp.report();
        exp.finish();

        ExportReport exportReport = new ExportReport(model);
    }
}
