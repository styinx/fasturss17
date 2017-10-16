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

        if(!model.idleQueues.get(id).isEmpty()){
            MicroserviceEntity msEntity = model.idleQueues.get(id).first();
            model.idleQueues.get(id).remove(msEntity);
            model.taskQueues.get(id).remove(messageObject);

            StopMicroserviceEvent msEndEvent = new StopMicroserviceEvent(model,"", model.getShowStopEvent(), id, operation);

            for(Operation op : msEntity.getOperations()) {
                if(op.getName().equals(operation)) {
                    // Provide CPU resources for the operation
                    if(model.serviceCPU.get(id) >= op.getCPU()) {
                        model.serviceCPU.put(id, model.serviceCPU.get(id) - op.getCPU());

                        timeUntilFinished = new ContDistUniform(model,
                                "Stop Event: " + msEntity.getName() + " (" + operation + ")",
                                op.getDuration(), op.getDuration(), true, false);

                        msEndEvent.schedule(msEntity, messageObject, new TimeSpan(timeUntilFinished.sample(), model.getTimeUnit()));
                    } else {
                        schedule(messageObject, new TimeSpan(1.0, model.getTimeUnit()));
                    }

                }
            }
        }
    }
}
