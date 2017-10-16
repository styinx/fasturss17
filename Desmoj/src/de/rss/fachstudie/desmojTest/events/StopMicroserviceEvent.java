package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf2Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.HashMap;
import java.util.SortedMap;
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
    }

    @Override
    public void eventRoutine(MicroserviceEntity microserviceEntity, MessageObject messageObject) {
        for(Operation operation : microserviceEntity.getOperations()) {
            if(operation.getName().equals(this.operation)) {
                // Free the cpu resources the operation has
                if(model.serviceCPU.get(id) + operation.getCPU() <= microserviceEntity.getCPU()) {
                    model.serviceCPU.put(id, model.serviceCPU.get(id) + operation.getCPU());
                } else {
                    // CPU has max resources
                }

                // If there is following operation, then start
                if(operation.getDependencies().length > 0) {
                    for(SortedMap<String, String> dependantOperation : operation.getDependencies()) {
                        String nextOperation = dependantOperation.get("name");
                        String nextService = dependantOperation.get("service");
                        int nextServiceId = model.getIdByName(nextService);

                        ContDistUniform prop = new ContDistUniform(this.model, "",0.0, 1.0,false, false);
                        if(prop.sample() <= operation.getProbability()) {
                            // Immediately start next instance
                            StartMicroserviceEvent nextEvent = new StartMicroserviceEvent(model,
                                    "Start Event: " + nextService + "(" + nextOperation + ")",
                                    model.getShowStartEvent(), nextServiceId, nextOperation);
                            nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                        }
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

            StopMicroserviceEvent repeat = new StopMicroserviceEvent(model,"" ,model.getShowStopEvent(), id, operation);

            for(Operation op : model.allMicroservices.get(id).getOperations()) {
                if(op.getName().equals(operation)) {
                    timeUntilFinished = new ContDistUniform(model ,
                            "Stop Event: " + model.allMicroservices.get(id).getName() + "(" + op.getName() + ")",
                            op.getDuration(), op.getDuration(), true, false);
                }
            }

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
