package de.rss.fachstudie.desmojTest.events;

import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.resources.Thread;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.EventOf3Entities;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class StopEvent extends EventOf3Entities<Microservice, Thread, MessageObject> {
    private MainModelClass model;
    private int id;
    private String operation;

    StopEvent(Model owner, String name, Boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (MainModelClass) owner;
    }

    @Override
    public void eventRoutine(Microservice msEntity, Thread thread, MessageObject messageObject) {
        for(Operation operation : msEntity.getOperations()) {
            if (operation.getName().equals(this.operation)) {

                // Remove the message object from the task queue
                model.taskQueues.get(id).remove(messageObject);

                // remove thread from microservice


                // Free stacked and waiting operations
                if (messageObject.getDependency().size() > 0) {

                    Predecessor predecessor = messageObject.removeDependency();
                    Microservice previousMs = predecessor.getEntity();
                    Thread previousThread = predecessor.getThread();
                    int previousId = previousMs.getId();

                    // add thread to cpu
                    previousMs.getThreads().insert(previousThread);
                    model.serviceCPU.get(previousId).get(previousMs.getSid()).addThread(previousThread);
                }
                // Statistics
                // CPU
                model.cpuStatistics.get(id).get(msEntity.getSid()).update(model.serviceCPU.get(id).get(msEntity.getSid()).getUsage());
                // Threads
                model.threadStatistics.get(id).get(msEntity.getSid()).update(model.serviceCPU.get(id).get(msEntity.getSid()).getActiveThreads());
                // Response Time
                model.responseStatisitcs.get(id).get(msEntity.getSid()).update(
                        model.presentTime().getTimeAsDouble() - thread.getCreationTime().getTimeAsDouble());
                // Task Queue
                model.taskQueueStatistics.get(id).update(model.taskQueues.get(id).size());
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
