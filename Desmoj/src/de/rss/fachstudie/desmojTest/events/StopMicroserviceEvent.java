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
import java.util.Map;
import java.util.Set;
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
            if (operation.getName().equals(this.operation)) {
                // Free the cpu resources the operation has
                if (model.serviceCPU.get(id) + operation.getCPU() <= microserviceEntity.getCPU()) {
                    model.serviceCPU.put(id, model.serviceCPU.get(id) + operation.getCPU());
                } else {
                    // CPU has max resources
                }

                // Free stacked and waiting operations
                if(messageObject.getDependency().size() > 0) {


                    HashMap<MicroserviceEntity, StopMicroserviceEvent> stopStackedService = messageObject.removeDependency();
                    Map.Entry<MicroserviceEntity, StopMicroserviceEvent> entry = stopStackedService.entrySet().iterator().next();
                    MicroserviceEntity previousMs = entry.getKey();
                    Operation stopOperation = null;
                    StopMicroserviceEvent previousStopEvent = entry.getValue();
                    int previousId = previousMs.getId();

                    for(Operation op : previousMs.getOperations()) {
                        if(op.getName().equals(previousStopEvent.getOperation())) {
                            stopOperation = op;
                        }
                    }

                    timeUntilFinished = new ContDistUniform(model,
                            "Stop Event: " + previousMs.getName() + "(" + stopOperation.getName() + ")",
                            stopOperation.getDuration(), stopOperation.getDuration(), model.getShowStopEvent(), true);

                    // Check if the previous service has enough resources
                    if(model.serviceCPU.get(previousId) >= stopOperation.getCPU()) {
                        model.serviceCPU.put(previousId, model.serviceCPU.get(previousId) - stopOperation.getCPU());
                        previousStopEvent.schedule(previousMs, messageObject,
                                new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    } else {
                        // If not reschedule to another time
                        schedule(previousMs, messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                    }
                }
                model.idleQueues.get(id).insert(microserviceEntity);
            }
        }

        /*

        Old Operations, non stacked

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

                        ContDistUniform prop = new ContDistUniform(this.model,"",0.0, 1.0,false, false);
                        if(prop.sample() <= operation.getProbability()) {
                            // Immediately start next instance
                            StartMicroserviceEvent nextEvent = new StartMicroserviceEvent(model, "",
                                    model.getShowStartEvent(), nextServiceId, nextOperation);
                            messageObject.addDependency();
                            nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                        }
                    }
                } else {

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
                            op.getDuration(), op.getDuration(), model.getShowStopEvent(), true);
                }
            }

            repeat.schedule(microserviceEntity , messageObject , new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
        } else {
            model.idleQueues.get(id).insert(microserviceEntity);
        }*/
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
