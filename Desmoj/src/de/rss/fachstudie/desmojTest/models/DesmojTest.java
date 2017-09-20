package de.rss.fachstudie.desmojTest.models;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.events.InitialMicroserviceEvent;
import de.rss.fachstudie.desmojTest.events.StartMicroserviceEvent;
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
        String[] allServices = new String[5];
        allServices[0] = "Micro0";
        allServices[1] = "Micro1";
        allServices[2] = "Micro2";
        allServices[3] = "Micro3";
        allServices[4] = "Micro4";

        //bekommen json file

        allMicroservices = new HashMap<Integer, MicroserviceEntity>();
        event = new HashMap<Integer, StartMicroserviceEvent>();
        taskQueues = new HashMap<Integer, Queue<MessageObject>>();
        idleQueues = new HashMap<Integer, Queue<MicroserviceEntity>>();


        for(int i = 0; i < 5; i++){
//
            MicroserviceEntity msEntity = new MicroserviceEntity(this , allServices[i], true );

            msEntity.setName(allServices[i]);
            msEntity.setId(i);
            if( i != 4){
                msEntity.setNextMicroservice("Micro" + (i + 1));
            }
            Queue<MicroserviceEntity> idleQueue = new Queue<MicroserviceEntity>(this, allServices[i] + "Idle", true, true);
            Queue<MessageObject> taskQueue = new Queue<MessageObject>(this, allServices[i] + "Working", true , true) ;

            for(int j = 0 ; j < msEntity.getNumberOfInstances() ; j++ ){
                idleQueue.insert(msEntity);
            }

            StartMicroserviceEvent startEvent = new StartMicroserviceEvent(this, allServices[i] + "Start", true, i);


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
