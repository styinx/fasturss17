package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StartMicroserviceEvent;
import de.rss.fachstudie.desmojTest.utils.InputParser;
import desmoj.core.simulator.*;
import desmoj.core.dist.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DesmojTest extends Model {


    private ContDistExponential requestArrivalTime;


    public HashMap<Integer,Queue<MicroserviceEntity>> idleQueues;
    public HashMap<Integer,Queue<MessageObject>> taskQueues;
    //public Queue<MessageObject> taskQueue;
    public HashMap<Integer,StartMicroserviceEvent> event;
    public HashMap<Integer, MicroserviceEntity> allMicroservices ;

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
        InitialMicroserviceEvent initialEvent = new InitialMicroserviceEvent(this , allMicroservices.get(0).getName() , true );
        initialEvent.schedule(new TimeSpan(0));


    }

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
        //String[] input[i].getName() = new String[5];
        //input[i].getName()[0] = "Micro0";
        //input[i].getName()[1] = "Micro1";
        //input[i].getName()[2] = "Micro2";
        //input[i].getName()[3] = "Micro3";
        //input[i].getName()[4] = "Micro4";

        //bekommen json file
        MicroserviceEntity[] input = InputParser.createMicroserviceEntities("input.json");

        allMicroservices = new HashMap<Integer, MicroserviceEntity>();
        event = new HashMap<Integer, StartMicroserviceEvent>();
        taskQueues = new HashMap<Integer, Queue<MessageObject>>();
        idleQueues = new HashMap<Integer, Queue<MicroserviceEntity>>();


        for(int i = 0; i < input.length; i++){
//
            MicroserviceEntity msEntity = new MicroserviceEntity(this , input[i].getName(), true );

            msEntity.setName(input[i].getName());
            msEntity.setId(i);
            msEntity.setInstances(input[i].getInstances());
            //in case last Microservice does not have a next Microservice
            if(i != input.length-1){
                msEntity.setNextMicroservice(input[i].getNextMicroservice());
            }
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, input[i].getName() + "Idle", true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, input[i].getName() + "Working", true , true) ;


            StartMicroserviceEvent startEvent = new StartMicroserviceEvent(this, input[i].getName() + "Start", true, i);

            for(int y = 0; y < input[i].getInstances(); y ++ ){
                idleQueue.insert(msEntity);
            }

            allMicroservices.put(i, msEntity);
            event.put(i,startEvent);
            taskQueues.put(i, taskQueue);
            idleQueues.put(i , idleQueue);
        }


    }

    public static void main(String[] args) {



        DesmojTest model = new DesmojTest(null, "Simple microservice model", true, true);
        Experiment exp = new Experiment("DesmojMicroserviceExperiment");

        model.connectToExperiment(exp);

        exp.setShowProgressBarAutoclose(true);
        exp.stop(new TimeInstant(1500, TimeUnit.SECONDS));
        exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.SECONDS));
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50, TimeUnit.SECONDS));

        exp.start();

        exp.report();
        exp.finish();
    }
}
