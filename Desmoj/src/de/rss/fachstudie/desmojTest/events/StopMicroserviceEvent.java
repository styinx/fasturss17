package de.rss.fachstudie.desmojTest.events;

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

    StopMicroserviceEvent(Model owner, String name, Boolean showInTrace, int id){
        super(owner, name, showInTrace);

        setId(id);
        model = (DesmojTest) owner;
        double msThroughput = model.allMicroservices.get(id).getThroughput();
        timeUntilFinished = new ContDistUniform(model , name, msThroughput, msThroughput, true, false);
    }

    @Override
    public void eventRoutine(MicroserviceEntity microserviceEntity, MessageObject messageObject) {
        if(!microserviceEntity.getNextMicroservice().isEmpty()){
            StartMicroserviceEvent nextEvent = model.event.get(model.getIdByName(microserviceEntity.getNextMicroservice()));
            nextEvent.schedule(messageObject, new TimeSpan(0, TimeUnit.SECONDS));
        }

        if(!model.taskQueues.get(id).isEmpty()){
            MessageObject nextMessage =  model.taskQueues.get(id).first();
            model.taskQueues.get(id).remove(nextMessage);

            StopMicroserviceEvent repeat = new StopMicroserviceEvent(model, "Start Event: " + model.allMicroservices.get(id).getName() ,true, id );
            repeat.schedule(microserviceEntity , messageObject , new TimeSpan(timeUntilFinished.sample(), TimeUnit.SECONDS));
        } else {
            model.idleQueues.get(id).insert(microserviceEntity);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
