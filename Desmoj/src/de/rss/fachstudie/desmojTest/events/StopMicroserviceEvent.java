package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StopMicroserviceEvent extends EventOf2Entities<MicroserviceEntity, MessageObject>{
    private DesmojTest model;
    private ContDistUniform timeUntilFinished;
    private int id;
    private String operation;

    StopMicroserviceEvent(Model owner, String name, Boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (DesmojTest) owner;
        double msThroughput = model.allMicroservices.get(id).getThroughput();
        timeUntilFinished = new ContDistUniform(model , name, msThroughput, msThroughput, true, false);
    }

    @Override
    public void eventRoutine(MicroserviceEntity microserviceEntity, MessageObject messageObject) {
        for(Operation operation : microserviceEntity.getOperations()) {
            if(operation.getName().equals(this.operation)) {
                if(operation.getDependencies().length > 0) {
                    for(HashMap<String, String> dependantOperation : operation.getDependencies()) {
                        //TODO only one instance gets started ???, needs some research
                        String nextOperation = dependantOperation.get("name");
                        String nextService = dependantOperation.get("service");
                        int nextServiceId = model.getIdByName(nextService);
                        StartMicroserviceEvent nextEvent = new StartMicroserviceEvent(model,
                                "Start Event: " + nextService + "(" + nextOperation + ")",
                                model.getShowStartEvent(), nextServiceId, nextOperation);
                        nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                    }
                } else {
                    // TODO go back to the start of the recursive call and insert all services in the idle queue/continue working
                    /*
                    if(!model.taskQueues.get(id).isEmpty()){
                        MessageObject nextMessage =  model.taskQueues.get(id).first();
                        model.taskQueues.get(id).remove(nextMessage);

                        StopMicroserviceEvent repeat = new StopMicroserviceEvent(model,
                                "Stop Event: " + model.allMicroservices.get(id).getName() ,model.getShowStopEvent(), id, operation);
                        repeat.schedule(microserviceEntity , messageObject , new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    } else {
                        model.idleQueues.get(id).insert(microserviceEntity);
                    }*/
                }
            }
        }

        if(!model.taskQueues.get(id).isEmpty()){
            MessageObject nextMessage =  model.taskQueues.get(id).first();
            model.taskQueues.get(id).remove(nextMessage);

            StopMicroserviceEvent repeat = new StopMicroserviceEvent(model,
                    "Stop Event: " + model.allMicroservices.get(id).getName() ,model.getShowStopEvent(), id, operation);
            repeat.schedule(microserviceEntity , messageObject , new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
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
