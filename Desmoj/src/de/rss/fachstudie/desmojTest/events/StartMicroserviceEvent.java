package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.SortedMap;

/**
 * This event class gets a working object and schedules a timespan during a microservice is busy.
 */
public class StartMicroserviceEvent extends Event<MessageObject> {
    private MainModelClass model;
    private int id;
    private String operation;

    public StartMicroserviceEvent(Model owner, String name, boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (MainModelClass) owner;
    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {
        model.taskQueues.get(id).insert(messageObject);

        boolean availServices = false;

        for(MicroserviceEntity m : model.idleQueues.get(id)) {
            if(!m.isKilled()) {
                availServices = true;
                break;
            }
        }

        if(availServices){

            model.taskQueues.get(id).remove(messageObject);
            // The service with most available resources gets returned
            MicroserviceEntity msEntity = model.getServiceEntity(id);

            StopMicroserviceEvent msEndEvent = new StopMicroserviceEvent(model,
                    "Stop Event: " + msEntity.getName() + "(" + operation + ")",
                    model.getShowStopEvent(), id, operation);

            for(Operation op : msEntity.getOperations()) {
                if(op.getName().equals(operation)) {
                    ContDistUniform timeUntilFinished = new ContDistUniform(model,
                            "Start Event: " + msEntity.getName() + " (" + operation + ")",
                            op.getDuration(), op.getDuration(), model.getShowStartEvent(), true);

                    // Are there dependant operations
                    if(op.getDependencies().length > 0) {

                        for(SortedMap<String, String> dependantOperation : op.getDependencies()) {

                            String nextOperation = dependantOperation.get("operation");
                            String nextService = dependantOperation.get("service");
                            double probability = Double.parseDouble(dependantOperation.get("probability"));
                            int nextServiceId = model.getIdByName(nextService);

                            // Roll probability
                            ContDistUniform prob = new ContDistUniform(model,"",0.0, 1.0,false, false);
                            if(prob.sample() <= probability) {

                                // Add Stacked operation info to message object
                                MicroserviceThread thread = new MicroserviceThread(model, "", false);
                                msEntity.getThreads().insert(thread);
                                Predecessor predecessor = new Predecessor(msEntity, thread, msEndEvent);
                                messageObject.addDependency(predecessor);

                                // Immediately start dependant operation
                                StartMicroserviceEvent nextEvent = new StartMicroserviceEvent(model,
                                        "Start Event: " + nextService + "(" + nextOperation + ")",
                                        model.getShowStartEvent(), nextServiceId, nextOperation);
                                nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                            } else {

                                // The probability of the next operation wasn't achieved, the current operation can start to work
                                // Provide CPU resources for the operation
                                if(model.serviceCPU.get(id).get(msEntity.getSid()) >= op.getCPU()) {
                                    model.serviceCPU.get(id).put(msEntity.getSid(), model.serviceCPU.get(id).get(msEntity.getSid()) - op.getCPU());
                                } else {
                                    // Not enough resources, try it later
                                    // TODO: try it later or kick out???
                                    schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                                }
                                MicroserviceThread thread = new MicroserviceThread(model, "", false);
                                msEntity.getThreads().insert(thread);
                                msEndEvent.schedule(msEntity, thread, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                            }
                        }
                    } else {
                        // No dependent operations, so the service can work
                        // Provide CPU resources for the operation
                        if(model.serviceCPU.get(id).get(msEntity.getSid()) >= op.getCPU()) {

                            model.serviceCPU.get(id).put(msEntity.getSid(), model.serviceCPU.get(id).get(msEntity.getSid()) - op.getCPU());
                        } else {

                            // Not enough resources, try it later
                            // TODO: try it later or kick out???
                            schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                        }
                        MicroserviceThread thread = new MicroserviceThread(model, "", false);
                        msEntity.getThreads().insert(thread);
                        msEndEvent.schedule(msEntity, thread, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    }
                }
            }
        }
    }
}
