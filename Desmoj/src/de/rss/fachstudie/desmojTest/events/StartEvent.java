package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.entities.Thread;
import de.rss.fachstudie.desmojTest.models.MainModelClass;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

import java.util.List;
import java.util.SortedMap;

/**
 * This event class gets a working object and schedules a timespan during a microservice is busy.
 */
public class StartEvent extends Event<MessageObject> {
    private MainModelClass model;
    private int id;
    private String operation;

    public StartEvent(Model owner, String name, boolean showInTrace, int id, String operation){
        super(owner, name, showInTrace);

        this.id = id;
        this.operation = operation;
        model = (MainModelClass) owner;
    }

    /**
     * Chooses the service with most resources and space available
     * @param id
     * @return
     */
    private Microservice getServiceEntity(int id) {
        double min = Double.POSITIVE_INFINITY;
        int i = 0;
        for(int instance = 0; instance < model.services.get(id).size(); ++instance) {
            if(!model.services.get(id).get(instance).isKilled()) {
                if(model.services.get(id).get(instance).getThreads().size() < min) {
                    min = model.services.get(id).get(instance).getThreads().size();
                    i = instance;
                }
            }
        }
        return model.services.get(id).get(i);
    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {

        boolean hasCircuitBreaker = false;
        if(model.allMicroservices.get(id).getOperation(operation) != null) {
            for(String pattern : model.allMicroservices.get(id).getOperation(operation).getPatterns()) {
                if(pattern.equals("Circuit Breaker")) {
                    hasCircuitBreaker = true;
                }
            }
        }

        if(!hasCircuitBreaker || model.taskQueues.get(id).size() < model.services.get(id).size()) {

            model.taskQueues.get(id).insert(messageObject);

            boolean availServices = false;
            for(Microservice m : model.services.get(id)) {
                if(!m.isKilled()) {
                    availServices = true;
                    break;
                }
            }

            // Check if there are available services
            if(availServices) {
                // The service with most available resources gets chosen
                Microservice msEntity = getServiceEntity(id);

                StopEvent msEndEvent = new StopEvent(model,
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

                                String nextOperation = dependantOperation.get("operation");
                                String nextService = dependantOperation.get("service");
                                double probability = Double.parseDouble(dependantOperation.get("probability"));
                                int nextServiceId = model.getIdByName(nextService);

                                // Roll probability
                                ContDistUniform prob = new ContDistUniform(model, "", 0.0, 1.0, false, false);
                                if (prob.sample() <= probability) {

                                    // Add Stacked operation info to message object
                                    Thread thread = new Thread(model, "", false);
                                    msEntity.getThreads().insert(thread);
                                    Predecessor predecessor = new Predecessor(msEntity, thread, msEndEvent);
                                    messageObject.addDependency(predecessor);

                                    // Immediately start dependant operation
                                    StartEvent nextEvent = new StartEvent(model,
                                            "Start Event: " + nextService + "(" + nextOperation + ")",
                                            model.getShowStartEvent(), nextServiceId, nextOperation);
                                    nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                                } else {

                                    // The probability of the next operation wasn't achieved, the current operation can start to work
                                    // Provide CPU resources for the operation
                                    if (model.serviceCPU.get(id).get(msEntity.getSid()) >= op.getCPU()) {
                                        model.serviceCPU.get(id).put(msEntity.getSid(), model.serviceCPU.get(id).get(msEntity.getSid()) - op.getCPU());
                                    } else {
                                        // Not enough resources, try it later
                                        schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                                    }
                                    Thread thread = new Thread(model, "", false);
                                    msEntity.getThreads().insert(thread);
                                    msEndEvent.schedule(msEntity, thread, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                                }
                            }
                        } else {
                            // No dependent operations, so the service can work
                            // Provide CPU resources for the operation
                            if (model.serviceCPU.get(id).get(msEntity.getSid()) >= op.getCPU()) {

                                model.serviceCPU.get(id).put(msEntity.getSid(), model.serviceCPU.get(id).get(msEntity.getSid()) - op.getCPU());
                            } else {

                                // Not enough resources, try it later
                                schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                            }
                            Thread thread = new Thread(model, "", false);
                            msEntity.getThreads().insert(thread);
                            msEndEvent.schedule(msEntity, thread, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                        }
                    }
                    // Statistics
                    // CPU
                    model.cpuStatistics.get(id).get(msEntity.getSid()).update(
                            (double)(msEntity.getCPU() - model.serviceCPU.get(id).get(msEntity.getSid()))/msEntity.getCPU());
                    // Thread
                    model.threadStatistics.get(id).get(msEntity.getSid()).update(msEntity.getThreads().size());
                    // Task Queue
                    model.taskQueueStatistics.get(id).update(model.taskQueues.get(id).size());
                }
            }
        }
        else
        {

            double last = 0;
            List<Double> values = model.circuitBreakerStatistics.get(id).getDataValues();
            if(values != null)
                last = values.get(values.size() - 1);
            model.circuitBreakerStatistics.get(id).update(last + 1);
        }
    }
}
