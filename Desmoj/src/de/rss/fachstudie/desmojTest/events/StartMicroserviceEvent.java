package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.MessageObject;
import de.rss.fachstudie.desmojTest.entities.MicroserviceEntity;
import de.rss.fachstudie.desmojTest.entities.Operation;
import de.rss.fachstudie.desmojTest.models.DesmojTest;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * This event class gets a working object and schedules a timespan during a microservice is busy.
 */
public class StartMicroserviceEvent extends Event<MessageObject> {
    private DesmojTest model;
    private ContDistUniform timeUntilFinished;
    private int id;
    private String operation;

    public StartMicroserviceEvent(Model owner, String name, boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (DesmojTest) owner;
    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {
        model.taskQueues.get(id).insert(messageObject);

        if (!model.idleQueues.get(id).isEmpty()) {
            MicroserviceEntity msEntity = model.idleQueues.get(id).first();
            model.idleQueues.get(id).remove(msEntity);
            model.taskQueues.get(id).remove(messageObject);

            StopMicroserviceEvent msEndEvent = new StopMicroserviceEvent(model,
                    "Stop Event: " + msEntity.getName() + "(" + operation + ")",
                    model.getShowStopEvent(), id, operation);

            for (Operation op : msEntity.getOperations()) {
                if (op.getName().equals(operation)) {
                    ContDistUniform timeUntilFinished = new ContDistUniform(model,
                            "Start Event: " + msEntity.getName() + " (" + operation + ")",
                            op.getDuration(), op.getDuration(), model.getShowStartEvent(), true);

                    // Are there dependant operations
                    if (op.getDependencies().length > 0) {
                        for (SortedMap<String, String> dependantOperation : op.getDependencies()) {
                            String nextOperation = dependantOperation.get("name");
                            String nextService = dependantOperation.get("service");
                            int nextServiceId = model.getIdByName(nextService);

                            ContDistUniform prop = new ContDistUniform(this.model, "", 0.0, 1.0, false, false);
                            // Next dependant operation gets executed
                            if (prop.sample() <= op.getProbability()) {
                                // Add Stacked operation info to message object
                                HashMap<MicroserviceEntity, StopMicroserviceEvent> stackedOperation = new HashMap<>();
                                stackedOperation.put(msEntity, msEndEvent);
                                messageObject.addDependency(stackedOperation);

                                // Immediately start next instance
                                StartMicroserviceEvent nextEvent = new StartMicroserviceEvent(model,
                                        "Start Event: " + nextService + "(" + nextOperation + ")",
                                        model.getShowStartEvent(), nextServiceId, nextOperation);
                                nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                            } else {
                                msEndEvent.schedule(msEntity, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                            }
                        }
                    } else {
                        // No Dependencies are need, so the service can work
                        // Provide CPU resources for the operation
                        if (model.serviceCPU.get(id) >= op.getCPU()) {
                            model.serviceCPU.put(id, model.serviceCPU.get(id) - op.getCPU());
                        } else {
                            schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                        }
                        msEndEvent.schedule(msEntity, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    }
                }
            }
        }
    }
}
