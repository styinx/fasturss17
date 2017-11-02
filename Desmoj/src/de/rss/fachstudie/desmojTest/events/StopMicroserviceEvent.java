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
    public void eventRoutine(MicroserviceEntity msEntity, MicroserviceThread thread, MessageObject messageObject) {
        for(Operation operation : msEntity.getOperations()) {
            if (operation.getName().equals(this.operation)) {

                // Remove the message object from the task queue
                model.taskQueues.get(id).remove(messageObject);
                // Free the cpu resources the operation has
                if (model.serviceCPU.get(id).get(msEntity.getSid()) + operation.getCPU() <= msEntity.getCPU()) {

                    model.serviceCPU.get(id).put(msEntity.getSid(), model.serviceCPU.get(id).get(msEntity.getSid()) + operation.getCPU());
                }
                // Save response time
                msEntity.addResponseTime(thread.getCreationTime().getTimeAsDouble());
                // remove thread from microservice
                msEntity.getThreads().remove(thread);

                // Free stacked and waiting operations
                if (messageObject.getDependency().size() > 0) {

                    Predecessor predecessor = messageObject.removeDependency();
                    MicroserviceEntity previousMs = predecessor.getEntity();
                    MicroserviceThread previousThread = predecessor.getThread();
                    StopMicroserviceEvent previousStopEvent = predecessor.getStopEvent();
                    Operation stopOperation = new Operation(model, "", false);
                    int previousId = previousMs.getId();

                    for (Operation op : previousMs.getOperations()) {
                        if (op.getName().equals(previousStopEvent.getOperation())) {
                            stopOperation = op;
                        }
                    }

                    ContDistUniform timeUntilFinished = new ContDistUniform(model,
                            "Stop Event: " + previousMs.getName() + "(" + stopOperation.getName() + ")",
                            stopOperation.getDuration(), stopOperation.getDuration(), model.getShowStopEvent(), true);

                    // Check if the previous service has enough resources
                    if (model.serviceCPU.get(previousId).get(previousMs.getSid()) >= stopOperation.getCPU()) {

                        model.serviceCPU.get(previousId).put(previousMs.getSid(), model.serviceCPU.get(previousId).get(previousMs.getSid()) - stopOperation.getCPU());
                        previousStopEvent.schedule(previousMs, previousThread, messageObject,
                                new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    } else {

                        // Not enough resources, not reschedule to another time
                        schedule(previousMs, previousThread, messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                    }
                }
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
