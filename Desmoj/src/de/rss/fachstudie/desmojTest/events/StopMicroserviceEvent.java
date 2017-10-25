package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf3Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class StopMicroserviceEvent extends EventOf3Entities<MicroserviceEntity, MicroserviceThread, MessageObject> {
    private MainModelClass model;
    private int id;
    private String operation;

    StopMicroserviceEvent(Model owner, String name, Boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (MainModelClass) owner;
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
                // Save response time
                microserviceEntity.addResponseTime(thread.getCreationTime().getTimeAsDouble());
                // remove thread from microservice
                microserviceEntity.getThreads().remove(thread);

                // Free stacked and waiting operations
                if(messageObject.getDependency().size() > 0) {

                    Predecessor predecessor = messageObject.removeDependency();
                    MicroserviceEntity previousMs = predecessor.getEntity();
                    MicroserviceThread previousThread = predecessor.getThread();
                    StopMicroserviceEvent previousStopEvent = predecessor.getStopEvent();
                    Operation stopOperation = new Operation(model, "", false);
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
