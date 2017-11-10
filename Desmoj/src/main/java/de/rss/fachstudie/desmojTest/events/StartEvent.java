package de.rss.fachstudie.desmojTest.events;

import co.paralleluniverse.fibers.SuspendExecution;
import de.rss.fachstudie.desmojTest.entities.*;
import de.rss.fachstudie.desmojTest.resources.Thread;
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
                if(model.serviceCPU.get(id).get(instance).getUsage() < min) {
                    min = model.serviceCPU.get(id).get(instance).getUsage();
                    i = instance;
                }
            }
        }
        return model.services.get(id).get(i);
    }

    @Override
    public void eventRoutine(MessageObject messageObject) throws SuspendExecution {

        Operation op = model.allMicroservices.get(id).getOperation(operation);
        boolean hasCircuitBreaker = op.hasPattern("Circuit Breaker");
        int circuitBreakerLimit = model.services.get(id).size() *
                (model.services.get(id).get(0).getCapacity() / model.services.get(id).get(0).getOperation(operation).getDemand());

        if(!hasCircuitBreaker || model.taskQueues.get(id).size() < circuitBreakerLimit) {

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
                StopEvent msEndEvent = new StopEvent(model, "", model.getShowStopEvent(), id, operation);
                Thread thread = new Thread(model, "", false, op.getDemand(), msEndEvent, msEntity, messageObject);

                // Are there dependant operations
                if (op.getDependencies().length > 0) {

                    for (SortedMap<String, String> dependantOperation : op.getDependencies()) {

                        double probability = Double.parseDouble(dependantOperation.get("probability"));
                        // Roll probability
                        ContDistUniform prob = new ContDistUniform(model, "", 0.0, 1.0, false, false);

                        if (prob.sample() <= probability) {

                            String nextOperation = dependantOperation.get("operation");
                            String nextService = dependantOperation.get("service");
                            int nextServiceId = model.getIdByName(nextService);

                            // Add Stacked operation info to message object
                            Predecessor predecessor = new Predecessor(msEntity, thread, msEndEvent);
                            messageObject.addDependency(predecessor);

                            // Immediately start dependant operation
                            StartEvent nextEvent = new StartEvent(model,"", model.getShowStartEvent(), nextServiceId, nextOperation);
                            nextEvent.schedule(messageObject, new TimeSpan(0, model.getTimeUnit()));
                        } else {
                            // add thread to cpu
                            msEntity.getThreads().insert(thread);
                            model.serviceCPU.get(id).get(msEntity.getSid()).addThread(thread);
                        }
                    }
                } else {
                    // add thread to cpu
                    msEntity.getThreads().insert(thread);
                    model.serviceCPU.get(id).get(msEntity.getSid()).addThread(thread);
                }

                // Statistics
                // CPU
                model.cpuStatistics.get(id).get(msEntity.getSid()).update(model.serviceCPU.get(id).get(msEntity.getSid()).getUsage());
                // Thread
                model.threadStatistics.get(id).get(msEntity.getSid()).update(msEntity.getThreads().size());
                // Task Queue
                model.taskQueueStatistics.get(id).update(model.taskQueues.get(id).size());
            }
        }
        else
        {
            // Circuit Breaker
            double last = 0;
            List<Double> values = model.circuitBreakerStatistics.get(id).getDataValues();
            if(values != null)
                last = values.get(values.size() - 1);
            model.circuitBreakerStatistics.get(id).update(last + 1);
        }
    }
}
