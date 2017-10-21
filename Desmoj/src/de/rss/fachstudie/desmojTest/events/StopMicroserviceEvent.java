package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.MicroserviceThread;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf3Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.HashMap;
import java.util.Map;

public class StopMicroserviceEvent extends EventOf3Entities<MicroserviceEntity, MicroserviceThread, MessageObject> {
    private DesmojTest model;
    private int id;
    private String operation;

    StopMicroserviceEvent(Model owner, String name, Boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(MicroserviceEntity microserviceEntity, MicroserviceThread thread, MessageObject messageObject) {
        for(Operation operation : microserviceEntity.getOperations()) {
            if (operation.getName().equals(this.operation)) {
                // Free the cpu resources the operation has
                if (model.serviceCPU.get(id).get(microserviceEntity.getSid()) + operation.getCPU() <= microserviceEntity.getCPU()) {
                    model.serviceCPU.get(id).put(microserviceEntity.getSid(), model.serviceCPU.get(id).get(microserviceEntity.getSid()) + operation.getCPU());
                } else {
                    // CPU has max resources
                }

                // remove thread from microservice
                microserviceEntity.getThreads().remove(thread);

                // Free stacked and waiting operations
                if(messageObject.getDependency().size() > 0) {

                    HashMap<MicroserviceEntity, StopMicroserviceEvent> stopStackedService = messageObject.removeDependency();
                    Map.Entry<MicroserviceEntity, StopMicroserviceEvent> entry = stopStackedService.entrySet().iterator().next();
                    MicroserviceEntity previousMs = entry.getKey();
                    MicroserviceThread previousThread = new MicroserviceThread(model, "", false);
                    Operation stopOperation = new Operation(model, "", false, false);
                    StopMicroserviceEvent previousStopEvent = entry.getValue();
                    int previousId = previousMs.getId();

                    for(Operation op : previousMs.getOperations()) {
                        if(op.getName().equals(previousStopEvent.getOperation())) {
                            stopOperation = op;
                        }
                    }

                    ContDistUniform timeUntilFinished = new ContDistUniform(model,
                            "Stop Event: " + previousMs.getName() + "(" + stopOperation.getName() + ")",
                            stopOperation.getDuration(), stopOperation.getDuration(), model.getShowStopEvent(), true);

                    // Check if the previous service has enough resources
                    if(model.serviceCPU.get(previousId).get(previousMs.getSid()) >= stopOperation.getCPU()) {
                        model.serviceCPU.get(previousId).put(previousMs.getSid(), model.serviceCPU.get(previousId).get(previousMs.getSid()) - stopOperation.getCPU());
                        previousStopEvent.schedule(previousMs, previousThread, messageObject,
                                new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    } else {
                        // Not enough resources, not reschedule to another time
                        schedule(previousMs, previousThread, messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                    }
                }
                model.idleQueues.get(id).insert(microserviceEntity);
            }
        }
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
