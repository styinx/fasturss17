package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.concurrent.TimeUnit;

public class StopMicroserviceEvent extends EventOf2Entities<MicroserviceEntity, MessageObject>{
    private DesmojTest model;
    private ContDistUniform timeUntilFinished;
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    StopMicroserviceEvent(Model owner, String name, Boolean showInTrace, int id){
        super(owner, name, showInTrace);
        setId(id);
        model = (DesmojTest) owner;
        timeUntilFinished = new ContDistUniform(model , name, 4,4, true, false);

    }

    @Override
    public void eventRoutine(MicroserviceEntity microserviceEntity, MessageObject messageObject) {

        //in case there is a next microservice
        if(!microserviceEntity.getNextMicroservice().isEmpty()){
            StartMicroserviceEvent nextEvent = model.event.get(model.getIdByName(microserviceEntity.getNextMicroservice()));

            nextEvent.schedule(messageObject, new TimeSpan(0.0, TimeUnit.SECONDS));
        }

        if(!model.taskQueues.get(id).isEmpty()){
            //neuen task nehmen
            MessageObject nextMessageObjectToworkOn =  model.taskQueues.get(id).first();
            model.taskQueues.get(id).remove(nextMessageObjectToworkOn);


            StopMicroserviceEvent repeat = new StopMicroserviceEvent(model , model.allMicroservices.get(id).getName() + "Stopp" ,true, id );
            repeat.schedule(microserviceEntity , messageObject , new TimeSpan(timeUntilFinished.sample(), TimeUnit.SECONDS));


        } else {
            //gehe in die idle queue
            model.idleQueues.get(id).insert(microserviceEntity);

        }


    }



}
